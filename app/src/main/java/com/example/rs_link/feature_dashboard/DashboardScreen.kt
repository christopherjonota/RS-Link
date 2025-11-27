package com.example.rs_link.feature_dashboard
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
        DashboardRoute.Location,
        DashboardRoute.Riding,
        DashboardRoute.Safety,
        DashboardRoute.Settings
    )
    Scaffold(
        bottomBar = {
            NavigationBar(modifier = Modifier.background(MaterialTheme.colorScheme.secondary).padding(horizontal = 4.dp), containerColor =  MaterialTheme.colorScheme.secondary, ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            val isSelected =  currentDestination?.hierarchy?.any { it.route == screen.route } == true
                            val iconSelected = if(!isSelected) {screen.icon} else{screen.selectedIcon}
                            Image(painter = painterResource(iconSelected), contentDescription = screen.title, modifier = Modifier.width(24.dp).height(30.dp))
                            Modifier.size(10.dp).padding(0.dp)},
                        label = { Text(
                            text = screen.title,
                            style = MaterialTheme.typography.labelSmall
                        ) },
                        // Check if this item is selected
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // 1. Pop up to the start destination to avoid stack buildup
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = false
                                }
                                // 2. Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // 3. Restore state when reselecting
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primary,
                        ))
                }
            }
        }
    ) { innerPadding ->
        // Call your separate Navigation file
        DashboardNavigation(
            navController = navController,
            paddingValues = innerPadding,

            onLogOut = onLogout
        )
    }
}