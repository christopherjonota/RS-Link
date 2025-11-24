package com.example.rs_link.feature_dashboard.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun HomeScreen (
    viewModel: HomeViewModel = hiltViewModel(),
){
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.primary)
            .height(20.dp).width(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "hello"
        )
    }
}