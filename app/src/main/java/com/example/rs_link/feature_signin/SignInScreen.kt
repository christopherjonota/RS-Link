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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun SignInScreen (viewModel: SignInViewModel){


    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom

    ) {
        Image(
            painter = painterResource(id = R.drawable.rs_link_logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(200.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = "RS-LINK",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "Ride smart. Ride safe",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(160.dp))
        // "Create Account" button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ){
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
                ) {
                Text(
                    text = "Create Account",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge)
            }
        }


        Spacer(Modifier.height(24.dp))


        // "Already have an Account" button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.Center
        ){
            Button(
                onClick = {},
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

//    var showForm by remember { mutableStateOf(false) }
//    Scaffold (
//        content = { paddingValues ->
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ){
//                Button(onClick = {}) {
//                    Text("Hello")
//                }
//            }
//        }
//    )
//    if (showForm) {
//        Dialog(onDismissRequest = { showForm = false }) {
//            // 1. AnimatedVisibility handles the slide-in/out
//            AnimatedVisibility(
//                visible = showForm,
//                // Slide up from the bottom of the screen (full height)
//                enter = slideInVertically(
//                    initialOffsetY = { fullHeight -> fullHeight },
//                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
//                ),
//                // Slide out back to the bottom
//                exit = slideOutVertically(
//                    targetOffsetY = { fullHeight -> fullHeight },
//                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
//                )
//            ) {
//                // 2. The Form Container (e.g., a Card)
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth(0.85f) // Adjust width
//                        .wrapContentHeight() // Allow it to sit in the center
//                        .padding(16.dp)
//                ) {
//                    // 3. The Login Form Content
//                    LoginFormContent(
//                        onLogin = { showForm = false }
//                    )
//                }
//            }
//        }
//    }
}