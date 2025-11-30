package com.example.rs_link.feature_dashboard.home

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import androidx.bluetooth.ScanFilter
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


object BluetoothConstants {
    // This will match the service uuid of the rs link device so it only shows that device
    const val SERVICE_UUID = "12345678-1234-5678-1234-56789abcdef0"
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
//        val filter = ScanFilter.Builder()
//            .setServiceUuid(ParcelUuid(UUID.fromString(BluetoothConstants.SERVICE_UUID)))
//            .build()

        // Start scanning
        scanner.startScan(scanCallback)

        // Stop scanning automatically after 10 seconds to save battery
        viewModelScope.launch {
            delay(10000)
            stopScan()
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        if (!_isScanning.value) return
        scanner.stopScan(scanCallback)
        _isScanning.value = false
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice) {
        stopScan()
        // Logic to connect to the device (GATT) goes here
        // device.connectGatt(...)
        Log.d("Bluetooth", "Connecting to ${device.name}...")
    }
}