// ui/theme/Type.kt

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Define your custom font family if you use one (optional)
val CustomFont = FontFamily(
    // e.g., Font(R.font.roboto_regular, FontWeight.Normal),
    // e.g., Font(R.font.roboto_bold, FontWeight.Bold)
)

// Create the Typography object based on the Material 3 type scale
val AppTypography = Typography(
    // Customizing the standard headline for a heavier, larger look
    headlineLarge = TextStyle(
        fontFamily = CustomFont, // Use your custom font or FontFamily.Default
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    // Customizing the body text
    bodyLarge = TextStyle(
        fontFamily = CustomFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // You can customize any other style, like titleMedium, labelSmall, etc.
    titleMedium = TextStyle(
        fontFamily = CustomFont,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    )
    /*
    If you don't override a style (e.g., displayLarge), it will fall back 
    to the Material 3 defaults.
    */
)