package com.example.rs_link.feature_dashboard

import android.graphics.drawable.Drawable
import android.media.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.rs_link.R
import com.example.rs_link.feature_dashboard.home.HomeScreen
import com.example.rs_link.feature_dashboard.safety.SafetyScreen
import com.example.rs_link.feature_dashboard.settings.SettingsScreen


sealed class DashboardRoute(val route: String, val title: String, val icon: Int, val selectedIcon: Int) {
    object Home : DashboardRoute("home", "Home", R.drawable.icon_home, R.drawable.icon_home_filled)
    object Riding : DashboardRoute("riding", "Riding", R.drawable.icon_riding,R.drawable.icon_riding_filled)
    object Settings : DashboardRoute("settings", "Settings", R.drawable.icon_settings,R.drawable.icon_settings_filled)
    object Safety: DashboardRoute("safety", "Safety", R.drawable.icon_safety,R.drawable.icon_safety_filled)
    object Location: DashboardRoute("location", "Location", R.drawable.icon_location,R.drawable.icon_location_filled)
}


@Composable
fun DashboardNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues, // <--- Crucial! Passed from Scaffold
    onLogOut: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = DashboardRoute.Home.route,
        modifier = Modifier
            .padding(paddingValues) // Apply padding here
            .background(color = MaterialTheme.colorScheme.surface)
    ) {


        composable(DashboardRoute.Home.route) {
            HomeScreen()
        }

        composable(DashboardRoute.Safety.route){
            SafetyScreen()
        }
        composable(DashboardRoute.Riding.route) {
            // HistoryScreen()
            androidx.compose.material3.Text("Ride History Content")
        }
        composable(DashboardRoute.Location.route) {
            androidx.compose.material3.Text("Ride History Content")
        }
        composable(DashboardRoute.Settings.route) {
            SettingsScreen(
                onLogOut = onLogOut
            )
        }
    }
}