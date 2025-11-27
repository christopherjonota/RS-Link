package com.example.rs_link.feature_dashboard.safety

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.rs_link.R

@Composable
fun EmergencyContactScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddContact: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(MaterialTheme.colorScheme.surface)){
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
                            text = "Emergency Contacts",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelLarge)
                    }
                }
                Spacer(Modifier.height(24.dp))
                Column (modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween){
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Image(
                            painter = painterResource(id = R.drawable.emergency_contact_illus),
                            contentDescription = null,
                            Modifier.size(240.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(text = "Add Emergency Contacts",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.background,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = "Your emergency contacts will be notified if you have trigger an alert.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                    Column (
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ){
                        Text(
                            text = "Tip: Add trusted family members, or friends who can monitor you on your ride.",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { showDialog= true },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                        ) {
                            Text("+ Add Contact")
                        }
                        if (showDialog) {
                            ContactOptionDialog(
                                onDismiss = { showDialog = false },
//                                onPickFromContacts = {
//                                    // Launch system picker
//                                    contactLauncher.launch(null)
//                                },
                                onAddManually = {
                                    showDialog = false
                                    // Navigate to your manual form
                                    onNavigateToAddContact()
                                }
                            )
                        }
                    }

                }


            }
        }
    }
}

@Composable
fun ContactOptionDialog(
    onDismiss: () -> Unit,
    onAddManually: () -> Unit
){
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Add Emergency Contact",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

//                // OPTION 1: Import
//                ListItem(
//                    headlineContent = { Text("Import from Contacts") },
//                    leadingContent = {
//                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
//                    },
//                    modifier = Modifier.clickable { onPickFromContacts() }
//                )

                // OPTION 2: Manual
                ListItem(
                    headlineContent = { Text("Add Manually") },
                    leadingContent = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    modifier = Modifier.clickable { onAddManually() }
                )
            }
        }
    }

}