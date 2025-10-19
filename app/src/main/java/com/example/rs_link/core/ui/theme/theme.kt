package com.example.rs_link.core.ui.theme

import AppTypography
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

import androidx.compose.material3.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

enum class AppThemeMode {
    DEFAULT,
    DARK,
    HIGH_CONTRAST
}

@Composable
fun ThemeRSLink(
    mode: AppThemeMode = AppThemeMode.DEFAULT,
    content: @Composable () -> Unit
) {
    // 1. Select the color scheme based on the system setting
    val colorScheme = when(mode) {
        AppThemeMode.DEFAULT -> DefaultScheme
        else -> DefaultScheme
    }

    // 2. Apply the theme to all wrapped content
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        // shapes = AppShapes, // Optional, if you define custom shapes
        content = content
    )
}