package com.example.rs_link.core.service

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.openlocationcode.OpenLocationCode // The library
import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.rs_link.R
import com.example.rs_link.data.model.Contact
import com.example.rs_link.data.repository.UserRepository
import com.example.rs_link.feature_dashboard.DashboardActivity
import com.example.rs_link.feature_dashboard.home.BluetoothConstants
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class BluetoothLeService : Service() {
    // 1. The Connection now lives here
    private var bluetoothGatt: BluetoothGatt? = null
    @Inject
    lateinit var userRepository: UserRepository

    // Scope for background database/repository operations
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }
    private val gattCallback = object : BluetoothGattCallback() {

        // A. Connection Change (Connected / Disconnected)
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // CRITICAL: You must discover services immediately after connecting
                Log.d("BluetoothService", "Connected to GATT server.")
                gatt.discoverServices()
                serviceScope.launch {
                    userRepository.updateConnectionStatus("Connected")
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("BluetoothService", "Disconnected from GATT server.")

                serviceScope.launch {
                    userRepository.updateConnectionStatus("Disconnected")
                }
                gatt.close()
                bluetoothGatt = null
            }
        }

        // B. Services Discovered (Map of Features Loaded)
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                enableNotifications(gatt)
            }
        }

        // C. Data Received (Where your sensor values arrive!)
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray // For newer Android versions
        ) {
            // Parse the data (e.g., String or Int)
            val message = String(value, Charsets.UTF_8)
            Log.d("BluetoothData", "Received: $message")

            // B. Upd   ate the UI State
            serviceScope.launch {
                userRepository.updateSensorData(message)
                // For now, if you haven't added that function yet, just Log it:
                Log.i("BluetoothService", "Data to Repo: $message")
            }
            if (message.contains("Manual Alert", ignoreCase = true)) {
                Log.w("Bluetooth", "Crash Detected - Sending Notification")
                sendAlertNotification("Manual Crash Activated! Check status.")
                sendEmergencySms(message)
            }
//            else if (message.contains("CRASH_DETECTED", ignoreCase = true)){
//                sendAlertNotification("Accident Detected has been Confirmed! Send Help!")
//                sendEmergencySms(message)

//            }
            else if (message.contains("Crash Confirmed", ignoreCase = true)){
                sendAlertNotification("Accident Detected has been Confirmed! Send Help!")
                sendEmergencySms(message)
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createAlertNotificationChannel()
        // Inside onStartCommand

        val action = intent?.action

        // 1. Start the Foreground Notification immediately
        if (action == "START_SERVICE") {
            startForegroundService()

            // Get device from Intent extras and connect
            val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent?.getParcelableExtra("DEVICE", BluetoothDevice::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent?.getParcelableExtra("DEVICE")
            }
            // Safe Call to connect
            if (device != null) {
                connectToDevice(device)
            }
        }

        // 2. Stop command
        if (action == "STOP_SERVICE") {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }

        // START_STICKY means "If the system kills me, restart me automatically"
        return START_STICKY
    }

    private fun startForegroundService() {
        val channelId = "bluetooth_service_channel"
        val channelName = "RS-Link Connection"

        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_LOW // Low importance = no sound/popup
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("RS-Link Active")
            .setContentText("Connected to device. monitoring sensors...")
            .setSmallIcon(R.drawable.rs_link_logo) // Use your icon
            .setOngoing(true) // Cannot be swiped away
            .build()

        // ID must be > 0
        startForeground(1, notification)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun connectToDevice(device: BluetoothDevice) {
        // ... Your connectGatt logic here ...
        bluetoothGatt = device.connectGatt(this, true, gattCallback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onDestroy() {
        super.onDestroy()
        try {
            bluetoothGatt?.close()
            bluetoothGatt = null
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
        serviceScope.launch {
            userRepository.updateConnectionStatus("Disconnected")
            Log.d("BluetoothService", "Service Destroyed. Status set to Disconnected.")
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // We are using a started service, binding is optional
    }

    // 3. ENABLE NOTIFICATIONS (The "Subscribe" Logic)
    @SuppressLint("MissingPermission")
    private fun enableNotifications(gatt: BluetoothGatt) {
        val serviceUuid = UUID.fromString(BluetoothConstants.SERVICE_UUID)
        val charUuid = UUID.fromString(BluetoothConstants.CHARACTERISTIC_UUID)

        val service = gatt.getService(serviceUuid)
        val characteristic = service?.getCharacteristic(charUuid)

        if (characteristic != null) {
            // 1. Enable locally in Android
            gatt.setCharacteristicNotification(characteristic, true)

            // 2. Enable remotely on ESP32 (Write to Descriptor)
            val descriptor = characteristic.getDescriptor(
                UUID.fromString(BluetoothConstants.CLIENT_CONFIG_DESCRIPTOR)
            )
            // Use ENABLE_NOTIFICATION_VALUE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            } else {
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)
            }
        }
    }

    private fun createAlertNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "rs_link_alerts"
            val channelName = "RS-Link Alerts"
            // IMPORTANCE_HIGH makes it make a sound and pop up on screen
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Notifications for crash detection and critical events"
                enableVibration(true)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    @SuppressLint("MissingPermission")
    private fun sendAlertNotification(message: String) {
        // 1. Create an Intent to open the Dashboard when clicked
        val intent = Intent(this, DashboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE // Required for Android 12+
        )

        // 2. Build the Notification
        val notification = NotificationCompat.Builder(this, "rs_link_alerts")
            .setSmallIcon(R.drawable.rs_link_logo) // Your Icon
            .setContentTitle("RS-Link Alert")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // For older Android versions
            .setContentIntent(pendingIntent) // Attach the click action
            .setAutoCancel(true) // Remove notification when clicked
            .build()

        // 3. Show it
        val notificationManager = getSystemService(NotificationManager::class.java)
        // Use a unique ID (e.g., System.currentTimeMillis().toInt()) if you want multiple separate notifications.
        // Use a fixed ID (e.g., 2) if you want to update the existing one.
        notificationManager.notify(2, notification)
    }
    // 3. The Sending Logic
    private fun sendEmergencySms(crashData: String) {
        serviceScope.launch {
            // 1. Get User ID
            val uid = userRepository.getCurrentUserId() ?: return@launch

            // 1. Get Contacts
            val contacts = userRepository.getEmergencyContacts().first()
            if (contacts.isEmpty()) return@launch

            // --- 1. DEFAULT MESSAGE (NO GPS) ---
            var smsBody = "SOS! RS-Link detected crash: $crashData. GPS Unavailable."

            // --- 2. CHECK PERMISSION SAFELY ---
            val hasLocationPermission = ContextCompat.checkSelfPermission(
                this@BluetoothLeService,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            // --- 3. IF GRANTED, TRY TO GET LOCATION ---
            if (hasLocationPermission) {
                try {
                    // 3. FETCH USER NAME (This works offline if Firestore cache is on)
                    val userProfile = userRepository.getUserProfile(uid)
                    val myName = userProfile?.firstName ?: "RS-Link User" // Fallback if name fails

                    // 1. GET CURRENT TIME
                    // Format: "2:30 PM" or "14:30" depending on user locale
                    val currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())

                    val location = fusedLocationClient.lastLocation.await()
                        ?: fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()

                    if (location != null) {
                        val plusCode = OpenLocationCode.encode(location.latitude, location.longitude, 10)
                        // Update message with GPS data
                        if(crashData.contains("Manual Alert", ignoreCase = true)){
                            smsBody = "SOS! $myName just activated the manual alert at $currentTime.\n" + "Location Code: $plusCode\n" +
                                    "Search this code on Maps. Contact him to update his status"
                        }
                        else if(crashData.contains("Crash Confirmed", ignoreCase = true)){
                            smsBody = "SOS! RS-Link detected $myName had a crash at $currentTime.\n" + "Location Code: $plusCode\n" +
                                    "Search this code on Maps."
                        }
//                        else if(crashData.contains("Confirmed Accident", ignoreCase = true)){
//                            smsBody = "RS-Link detected $myName had a crash at $currentTime.\n" + "Location Code: $plusCode\n" +
//                                    "Search this code on Maps."
//                        }

                    }
                    else{
                        smsBody = "SOS! RS-Link detected $myName had a crash at $currentTime.. GPS Unavailable."
                    }
                } catch (e: Exception) {
                    Log.e("BluetoothService", "Location Error: ${e.message}")
                    // Fallback to the default "GPS Unavailable" message
                }
            } else {
                Log.w("BluetoothService", "Cannot attach location: Permission missing.")
            }

            // --- 4. SEND THE SMS ---
            sendToContacts(contacts, smsBody)
        }
    }

    private fun sendToContacts(contacts: List<Contact>, message: String) {
        val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(SmsManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }

        contacts.forEach { contact ->
            smsManager.sendTextMessage(contact.number, null, message, null, null)
        }
    }
}