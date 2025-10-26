package com.example.rs_link.feature_signin

import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.rs_link.R

@Composable
fun RegistrationScreen(
    onNavigateBack: () -> Unit, // callback function passed to the navigation controller to go back
    onRegistrationSuccess: () -> Unit,  // callback function that triggers once the registration is finished
    viewModel: SignInViewModel  // This will inject the viewmodel on this compose
){
    val state by viewModel.uiState.collectAsState() // holds the value of the state e.g. email, password, etc.
    val context = LocalContext.current // Used for the Toast

    // Reacts only when isRegistrationSuccess changes to true
    LaunchedEffect(state.isRegistrationSuccess) {
        if (state.isRegistrationSuccess) {
            Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
            onRegistrationSuccess() // Navigate to the next screen
        }
    }
    Scaffold (

        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
            ) {
                Spacer(modifier = Modifier.height(36.dp))

                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = null, Modifier.size(32.dp))
                    Text(text = "Back")
                }

            }
        }
    ){ paddingValues ->
        Column (
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondary),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Box(modifier = Modifier.fillMaxWidth().height(10.dp).background(MaterialTheme.colorScheme.primary))
            Text(
                text = "Set up your account",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            OutlinedTextField(
                value = "",
                onValueChange = viewModel::onPasswordChange,
                label = { Text("First Name")}
            )
            OutlinedTextField(
                value = "",
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Last Name") }
            )
            OutlinedTextField(
                value = "",
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Date of Birth") }
            )
            OutlinedTextField(
                value = "",
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Contact Number") }
            )
            OutlinedTextField(
                value = "",
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Email Address") }
            )
            OutlinedTextField(
                value = "",
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Password") }
            )
            OutlinedTextField(
                value = "",
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Confirm Password") }
            )


        }
    }
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.secondary)
//    ) {

//    }

}