package com.example.rs_link.feature_dashboard.settings

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun SettingsScreen (
    viewModel: SettingViewModel = hiltViewModel(),
    onLogOut: () -> Unit
){
    Button(
        onClick = {viewModel.logout(onLogOut = onLogOut)}
    ) {
        Text("hello")
    }
}