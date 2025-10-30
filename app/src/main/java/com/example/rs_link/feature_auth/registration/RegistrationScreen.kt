package com.example.rs_link.feature_auth.registration

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.rs_link.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material3.DatePicker
import com.example.rs_link.feature_auth.SignInViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onNavigateBack: () -> Unit, // callback function passed to the navigation controller to go back
    onRegistrationSuccess: () -> Unit,  // callback function that triggers once the registration is finished
    viewModel: SignInViewModel  // This will inject the viewmodel on this compose
) {
    val state by viewModel.uiState.collectAsState() // holds the value of the state e.g. email, password, etc.
    val context = LocalContext.current // Used for the Toast

    // Reacts only when isRegistrationSuccess changes to true
    LaunchedEffect(state.isRegistrationSuccess) {
        if (state.isRegistrationSuccess) {
            Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
            onRegistrationSuccess() // Navigate to the next screen
        }
    }
    // 1. Create and remember the ScrollState
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { // This will only contain the back button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 12.dp)
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Set up your account",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(24.dp))
            Column ( // This will be the container per text field
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = "First Name",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelMedium
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = viewModel::onPasswordChange,
                    label = {
                        Text("Enter your first name")
                            },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(Modifier.height(12.dp))
            Column ( // This will be the container per text field
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = "Last Name",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelMedium
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = viewModel::onPasswordChange,
                    label = {
                        Text("Enter your last name")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(Modifier.height(12.dp))
            BirthDateInput()

            Spacer(Modifier.height(12.dp))
            Column ( // This will be the container per text field
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = "Contact Number",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelMedium
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = viewModel::onPasswordChange,
                    label = {
                        Text("Enter your last name")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(Modifier.height(12.dp))
            Column ( // This will be the container per text field
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = "Email Address",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelMedium
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = viewModel::onPasswordChange,
                    label = {
                        Text("Email address")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            Column ( // This will be the container per text field
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = "Password",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelMedium
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = viewModel::onPasswordChange,
                    placeholder = {
                        Text("Enter your password")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            Column ( // This will be the container per text field
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = "Confirm Password",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelMedium
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = viewModel::onPasswordChange,
                    placeholder = {
                        Text("Re-enter your password")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = viewModel::register,
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(text = "Add Account")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthDateInput() {
    // State to hold the date selection and dialog visibility
    val datePickerState = rememberDatePickerState()
    var showDialog by remember { mutableStateOf(false) }

    // Logic to format the selected date (omitted for brevity)
    val selectedDate = datePickerState.selectedDateMillis?.let {
        // Convert millis to a formatted date string
        SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date(it))
    } ?: "Select Date"

    OutlinedTextField(
        value = selectedDate,
        onValueChange = { /* readOnly is true, so this is empty */ },
        label = { Text("Date of Birth") },
        readOnly = true, // Key: Prevents manual keyboard input
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.DateRange, contentDescription = "Select date")
            }
        }
    )

    // The Date Picker Dialog
    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}