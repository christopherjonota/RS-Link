package com.example.rs_link.feature_dashboard.safety

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object SafetyScreen{
    const val EMERGENCYCONTACT = "emergency contact"
    const val SAFETYSCREEN = "safety screen"
}


@Composable
fun SafetyScreenNavigation(
    safetyViewModel: SafetyViewModel
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = SafetyScreen.EMERGENCYCONTACT
    ) {
        composable(
            route = SafetyScreen.SAFETYSCREEN
        ) {
            SafetyScreen()
        }

    }
}