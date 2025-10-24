package com.example.rs_link.feature_signin

import android.graphics.drawable.Drawable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.rs_link.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen (viewModel: SignInViewModel){

    // 2. State to control if the sheet is shown or hidden
    var showBottomSheet by remember { mutableStateOf(false) }

    // 3. State for the sheet component itself (controls animation, position)
    val sheetState = rememberModalBottomSheetState()

    val scope = rememberCoroutineScope()

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                // Apply the Scaffold padding directly to the Box
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                //verticalArrangement = Arrangement.Bottom
                // Use SpaceAround to vertically distribute elements
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.rs_link_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Fit
                )
                // 2. Texts (Group them with the Image)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "RS-LINK",
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
                        onClick = {},
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
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
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
        // This is the Bottom Sheet
        if (showBottomSheet) {
            // This will ignore the 50% anchor and will now occupy the full height of its content
            val state = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )
            ModalBottomSheet(
                // This is called when the user drags the sheet down or clicks outside the sheet.
                onDismissRequest = {
                    showBottomSheet = false // This will hide the sheet once it is dismissed
                },
                sheetState = state
            ) {
                LoginForm(
                    onClose = {
                        // Animate the sheet hiding, then set the state to false
                        scope.launch {
                            sheetState.hide() // This handles the animation
                        }.invokeOnCompletion {
                            showBottomSheet = false // this will set the state of bottomsheet
                        }
                    }
                )
            }
        }
    }
}


// This will be the login form that serve as the content inside the bottom sheet
@Composable
fun LoginForm(onClose: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(bottom = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to RS-Link!",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "Track. Alert. Ride with Confidence",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(40.dp))
        OutlinedTextField(
            shape = RoundedCornerShape(16.dp),
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            shape = RoundedCornerShape(16.dp),
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
        val listener = "hehe"
//        = LinkInteractionListener { link ->
//            if (link is LinkAnnotation.Clickable) {
//                // This is where you trigger the internal navigation action
//                onNavigateToForgotPassword()
//            }
//        }
        Text(
            text = buildAnnotatedString {
                append("Forgout")
                withLink(
                    link = LinkAnnotation.Clickable(
                        linkInteractionListener = listener
                    )
                )
            }
        )
        Button(
            onClick = { /* TODO: Handle login logic */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log in")
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onClose, // Trigger the lambda to close the sheet
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign up with Google")
        }
    }
}