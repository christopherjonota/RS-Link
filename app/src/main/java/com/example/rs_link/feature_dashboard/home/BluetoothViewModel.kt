package com.example.rs_link.feature_dashboard.home

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.UUID
import android.bluetooth.le.ScanSettings // <--- Crucial for scan mode
import android.os.Build
import android.bluetooth.BluetoothGattDescriptor

object BluetoothConstants {
    // This will match the service uuid of the rs link device so it only shows that device
    const val SERVICE_UUID = "12345678-1234-5678-1234-56789abcdef0"
    // 2. The Characteristic UUID (The specific value you want to read/notify)
    // You must add this to your ESP32 code too!
    const val CHARACTERISTIC_UUID = "abcdef01-1234-5678-1234-56789abcdef0"

    // 3. Android Standard UUID for "Client Characteristic Configuration"
    // This never changes. It is used to turn on notifications.
    const val CLIENT_CONFIG_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb"
}


@HiltViewModel
class BluetoothViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    //list of filtered devices found
    private val _scannedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val scannedDevices = _scannedDevices.asStateFlow()

    // state of bluetooth scan
    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()

    // System Bluetooth Adapter
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val adapter = bluetoothManager.adapter
    private val scanner = adapter.bluetoothLeScanner

    private var bluetoothGatt: BluetoothGatt? = null

    // UI STATE FOR STATUS
    private val _connectionStatus = MutableStateFlow("Disconnected")
    val connectionStatus = _connectionStatus.asStateFlow()

    private val _receivedData = MutableStateFlow("Waiting for data...")
    val receivedData = _receivedData.asStateFlow()

    private val gattCallback = object : BluetoothGattCallback() {

        // A. Connection Change (Connected / Disconnected)
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                _connectionStatus.value = "Connected!"
                // CRITICAL: You must discover services immediately after connecting
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                _connectionStatus.value = "Disconnected"
                gatt.close()
                bluetoothGatt = null
            }
        }

        // B. Services Discovered (Map of Features Loaded)
        @SuppressLint("MissingPermission")
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

            // B. Update the UI State
            _receivedData.update { message }
            // _connectionStatus.value = "Data: $dataString" // Optional: Update UI
        }
    }
    // 2. THE CONNECT FUNCTION
    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice) {
        _connectionStatus.value = "Connecting..."
        stopScan() // Always stop scanning before connecting to save battery!

        // connectGatt(Context, autoConnect, callback)
        // autoConnect = false means "Connect immediately", which is faster.
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
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

    // 4. CLEANUP
    @SuppressLint("MissingPermission")
    fun disconnect() {
        if (bluetoothGatt != null) {
            _connectionStatus.value = "Disconnecting..."
            bluetoothGatt?.disconnect()
        } else {
            _connectionStatus.value = "Disconnected"
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect() // Close connection when ViewModel dies
    }
    // Callback that receives results
    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device

            val currentList = _scannedDevices.value
            if (currentList.none { it.address == device.address }) {
                _scannedDevices.update { it + device }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            _isScanning.value = false
            Log.e("Bluetooth", "Scan failed: $errorCode")
        }
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        if (_isScanning.value) return // Already scanning

        // Clear old list
        _scannedDevices.value = emptyList()
        _isScanning.value = true

        // --- 1. BUILD THE FILTER ---
        val filter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(UUID.fromString(BluetoothConstants.SERVICE_UUID)))
            .build()

        // --- 2. BUILD SETTINGS ---
        // Low Latency = Fast scanning (good for active UI dialogs)
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        // --- 3. START SCAN WITH FILTER ---
        // We pass a List of filters. Only devices matching ALL rules in a filter show up.
        try {
            scanner.startScan(listOf(filter), settings, scanCallback)

            // Auto-stop after 10s
            viewModelScope.launch {
                delay(10000)
                stopScan()
            }
        } catch (e: Exception) {
            _isScanning.value = false
            Log.e("Bluetooth", "Start scan error: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        if (!_isScanning.value) return
        try {
            scanner.stopScan(scanCallback)
        } catch (e: Exception) {
            // Ignore error if bluetooth was turned off
        }
        _isScanning.value = false
    }


}