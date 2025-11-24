package com.example.rs_link.feature_auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.rs_link.R
import com.example.rs_link.feature_auth.login.LoginForm
import com.example.rs_link.feature_auth.login.LoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen (
    viewModel: LoginViewModel,
    onNavigateToRegistration: () -> Unit,
    onLoginSuccess: ()-> Unit
) {
    val uiState by viewModel.uiState.collectAsState() // holds the state of the login view model
    val scope = rememberCoroutineScope()

    // Auto-Dismiss Logic for the error box
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            // Wait 3 seconds, then clear the error
            delay(3000)
            viewModel.clearErrorShown() // This will reset the error to null
        }
    }

    // Navigates to the dashboard once the login is success
    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            onLoginSuccess()
        }
    }

    // This will control the sheet
    var loginFormBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { !uiState.isLoading } //this will prevent the login sheet to be dragged when the state is loading

    )
    // This will animate the padding value when 'showBottomSheet' changes.
    val logoPaddingTop by animateDpAsState( // adjust the padding
        targetValue = if (loginFormBottomSheet) 60.dp else 120.dp, // Moves from 120dp to 80dp
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "LogoPadding"
    )
    val logoSize by animateDpAsState( // adjust the image size
        targetValue = if (loginFormBottomSheet) 120.dp else 200.dp, // Shrinks from 200dp to 120dp
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "LogoSize"
    )

    @Composable
    fun TopErrorNotificationPopup(
        errorMessage: String?,
        onDismiss: () -> Unit
    ) {
        if (errorMessage != null) {
            // Popup Compose will breaks out of the layout and floats on top
            Popup(
                alignment = Alignment.TopCenter,
                properties = PopupProperties(
                    focusable = false, // Allows clicking the screen below it
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                // 2. ANIMATION: Slides down
                AnimatedVisibility(
                    visible = true, // Always true because the Popup only exists when error exists
                    enter = slideInVertically { -it } + fadeIn(),
                    exit = slideOutVertically { -it } + fadeOut()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp, start = 16.dp, end = 16.dp) // Top padding for Status Bar
                            .clickable { onDismiss() }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }

    TopErrorNotificationPopup(
        errorMessage = uiState.errorMessage,
        onDismiss = { viewModel.clearErrorShown() }
    )

    // Main container
    Scaffold(
    )
    { paddingValues ->
        // background that holds the content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.rs_link_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .align(Alignment.TopCenter) // Pin to the top center
                    .padding(top = logoPaddingTop) // Apply animated padding
                    .size(logoSize),    // apply animated sizing
                contentScale = ContentScale.Fit
            )

            // Container that holds the auth screen contents
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {

                // This serves as animation when the bottom sheet is shown and removed
                AnimatedVisibility(
                    visible = !loginFormBottomSheet, // get the boolean state of the bottom sheet
                    enter = fadeIn(animationSpec = tween(delayMillis = 200)), // Fade in
                    exit = fadeOut(animationSpec = tween(200)) // Fade out
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Holds the app title and tagline
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "RS LINK",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Ride smart. Ride safe",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(160.dp))

                        // "Create Account" button
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Button(
                                onClick = onNavigateToRegistration,
                                modifier = Modifier.fillMaxSize(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    text = "Create Account",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        // "Already have an Account" button
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Button(
                                onClick = { loginFormBottomSheet = true },
                                modifier = Modifier
                                    .fillMaxSize(),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.secondary
                                ),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.Transparent
                                )

                            ) {
                                Text(
                                    text = "Already have an Account",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }

        }

        // This is the Bottom Sheet that contains the login form
        if (loginFormBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {    // Called when the user drags the sheet down or clicks outside the sheet.
                    if (!uiState.isLoading) { //If they somehow trigger dismiss, ignore it if loading
                        loginFormBottomSheet = false // This will hide the sheet once it is dismissed
                        viewModel.resetUiState()    // reset the text fields to default values
                    }
                },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoginForm(
                        viewModel = viewModel,
                        onClose = {
                            // Animate the sheet hiding, then set the state to false
                            scope.launch {
                                sheetState.hide() // This handles the animation
                            }.invokeOnCompletion {
                                loginFormBottomSheet = false // this will set the state of bottomsheet
                                viewModel.resetUiState()    // this will reset the ui state once its been close
                            }
                        },
                    )
                }
            }
        }
    }
    if (uiState.isLoading) {
        Dialog(
            onDismissRequest = { /* Do nothing, blocks dismissal */ },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            // box in the center of the screen that holds the circular indicator
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


