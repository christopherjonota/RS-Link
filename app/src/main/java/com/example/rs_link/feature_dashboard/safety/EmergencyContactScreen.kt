package com.example.rs_link.feature_dashboard.safety

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.rs_link.R
import com.example.rs_link.data.model.Contact
import androidx.compose.foundation.lazy.items // <--- FIXES "Int Required" error

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyContactScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddContact: () -> Unit,
    viewModel: EmergencyContactViewModel= hiltViewModel(),
    onNavigateToEdit: (String) -> Unit, // Passes ID
) {
    var showDialog by remember { mutableStateOf(false) }
    val contacts by viewModel.contacts.collectAsState()
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Emergency Contacts", style = MaterialTheme.typography.labelLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Add Button in Top Bar
                    IconButton(onClick = onNavigateToAddContact) {
                        Icon(Icons.Default.Add, contentDescription = "Add Contact")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (contacts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(MaterialTheme.colorScheme.surface)){
                Column(modifier = Modifier.align(Alignment.TopCenter)) {
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
        else{
            LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
                items(contacts) { contact ->
                    // Reusing your ContactItem
                    ContactItem(
                        contact = contact,
                        // Clicking the item goes to EDIT
                        onClick = { onNavigateToEdit(contact.id) },
                        onCallClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${contact.number}")
                            }
                            context.startActivity(intent)
                        }
                    )
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

@Composable
fun ContactItem(
    contact: Contact,
    onClick: () -> Unit,      // Triggers navigation to Edit Screen
    onCallClick: () -> Unit   // Triggers phone dialer
) {
    Card(
        // 1. STYLE: White card with slight shadow
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            // 2. INTERACTION: Making the whole card clickable for Edit
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // --- LEFT SIDE: INFO ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f) // Takes up all available space
            ) {
                // A. Avatar / Icon
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            // Show first letter of name (e.g., "J")
                            text = contact.firstName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // B. Text Details
                Column {
                    Text(
                        text = "${contact.firstName} ${contact.lastName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${contact.relationship} • ${contact.number}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // --- RIGHT SIDE: ACTION ---
            // C. Call Button (Green)
            IconButton(
                onClick = onCallClick,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFFE0F2F1), // Light Green (Optional)
                    contentColor = Color(0xFF00695C)    // Dark Green
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Call Contact"
                )
            }
        }
    }
}