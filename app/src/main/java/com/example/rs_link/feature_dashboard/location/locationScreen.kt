package com.example.rs_link.feature_dashboard.location

// 1. Standard Android & Compose Imports
import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// 2. Google Maps Compose Library (The UI components)
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

// 3. Google Maps Core Models (The data objects like Lat/Long)
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

// 4. Accompanist Permissions (For handling runtime permissions easily)
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeMapScreen() {
    val context = LocalContext.current

    // 1. Get the Location Client
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }


    // 1. Setup Permission State
    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // 2. Setup Map Properties
    // This tells the map to show the blue dot if permissions are granted
    val mapProperties = remember(locationPermissionState.allPermissionsGranted) {
        MapProperties(
            isMyLocationEnabled = locationPermissionState.allPermissionsGranted,
            mapType = MapType.NORMAL,
            isTrafficEnabled = true
        )
    }

    val mapUiSettings = remember {
        MapUiSettings(
            myLocationButtonEnabled = true, // Shows the target button
            zoomControlsEnabled = false // Hide zoom +/- buttons for cleaner UI
        )
    }

    // 3. Camera Position (Start at a default, or last known location)
    // Example: Manila Coordinates
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(14.5995, 120.9842), 15f)
    }

    // 3. THE MAGIC: Auto-center when permission is granted
    LaunchedEffect(locationPermissionState.allPermissionsGranted) {
        if (locationPermissionState.allPermissionsGranted) {
            try {
                // Get the last known location (it's fast)
                val locationResult = fusedLocationClient.lastLocation
                locationResult.addOnSuccessListener { location ->
                    if (location != null) {
                        // Move the camera to the user!
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            LatLng(location.latitude, location.longitude),
                            15f
                        )
                    }
                }
            } catch (e: SecurityException) {
                // Handle exception if permission was revoked
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (locationPermissionState.allPermissionsGranted) {
            // --- SHOW MAP ---
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = MapUiSettings(myLocationButtonEnabled = true) // Allow them to re-center
            ) {
                // You can add Markers here if needed
                // Marker(state = MarkerState(position = LatLng(...)))
            }
        } else {
            // --- SHOW PERMISSION REQUEST UI ---
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("We need your location to show available rides.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { locationPermissionState.launchMultiplePermissionRequest() }) {
                    Text("Enable Location")
                }
            }
        }
    }
}