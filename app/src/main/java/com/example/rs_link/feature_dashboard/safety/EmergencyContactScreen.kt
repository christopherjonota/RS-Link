package com.example.rs_link.feature_dashboard.safety

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rs_link.R

@Composable
fun EmergencyContactScreen(
    onNavigateBack: () -> Unit
) {
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
                            onClick = {},
                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                        ) {
                            Text("+ Add Contact")
                        }
                    }

                }


            }
        }
    }

}