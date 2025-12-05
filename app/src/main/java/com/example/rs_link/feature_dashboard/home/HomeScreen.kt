package com.example.rs_link.feature_dashboard.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.fonts.FontStyle
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.rs_link.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

import com.google.android.gms.location.Priority

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen (
    viewModel: HomeViewModel = hiltViewModel(),
    bluetoothViewModel: BluetoothViewModel = hiltViewModel(),
){

    val context = LocalContext.current
    val diagonalGradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF026773),
            Color(0xFFFAFAFA)
        ),
        start = Offset.Zero, // Top-Left corner (0, 0)
        end = Offset.Infinite  // Bottom-Right corner
    )

    var showBluetoothDialog by remember { mutableStateOf(false) }

    // 1. Observe the status
    val connectionStatus by bluetoothViewModel.connectionStatus.collectAsState()
    val receivedData by bluetoothViewModel.receivedData.collectAsState()

    // Determine color based on status text
    val statusColor = when (connectionStatus) {
        "Connected" -> Color.Green
        "Connecting..." -> Color.Yellow
        else -> Color.Red
    }
    val notificationPermission = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    LaunchedEffect(Unit) {
        if (!notificationPermission.status.isGranted) {
            notificationPermission.launchPermissionRequest()
        }
    }

    // 1. Prepare the permission request
    val smsPermissionState = rememberPermissionState(
        android.Manifest.permission.SEND_SMS
    )

    // 2. Ask immediately when Home Screen opens
    LaunchedEffect(Unit) {
        if (!smsPermissionState.status.isGranted) {
            smsPermissionState.launchPermissionRequest()
        }
    }
    val userName by viewModel.userName.collectAsState()
    val hasNotifications by viewModel.hasNotifications.collectAsState()
    // ROOT CONTAINER: Handles Layering
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.background),

            ) {
                Row (modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween){
                    // LEFT: Greeting
                    Column {
                        Text(
                            text = "Hello,",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // RIGHT: Actions (Notifications + Profile)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp) // Space between icons
                    ) {

//                        // 1. Notification Button with Badge
//                        IconButton(
//                            onClick = {  },
//                            modifier = Modifier
//                                .background(Color.White, CircleShape) // White background circle
//                                .size(48.dp) // Match profile size
//                        ) {
//                            // This Box handles the "Red Dot" logic
//                            BadgedBox(
//                                badge = {
//                                    if (hasNotifications) {
//                                        Badge(
//                                            containerColor = Color.Red,
//                                            contentColor = Color.White
//                                        )
//                                    }
//                                }
//                            ) {
//                                Icon(
//                                    imageVector = Icons.Default.Notifications,
//                                    contentDescription = "Notifications",
//                                    tint = MaterialTheme.colorScheme.primary
//                                )
//                            }
//                        }

                        // 2. Profile Icon (Your existing code)
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.padding(12.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

            }
            Column (modifier = Modifier.fillMaxSize().padding(16.dp)){
                Surface (
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)

                        ,
                    shape = RoundedCornerShape(12.dp),

                ){
                    Column(modifier = Modifier
                        .background(
                        brush = diagonalGradientBrush // 👈 Apply the Brush here
                    )
                        .padding(8.dp)
                    ){
                        Text(text = "RS Link", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.height(16.dp))
                        Row {
                            Text(modifier = Modifier.width(100.dp), text = "The Intelligent Link Between You and Safety.", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.secondary)
                            Spacer(Modifier.width(8.dp))
                            Image(
                                painter = painterResource(id = R.drawable.home_screen_illus),
                                contentDescription = null
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        val onCardClick: () -> Unit = {
                            // 2. Create and launch the Intent
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.shopee.com"))
                            context.startActivity(intent)
                        }
                        Button(onClick = onCardClick) {
                            Text(text = "Get your RS Link Now!")
                        }
                    }

                }
                Spacer(Modifier.height(16.dp))
                Surface (
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                    ,
                    shape = RoundedCornerShape(12.dp),

                    ){
                    Column(modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.background
                        )
                        .padding(8.dp)
                    ){
                        Spacer(Modifier.height(16.dp))
                        Row(Modifier.padding(horizontal = 8.dp)) {
                            Image(
                                painter = painterResource(id = R.drawable.home_screen_illus1),
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.fillMaxHeight().fillMaxWidth().align(Alignment.CenterVertically),horizontalAlignment = Alignment.CenterHorizontally){
                                if (showBluetoothDialog) {
                                    BluetoothDeviceListDialog(
                                        onDismiss = { showBluetoothDialog = false }
                                    )
                                }

                                // Right: Connect/Disconnect Button
                                if (connectionStatus == "Connected") {
                                    Button(
                                        onClick = { bluetoothViewModel.disconnect() },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Disconnect", fontSize = 12.sp)
                                    }
                                } else {
                                    // Show "Connect" button or Icon to open Scanner Dialog
                                    Button(
                                        onClick = { showBluetoothDialog = true },
                                        modifier = Modifier.padding(vertical = 16.dp),
                                    ) {
                                        Text(text = "Start your Ride Now", style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center )
                                    }
                                }

                            }

                        }
                    }

                }

            }


        }


    }
}

@SuppressLint("MissingPermission")
@Composable
fun BluetoothDeviceListDialog(
    viewModel: BluetoothViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val devices by viewModel.scannedDevices.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()


    val smsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // User finally said yes! We don't know WHICH device they clicked,
            // so usually we just show a Toast "Permission Granted, please try connecting again"
            // Or we handle the pending connection (more complex).
            Toast.makeText(context, "Permission granted. Tap device to connect.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Crash alerts will NOT work without SMS permission.", Toast.LENGTH_LONG).show()
        }
    }
    // --- 1. LAUNCHER: TURN ON LOCATION ---
    val locationSettingLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // User enabled Location -> Finally Start Scan!
            viewModel.startScan()
        } else {
            // User denied -> Close dialog
            onDismiss()
        }
    }

    // --- 1. LAUNCHER TO TURN ON BLUETOOTH ---
    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // User said "Yes", Bluetooth is now ON -> Start Scan
            // Bluetooth is On -> Now Check Location
            checkLocationAndScan(context, locationSettingLauncher) { viewModel.startScan() }
        } else {
            // User said "No" -> Dismiss dialog or show error
            onDismiss()
        }
    }
    // --- PERMISSION LAUNCHER ---
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            // Permissions Granted -> Check Hardware Power
            checkHardwareAndScan(context, enableBluetoothLauncher, locationSettingLauncher) {
                viewModel.startScan()
            }
        } else {
            onDismiss()
        }
    }

    LaunchedEffect(Unit) {
        // Request permissions specific to Android 12+ or older
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        permissionLauncher.launch(permissions)
    }

    AlertDialog(
        onDismissRequest = {
            viewModel.stopScan()
            onDismiss()
        },
        title = { Text("Connect to Device") },
        text = {
            Column(modifier = Modifier.width(300.dp)) {
                // 1. STATUS HEADER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isScanning) {
                        Text("Scanning...", style = MaterialTheme.typography.bodySmall)
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Scan Stopped", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (isScanning) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (devices.isEmpty() && !isScanning) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No devices found.")
                    }
                }
                else {
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(devices) { device ->
                            ListItem(
                                headlineContent = { Text(device.name ?: "Unknown Device") },
                                supportingContent = { Text(device.address) },
                                leadingContent = { Icon(Icons.Default.Phone, null) },
                                modifier = Modifier.clickable {
                                    // --- 2. THE CHECK LOGIC ---
                                    val hasSms = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.SEND_SMS
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (hasSms) {
                                        // All good! Connect.
                                        viewModel.connectToDevice(device)
                                        onDismiss()
                                    } else {
                                        // ⚠️ Alert the user!
                                        smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)

                                        // Optional: You can still connect anyway if you want,
                                        // but asking first is safer.
                                    }
                                    viewModel.connectToDevice(device)
                                    onDismiss()
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        },
        confirmButton = {
            // Only show "Scan Again" if we are NOT currently scanning
            if (!isScanning) {
                TextButton(onClick = { viewModel.startScan() }) {
                    Text("Scan Again")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                viewModel.stopScan()
                onDismiss()
            }) {
                Text("Cancel")
            }
        }
    )
}
private fun checkHardwareAndScan(
    context: Context,
    btLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    locLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
    onReady: () -> Unit
) {
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val adapter = bluetoothManager.adapter

    if (adapter?.isEnabled == true) {
        // Bluetooth is Good -> Check Location
        checkLocationAndScan(context, locLauncher, onReady)
    } else {
        // Bluetooth is Off -> Ask to Turn On
        btLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
    }
}
private fun checkLocationAndScan(
    context: Context,
    launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
    onReady: () -> Unit
) {
    // Define the requirement: We need "Low Power" location at minimum
    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_LOW_POWER, 10000).build()
    val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

    val client = LocationServices.getSettingsClient(context)
    val task = client.checkLocationSettings(builder.build())

    task.addOnSuccessListener {
        // Location is already ON
        onReady()
    }

    task.addOnFailureListener { exception ->
        if (exception is ResolvableApiException) {
            // Location is OFF, but we can ask user to turn it on via Popup
            try {
                val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                launcher.launch(intentSenderRequest)
            } catch (sendEx: IntentSender.SendIntentException) {
                // Ignore error
            }
        }
    }
}