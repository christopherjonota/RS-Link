package com.example.rs_link.feature_dashboard

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
import com.example.rs_link.feature_dashboard.home.HomeScreen
import com.example.rs_link.feature_dashboard.safety.SafetyScreen
import com.example.rs_link.feature_dashboard.settings.SettingsScreen


sealed class DashboardRoute(val route: String, val title: String, val icon: ImageVector) {
    object Home : DashboardRoute("home", "Home", Icons.Default.Home)
    object Riding : DashboardRoute("riding", "Riding", Icons.Default.AccountBox)
    object Settings : DashboardRoute("settings", "Settings", Icons.Default.Person)
    object Safety: DashboardRoute("safety", "Safety", Icons.Default.Person)
    object Location: DashboardRoute("location", "Location", Icons.Default.Person)
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

        composable(DashboardRoute.Settings.route) {
            SettingsScreen(
                onLogOut = onLogOut
            )
            androidx.compose.material3.Text("Profile Content")
        }
    }
}