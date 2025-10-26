package com.example.rs_link.feature_signin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun RegistrationScreen(
    onNavigateBack: () -> Unit,
    onRegistrationSuccess: () -> Unit,
    viewModel: SignInViewModel){

    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current // Used for the Toast

    // --- Side Effect for Navigation ---
    // Reacts only when isRegistrationSuccess changes to true
    LaunchedEffect(state.isRegistrationSuccess) {
        if (state.isRegistrationSuccess) {
            Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
            onRegistrationSuccess()
        }
    }
    // ----------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        Text(
            text = "Set up your account"
        )
        OutlinedTextField(
            value = "aad",
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Password") }
        )
        Button(
            onClick = onNavigateBack
        ) {
            Text(text = "Go b|ack")
        }
    }

}