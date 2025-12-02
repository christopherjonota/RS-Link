package com.example.rs_link.feature_dashboard.safety

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.rs_link.R
import com.example.rs_link.feature_auth.registration.LabeledTextField
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun AddEmergencyContactScreen(
    viewModel: EmergencyContactViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
// 1. Local state for the confirmation popup
    var showDeleteDialog by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateBack() // This calls the navController logic
        }
    }
    Scaffold { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.surface)){
            Column(modifier = Modifier.align(Alignment.TopCenter)) {
                Surface (
                    shadowElevation = 8.dp,
                    modifier = Modifier.wrapContentSize()
                ){
                    Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
                        Row (verticalAlignment = Alignment.CenterVertically){
                            IconButton(
                                onClick = onNavigateBack
                            ) {
                                Image(
                                    contentDescription = null,
                                    imageVector = Icons.Default.ArrowBack
                                )
                                Spacer(Modifier.width(12.dp))
                            }
                            Text(
                                text = if (viewModel.isEditMode) "Edit Contact" else "Add Emergency Contact",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelLarge)
                        }
                        if (viewModel.isEditMode) {
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Contact",
                                    tint = MaterialTheme.colorScheme.error // Red color for danger
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
                Column (modifier = Modifier.padding(horizontal = 16.dp)){
                    // First Name text field
                    LabeledTextField(
                        textFieldLabel = "First Name",
                        value = uiState.firstName,
                        onValueChange = viewModel::onFirstNameChange,
                        placeholder = "Enter your first name",
                        errorText = uiState.firstNameError
                    )
                    Spacer(Modifier.height(8.dp))

                    // Last Name text field
                    LabeledTextField(
                        textFieldLabel = "Last Name",
                        value = uiState.lastName,
                        onValueChange = viewModel::onLastNameChange,
                        placeholder = "Enter your last name",
                        errorText = uiState.lastNameError
                    )

                    Spacer(Modifier.height(8.dp))

                    // Contact Number text field
                    LabeledTextField(
                        textFieldLabel = "Contact Number",
                        value = uiState.phoneNumber,
                        onValueChange = viewModel::onNumberChange,
                        placeholder = "Enter your contact number",
                        errorText = uiState.phoneNumberError, // Show error if validation failed later
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone
                        ),
                        leadingIcon = {
                            Box {
                                TextButton(onClick = {}) {
                                    Text(
                                        text = "+63",
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )

                                }
//                            DropdownMenu(
//                                expanded = expanded,
//                                onDismissRequest = { expanded = false }
//                            ) {
//                                countryCodes.forEach{ code ->
//                                    DropdownMenuItem(
//                                        text = { Text(code)},
//                                        onClick = {
//                                            viewModel.onCountryCodeChange(code)
//                                            expanded = false
//                                        }
//                                    )
//                                }
//                            }
                            }
                        }
                    )
                    Button(
                        onClick = viewModel::saveContact,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White)
                        } else {
                            Text(if (viewModel.isEditMode) "Update Contact" else "Save Contact")
                        }
                    }
                }

            }
        }
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Contact?") },
                text = { Text("Are you sure you want to remove this emergency contact? This action cannot be undone.") },
                icon = { Icon(Icons.Default.Warning, null) },

                // Confirm Button (Red)
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteContact() // Call VM
                            showDeleteDialog = false
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                },

                // Cancel Button
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}