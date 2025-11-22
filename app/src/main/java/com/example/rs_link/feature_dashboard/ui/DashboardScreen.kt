package com.example.rs_link.feature_dashboard.ui
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen (
    onLogout: () -> Unit
){
    val navController = rememberNavController()

    // Define the tabs for the bottom bar
    val bottomNavItems = listOf(
        DashboardRoute.Home,
        DashboardRoute.History,
        DashboardRoute.Profile
    )
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("RS-LINK") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        // Check if this item is selected
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // 1. Pop up to the start destination to avoid stack buildup
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // 2. Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // 3. Restore state when reselecting
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Call your separate Navigation file
        DashboardNavigation(
            navController = navController,
            paddingValues = innerPadding
        )
    }
}