package com.example.rs_link.feature_auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.example.rs_link.R
import com.example.rs_link.feature_auth.login.LoginForm
import com.example.rs_link.feature_auth.login.SignInViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen (
    viewModel: SignInViewModel,
    onNavigateToRegistration: () -> Unit,
    onLoginSuccess: ()-> Unit
)
{

    // This will control the sheet
    var showBottomSheet by remember { mutableStateOf(false) }

    // Define the state for the sheet component
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // Ignore the anchor
    )
    val scope = rememberCoroutineScope()


    // This will animate the padding value when 'showBottomSheet' changes.
    val logoPaddingTop by animateDpAsState( // adjust the padding
        targetValue = if (showBottomSheet) 80.dp else 120.dp, // Moves from 120dp to 80dp
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "LogoPadding"
    )
    val logoSize by animateDpAsState( // adjust the image size
        targetValue = if (showBottomSheet) 120.dp else 200.dp, // Shrinks from 200dp to 120dp
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "LogoSize"
    )

    // Main container
    Scaffold { paddingValues ->
        // background that holds the content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            // pinned to the TopCenter of the Box with adjustable padding
            Image(
                painter = painterResource(id = R.drawable.rs_link_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .align(Alignment.TopCenter) // Pin to the top center
                    .padding(top = logoPaddingTop) // Apply animated padding
                    .size(logoSize),    // apply animated sizing
                contentScale = ContentScale.Fit
            )

            // a column container that holds the content below logo
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                // This serves as animation when the bottom sheet is shown and removed
                AnimatedVisibility(
                    visible = !showBottomSheet, // get the boolean state of the bottom sheet
                    enter = fadeIn(animationSpec = tween(delayMillis = 200)), // Fade in
                    exit = fadeOut(animationSpec = tween(200)) // Fade out
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        // holds the app name
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
                                modifier = Modifier
                                    .fillMaxSize(),
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = { showBottomSheet = true },
                                modifier = Modifier
                                    .fillMaxSize(),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
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

        // This is the Bottom Sheet
        if (showBottomSheet) {
            ModalBottomSheet(
                // This is called when the user drags the sheet down or clicks outside the sheet.
                onDismissRequest = {
                    showBottomSheet = false // This will hide the sheet once it is dismissed
                },
                sheetState = sheetState
            ) {
                LoginForm(
                    viewModel = viewModel,
                    onClose = {
                        // Animate the sheet hiding, then set the state to false
                        scope.launch {
                            sheetState.hide() // This handles the animation
                        }.invokeOnCompletion {
                            showBottomSheet = false // this will set the state of bottomsheet
                        }
                    },
                    onLoginSuccess =  onLoginSuccess,
                    onNavigateToRegistration = onNavigateToRegistration )
            }
        }
    }
}