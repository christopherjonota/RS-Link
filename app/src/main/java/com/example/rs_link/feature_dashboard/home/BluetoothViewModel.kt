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
import android.content.Intent
import com.example.rs_link.core.service.BluetoothLeService
import com.example.rs_link.data.repository.UserRepository

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
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository
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
    private val scanner
        get() = adapter.bluetoothLeScanner

    private var bluetoothGatt: BluetoothGatt? = null

    // UI STATE FOR STATUS
    val connectionStatus = userRepository.connectionStatus

    private val _receivedData = MutableStateFlow("Waiting for data...")
    val receivedData = _receivedData.asStateFlow()

    private val gattCallback = object : BluetoothGattCallback() {


    }
    // 2. THE CONNECT FUNCTION
    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice) {
        stopScan() // Always stop scanning before connecting to save battery!

        // Instead of connecting here, we start the Service
        val intent = Intent(context, BluetoothLeService::class.java).apply {
            action = "START_SERVICE"
            putExtra("DEVICE", device)
        }

        // Android 8+ requires startForegroundService
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }



    // 4. CLEANUP
    @SuppressLint("MissingPermission")
    fun disconnect() {
        val intent = Intent(context, BluetoothLeService::class.java).apply {
            action = "STOP_SERVICE"
        }
        context.startService(intent)
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
        // 1. Check if Scanner exists (Bluetooth is On)
        if (scanner == null) {
            Log.e("Bluetooth", "Bluetooth is OFF or unavailable.")
            _isScanning.value = false
            // Optional: Trigger a UI event to tell the user to turn on Bluetooth
            return
        }

        if (_isScanning.value) return

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
            scanner?.startScan(listOf(filter), settings, scanCallback)

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