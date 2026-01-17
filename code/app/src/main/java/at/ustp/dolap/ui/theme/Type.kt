package at.ustp.dolap.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.unit.sp
import at.ustp.dolap.R

// Decorative brand font (LOGO ONLY)
val CuteBubblyFont = FontFamily(
    Font(R.font.bubblegumsans_regular, FontWeight.Normal)
)

// App typography
val Typography = Typography(

    // 🔥 BRAND / LOGO STYLE
    displaySmall = TextStyle(
        fontFamily = CuteBubblyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 34.sp,
        letterSpacing = 1.2.sp
    ),

    // Headlines (screen titles)
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp
    ),

    titleLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    ),

    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),

    // Body text
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),

    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),

    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),

    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    )
)