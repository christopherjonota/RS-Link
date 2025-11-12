package com.example.rs_link.feature_auth.registration

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import com.example.rs_link.feature_auth.login.SignInViewModel
import com.google.firestore.v1.TransactionOptions.ReadOnly
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onNavigateBack: () -> Unit, // callback function passed to the navigation controller to go back
    onRegistrationSuccess: () -> Unit,  // callback function that triggers once the registration is finished
    viewModel: RegistrationViewModel  = hiltViewModel()
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
    // Create and remember the ScrollState
    val scrollState = rememberScrollState()

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

            // First Name text field
            LabeledTextField(
                title = "First Name",
                value = state.firstName,
                onValueChange = viewModel::onFirstNameChange,
                placeholder = "Enter your first name"
            )
            Spacer(Modifier.height(12.dp))

            // Last Name text field
            LabeledTextField(
                title = "Last Name",
                value = state.lastName,
                onValueChange = viewModel::onLastNameChange,
                placeholder = "Enter your last name"
            )

            Spacer(Modifier.height(12.dp))

            // Contact Number text field
            LabeledTextField(
                title = "Contact Number",
                value = state.contactNumber,
                onValueChange = viewModel::onContactNumberChange,
                placeholder = "Enter your contact number",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            Spacer(Modifier.height(12.dp))

            // Birthday text field
            DatePickerField(
                title = "Birthday",
                value = state.birthdayDisplay,
                placeholder = "Select Date",
                onDateSelected = { millis ->
                    viewModel.onBirthDateChange(millis)
                }
            )

            Spacer(Modifier.height(12.dp))

            LabeledTextField(
                title = "Email Address",
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                placeholder = "Enter your email address",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                )

            )

            Spacer(Modifier.height(12.dp))

            var passwordVisible by remember { mutableStateOf(false) }
            LabeledTextField(
                title = "Password",
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = "Enter your password",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None // Show password
                } else {
                    PasswordVisualTransformation() // Hide password
                },
                trailingIcon = {
                    // Determine which icon to show
                    val image = if (passwordVisible)
                        Icons.Filled.Check // Eye icon open
                    else
                        Icons.Filled.Add // Eye icon closed

                    IconButton(onClick = {
                        passwordVisible = !passwordVisible // Toggle state on click
                    }) {
                        Icon(imageVector  = image, contentDescription = "Toggle password visibility")
                    }
                }

            )
            Spacer(Modifier.height(12.dp))

            LabeledTextField(
                title = "Confirm Password",
                value = state.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                placeholder = "Re-enter your password",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = PasswordVisualTransformation()

            )
            Spacer(Modifier.height(24.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {  },
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(text = "Add Account")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    title: String,
    value: String,
    placeholder: String,
    onDateSelected: (Long) -> Unit // This is the callback
) {
    // the state for the picker lives here
    var showDatePicker by remember { mutableStateOf(false) }

    // DatePickerDialog logic
    if (showDatePicker) { // if the textfield was clicked this will show
        val datePickerState = rememberDatePickerState() // this is what is used to interact with and get data

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false }, // unshow the date picker
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                        showDatePicker = false // Close the dialog
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { // unshow the date picker
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // 4. This is the UI. It *uses* your LabeledTextField
    LabeledTextField(
        title = title,
        value = value,
        onValueChange = {}, // Not used
        placeholder = placeholder,
        enabled = false, // Makes it read-only
        modifier = Modifier.clickable { // Makes it clickable
            showDatePicker = true
        },
        trailingIcon = {
            IconButton(onClick = {showDatePicker = true}) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select date"
                )
            }

        }

    )
//    // State to hold the date selection and dialog visibility
//    val datePickerState = rememberDatePickerState()
//    var showDialog by remember { mutableStateOf(false) }
//
//    // Logic to format the selected date (omitted for brevity)
//    val selectedDate = datePickerState.selectedDateMillis?.let {
//        // Convert millis to a formatted date string
//        SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date(it))
//    } ?: "Select Date"
//
//    OutlinedTextField(
//        value = selectedDate,
//        onValueChange = { /* readOnly is true, so this is empty */ },
//        label = { Text("Date of Birth") },
//        readOnly = true, // Key: Prevents manual keyboard input
//        trailingIcon = {
//            IconButton(onClick = { showDialog = true }) {
//                Icon(Icons.Default.DateRange, contentDescription = "Select date")
//            }
//        }
//    )
//
//    // The Date Picker Dialog
//    if (showDialog) {
//        DatePickerDialog(
//            onDismissRequest = { showDialog = false },
//            confirmButton = {
//                TextButton(onClick = { showDialog = false }) { Text("OK") }
//            },
//            dismissButton = {
//                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
//            }
//        ) {
//            DatePicker(state = datePickerState)
//        }
//    }
}

@Composable
fun LabeledTextField(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false
//    isError: Boolean = false,
//    supportingText: String? = null
) {


    // This holds the label and the text field
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier.fillMaxWidth()
    ) {
        // This is the label above the field
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium
        )

        Spacer(modifier = Modifier.height(4.dp)) // Added a small spacer

        // This is the text field
        OutlinedTextField(
            value = value,                // Use the 'value' parameter
            onValueChange = onValueChange,  // Use the 'onValueChange' parameter
            placeholder = { Text(placeholder) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            singleLine = true, // Good for most form fields
            enabled = enabled,
            readOnly = !enabled,
            trailingIcon = trailingIcon,
            // Ensure colors look "active" even when disabled so the icon isn't grayed out
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                // This ensures the icon stays visible/dark even if enabled=false
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
            // --- APPLY THE ERROR STATE ---
//            isError = isError
        )
        // --- ADD THE SUPPORTING/ERROR TEXT ---
//        if (supportingText != null) {
//            Text(
//                text = supportingText,
//                color = if (isError) {
//                    MaterialTheme.colorScheme.error // Use error color if 'isError' is true
//                } else {
//                    MaterialTheme.colorScheme.onSurfaceVariant // Use a subtle color for regular hints
//                },
//                style = MaterialTheme.typography.bodySmall,
//                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
//            )
//        }

    }
}




