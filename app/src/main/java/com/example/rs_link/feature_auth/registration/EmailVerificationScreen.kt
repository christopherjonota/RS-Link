package com.example.rs_link.feature_auth.registration

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.rs_link.R
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.delay

@Composable
fun EmailVerificationScreen(
    viewModel: EmailVerificationViewModel = hiltViewModel(),
    onVerificationSuccess: () -> Unit, // Navigate to Dashboard
    onNavigateBack:() -> Unit
){
    val isVerified by viewModel.isVerified.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Auto-navigate when verified
    LaunchedEffect(isVerified) {
        if (isVerified) {
            onVerificationSuccess()
        }
    }

    // Optional: Auto-check every 5 seconds (Polling)
    LaunchedEffect(Unit) {
        while(!isVerified) {
            delay(5000) // Wait 5 seconds
            viewModel.checkVerificationStatus()
        }
    }

    Scaffold(
        topBar = { // This will only contain the back button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(36.dp))
                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = null,
                        Modifier.size(32.dp)
                    )
                    Text(text = "Back")
                }
            }
        },
    ) { paddingValues ->

        // holds the content
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.rs_link_logo),
                contentDescription = null
            )
            Text("Email Verification")

            Text("Please check your inbox and click the verification link we've sent to")
            Text("@yahoo.com")

            Image(
                painter = painterResource(R.drawable.icon_mail),
                contentDescription = null
            )
            Button(
                onClick = { viewModel.checkVerificationStatus() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("I have verified my email")
                }
            }
            Text("Didn't receive an email?")
            TextButton(onClick = { viewModel.resendEmail() }) {
                Text("Resend Email")
            }

        }
    }
}