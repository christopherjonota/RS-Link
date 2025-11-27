package com.example.rs_link.feature_dashboard.safety

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.rs_link.R

@Composable
fun EmergencyContactScreen() {
    Scaffold { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)){
            Column {
                Row {
                    IconButton(
                        onClick = {},

                    ) {
                        Image(
                            contentDescription = null,
                            imageVector = Icons.Default.Person
                        )
                        Spacer(Modifier.width(12.dp))
                        Text("Emergency Contacts")
                    }
                }
                Image(
                    painter = painterResource(id = R.drawable.emergency_contact_illus),
                    contentDescription = null
                )
                Text("Add Emergency Contacts")
                Text("Your emergency contacts will be notified if you have trigger an alert. ")
                Text("Tip: Add trusted family members, or friends who can monitor you on your ride.")
                Button(
                    onClick = {}
                ) {
                    Text("+ Add Contact")
                }

            }
        }
    }

}