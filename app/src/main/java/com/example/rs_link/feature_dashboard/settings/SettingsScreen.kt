package com.example.rs_link.feature_dashboard.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.rs_link.feature_dashboard.safety.SafetyScreenItem

@Composable
fun SettingsScreen (
    viewModel: SettingViewModel = hiltViewModel(),
    onLogOut: () -> Unit
){
    Box(
        modifier = Modifier.fillMaxSize()
    ){

        Column (modifier = Modifier.fillMaxSize().align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .height(80.dp)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ){
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
            Column(
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
            ) {
                SafetyScreenItem("Crash Detection & Alert")
                Spacer(Modifier.height(20.dp))
                SafetyScreenItem("Emergency Contacts", "0 contacts added")
                Spacer(Modifier.height(20.dp))
                SafetyScreenItem("Crash Detection & Alert")
                Spacer(Modifier.height(20.dp))
                SafetyScreenItem("Emergency Contacts", "0 contacts added")
                Button(
                    onClick = {viewModel.logout(onLogOut = onLogOut)}
                ) {
                    Text("hello")
                }
            }


        }
    }

}