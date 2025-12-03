package com.example.rs_link.feature_auth

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.animation.core.tween
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rs_link.feature_auth.email_verification.EmailVerificationScreen
import com.example.rs_link.feature_auth.forgot_password.ForgotPasswordScreen
import com.example.rs_link.feature_auth.registration.RegistrationScreen
import com.example.rs_link.feature_auth.login.LoginViewModel
import com.example.rs_link.feature_auth.registration.RegistrationViewModel

object Screen{
    const val REGISTRATION = "registration"
    const val AUTH = "auth"
    const val FORGOT_PASSWORD = "forgot_password"
    const val EMAIL_VERIFICATION = "email_verification"
}

@Composable
fun AuthNavigation(
    loginViewModel: LoginViewModel,
    registrationViewModel: RegistrationViewModel,
    onLoginSuccess: () -> Unit,
    startDestination: String = Screen.AUTH // Default
){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination  // Start the navigation to the starting part
    ){

        // Auth Screen Route
        composable( // Destination for starting point
            route = Screen.AUTH,
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(700),
                )
            }
        ) {
            AuthScreen(
                viewModel = loginViewModel,
                onNavigateToRegistration = {
                    navController.navigate(Screen.REGISTRATION){
                        launchSingleTop = true // this avoids creating multiple copies of the destination
                        restoreState = true // restore the state when returning to a destination
                    }
                },
                onLoginSuccess = onLoginSuccess,
                onNavigateToForgotPassword = {navController.navigate(Screen.FORGOT_PASSWORD)}

            )
        }

        // Registration Route
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
                    navController.navigate(Screen.EMAIL_VERIFICATION) {
                        popUpTo(Screen.REGISTRATION) { inclusive = true }
                    }
                },
                registrationViewModel
            )
        }

        composable(
            route = Screen.EMAIL_VERIFICATION,
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
            }

        ) {
            EmailVerificationScreen(
                // Use hiltViewModel() here to get your EmailVerificationViewModel
                viewModel = hiltViewModel(),

                // ✅ SUCCESS: When verified, trigger the Activity switch
                onVerificationSuccess = {
                    onLoginSuccess()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.FORGOT_PASSWORD,
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
            }){
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}






