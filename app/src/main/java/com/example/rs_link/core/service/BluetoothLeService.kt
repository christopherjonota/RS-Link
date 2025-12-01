package com.example.rs_link.core.service

// 1. Android Service & System

// 2. Bluetooth Components

// 3. Notification Compatibility (Required for older Android versions)

// 4. Hilt Dependency Injection

// 5. Your Resources (For the Notification Icon)
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
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.rs_link.R
import com.example.rs_link.data.repository.UserRepository
import com.example.rs_link.feature_dashboard.DashboardActivity
import com.example.rs_link.feature_dashboard.home.BluetoothConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.UUID


@AndroidEntryPoint
class BluetoothLeService : Service() {
    // 1. The Connection now lives here
    private var bluetoothGatt: BluetoothGatt? = null
    @Inject
    lateinit var userRepository: UserRepository

    // Scope for background database/repository operations
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

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
            }
            else if (message.contains("Confirmed Accident", ignoreCase = true)){
                sendAlertNotification("Accident Detected has been Confirmed! Send Help!")
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
}