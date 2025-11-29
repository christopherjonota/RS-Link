package com.example.rs_link.feature_dashboard.home

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
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
import com.example.rs_link.R

@Composable
fun HomeScreen (
    viewModel: HomeViewModel = hiltViewModel(),
){
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
                        Button(
                            onClick = {}
                        ) {
                            Text("connect")
                        }
                    }
                }
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
        // If permissions granted, start scan
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            viewModel.startScan()
        }
    }

    // Request permissions immediately when dialog opens
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            ))
        } else {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            ))
        }
    }

    // --- THE DIALOG ---
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Connect to RS-LINK") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp) // Limit height
            ) {
                if (isScanning) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Scanning for nearby devices...", style = MaterialTheme.typography.bodySmall)
                } else if (devices.isEmpty()) {
                    Text("No RS-LINK devices found.", color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // THE LIST OF FILTERED DEVICES
                LazyColumn {
                    items(devices) { device ->
                        ListItem(
                            headlineContent = { Text(device.name ?: "Unknown") },
                            supportingContent = { Text(device.address) },
                            leadingContent = {
                                Icon(Icons.Default.Person, contentDescription = null)
                            },
                            modifier = Modifier.clickable {
                                viewModel.connectToDevice(device)
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
    )}
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