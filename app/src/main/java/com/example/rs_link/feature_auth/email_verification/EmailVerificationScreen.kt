package com.example.rs_link.feature_auth.email_verification

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.rs_link.R
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun EmailVerificationScreen(
    viewModel: EmailVerificationViewModel = hiltViewModel(),
    onVerificationSuccess: () -> Unit, // Navigate to Dashboard
    onNavigateBack:() -> Unit
){
    val context = LocalContext.current // <--- Get Context
    val uiState by viewModel.uiState.collectAsState() // <--- Only one collector needed!

    val isVerified = uiState.isVerified
    val isLoading = uiState.isLoading
    val email = uiState.email

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

            Spacer(Modifier.height(8.dp))
            Text(text = "Email Verification",color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineLarge, fontSize = 32.sp)
            Spacer(Modifier.height(16.dp))
            Text(text = "Please check your inbox and click the verification link we've sent to",color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.headlineMedium)

            Text(text = email,  style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(24.dp))
            Image(
                painter = painterResource(R.drawable.icon_mail),
                contentDescription = null
            )
            Spacer(Modifier.height(24.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally){
                Button(
                    onClick = { openEmailApp(context) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("Open Email App")
                    }
                }

                Spacer(Modifier.height(16.dp))

                TextButton(onClick = { viewModel.resendEmail() }) {
                    Text("Resend Email")
                }

                Spacer(Modifier.height(16.dp))
            }


        }

    }

}
private fun openEmailApp(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_EMAIL)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback: If no specific email app is found, let them choose
        // or just show a toast saying "No email app found"
        Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
    }
}