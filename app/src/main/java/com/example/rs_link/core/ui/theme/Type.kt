// ui/theme/Type.kt

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.rs_link.R

// Define your custom font family if you use one (optional)
//val CustomFont = FontFamily(
//    //Font(R.font.roboto_regular, FontWeight.Normal),
//    // e.g., Font(R.font.roboto_bold, FontWeight.Bold)
//)

// Create the Typography object based on the Material 3 type scale

val nunitoFontFamily = FontFamily(
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_black, FontWeight.Black),
    Font(R.font.nunito, FontWeight.Normal)
)
val mulishFontFamily = FontFamily(
    Font(R.font.mulish_bold, FontWeight.Bold),
    Font(R.font.mulish_medium, FontWeight.Medium),
    Font(R.font.mulish_regular, FontWeight.Normal)
)

val AppTypography = Typography(

    // Used for title
    headlineLarge = TextStyle(
        fontFamily = nunitoFontFamily, // Use your custom font or FontFamily.Default
        fontWeight = FontWeight.Black,
        fontSize = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontSize = 16.sp
    ),

    // Customizing the body text
    bodyLarge = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // You can customize any other style, like titleMedium, labelSmall, etc.
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    )
    /*
    If you don't override a style (e.g., displayLarge), it will fall back 
    to the Material 3 defaults.
    */
)