package com.example.rs_link.feature_auth

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.animation.core.tween
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rs_link.feature_auth.registration.RegistrationScreen

object Screen{
    const val REGISTRATION = "registration"
    const val AUTH = "auth"
    const val SIGNIN = "signin"
}

@Composable
fun AuthNavigation(viewModel: SignInViewModel){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.AUTH  // Start the navigation to the starting part
    ){

        // Destination for starting point
        composable(
            route = Screen.AUTH,
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(700),
                )
            })
        {
            AuthScreen(
                viewModel,
                onNavigateToRegistration = {
                    navController.navigate(Screen.REGISTRATION){
                        launchSingleTop = true // this avoids creating multiple copies of the destination
                        restoreState = true // restore the state when returning to a destination
                    }
                })
        }


        composable(
            route = Screen.REGISTRATION,
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(700)
                )
            },
            // When returning to this screen (from REGISTRATION), slide it in from the LEFT
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(700)
                )
            })
        {

            RegistrationScreen(
                onNavigateBack = {
                    navController.popBackStack(
                        route = Screen.AUTH,
                        inclusive = false // This will not remove Screen.HOME from the stack
                    )
                },
                onRegistrationSuccess = {
                    navController.navigate(Screen.AUTH)
                },
                viewModel
            )
        }
    }
}






