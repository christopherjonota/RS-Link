package com.example.rs_link.feature_dashboard.home

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
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
import androidx.compose.ui.unit.sp
import com.example.rs_link.R

@Composable
fun HomeScreen (
    viewModel: HomeViewModel = hiltViewModel(),
    bluetoothViewModel: BluetoothViewModel = hiltViewModel()
){
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

                        // 1. Notification Button with Badge
                        IconButton(
                            onClick = {  },
                            modifier = Modifier
                                .background(Color.White, CircleShape) // White background circle
                                .size(48.dp) // Match profile size
                        ) {
                            // This Box handles the "Red Dot" logic
                            BadgedBox(
                                badge = {
                                    if (hasNotifications) {
                                        Badge(
                                            containerColor = Color.Red,
                                            contentColor = Color.White
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

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
                        .height(250.dp),
                    shape = RoundedCornerShape(12.dp)
                ){
                    Column{
                        Text("RS Link")
                        Row {
                            Text("Hehheehe")
                            Image(
                                painter = painterResource(id = R.drawable.home_screen_illus),
                                contentDescription = null
                            )
                        }
                        Card {
                            Text("get yours now")
                        }


                        if (showBluetoothDialog) {
                            BluetoothDeviceListDialog(
                                onDismiss = { showBluetoothDialog = false }
                            )
                        }

                        // Right: Connect/Disconnect Button
                        if (connectionStatus == "Connected!") {
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
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Connect", fontSize = 12.sp)
                            }
                        }

                        Text(
                            text = "Status: $connectionStatus",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                // 2. Show the Card
                LiveDataCard(data = receivedData)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickActionItem(
                        title = "Ride",
                        icon = Icons.Default.Person,
                        onClick = {}
                    )
                    QuickActionItem(
                        title = "Package",
                        icon = Icons.Default.Person,
                        onClick = { /* TODO */ }
                    )
                    QuickActionItem(
                        title = "Reserve",
                        icon = Icons.Default.Person,
                        onClick = { /* TODO */ }
                    )
                }
            }


        }


    }
}


// --- HELPER COMPONENT FOR BUTTONS ---
@Composable
fun QuickActionItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        // The Icon Box
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(70.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@SuppressLint("MissingPermission")
@Composable
fun BluetoothDeviceListDialog(
    viewModel: BluetoothViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val devices by viewModel.scannedDevices.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    // --- PERMISSION LAUNCHER ---
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            viewModel.startScan()
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
                if (isScanning) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Text("Searching for hardware...", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (devices.isEmpty() && !isScanning) {
                    Text("No devices found.")
                }

                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(devices) { device ->
                        ListItem(
                            headlineContent = { Text(device.name ?: "Unknown Device") },
                            supportingContent = { Text(device.address) },
                            leadingContent = { Icon(Icons.Default.Phone, null) },
                            modifier = Modifier.clickable {
                                viewModel.connectToDevice(device) // Your connection logic
                                onDismiss()
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { viewModel.stopScan(); onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}
@Composable
fun LiveDataCard(
    data: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Phone, // Or Icons.Default.Bolt
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Live Device Data",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // The Actual Text from ESP32
            Text(
                text = data,
                style = MaterialTheme.typography.headlineMedium, // Big text
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}