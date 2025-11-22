package com.example.rs_link.feature_dashboard.ui

import androidx.compose.foundation.layout.PaddingValues
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



sealed class DashboardRoute(val route: String, val title: String, val icon: ImageVector) {
    object Home : DashboardRoute("home", "Home", Icons.Default.Home)
    object History : DashboardRoute("history", "Rides", Icons.Default.AccountBox)
    object Profile : DashboardRoute("profile", "Profile", Icons.Default.Person)
}


@Composable
fun DashboardNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues, // <--- Crucial! Passed from Scaffold
) {
    NavHost(
        navController = navController,
        startDestination = DashboardRoute.Home.route,
        modifier = Modifier.padding(paddingValues) // Apply padding here
    ) {

        composable(DashboardRoute.Home.route) {
            // HomeScreen()
            // Mock content for now
            androidx.compose.material3.Text("Home Screen Content")
        }

        composable(DashboardRoute.History.route) {
            // HistoryScreen()
            androidx.compose.material3.Text("Ride History Content")
        }

        composable(DashboardRoute.Profile.route) {
            // ProfileScreen()
            androidx.compose.material3.Text("Profile Content")
        }
    }
}