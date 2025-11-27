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
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rs_link.R
import com.example.rs_link.feature_auth.registration.LabeledTextField

@Composable
fun AddEmergencyContactScreen(
    onNavigateBack: () -> Unit
) {
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
                    Row (modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
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
                            text = "Add Emergency Contact",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelLarge)
                    }
                }
                Spacer(Modifier.height(24.dp))
                // First Name text field
                LabeledTextField(
                    textFieldLabel = "First Name",
                    value = "",
                    onValueChange = { },
                    placeholder = "Enter your first name",
                    errorText = "",
                )
                Spacer(Modifier.height(8.dp))

                // Last Name text field
                LabeledTextField(
                    textFieldLabel = "Last Name",
                    value = "",
                    onValueChange = {},
                    placeholder = "Enter your last name",
                    errorText = "",
                )

                Spacer(Modifier.height(8.dp))

                // Contact Number text field
                LabeledTextField(
                    textFieldLabel = "Contact Number",
                    value = "",
                    onValueChange = {},
                    placeholder = "Enter your contact number",
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
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }
            }
        }
    }
}