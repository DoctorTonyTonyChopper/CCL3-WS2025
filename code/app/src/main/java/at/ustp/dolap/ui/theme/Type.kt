package at.ustp.dolap.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import at.ustp.dolap.R

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )

)

val CuteBubblyFont = FontFamily(
    Font(R.font.bubblegumsans_regular, FontWeight.Normal)
)

val LogoTextStyle = TextStyle(
    fontFamily = CuteBubblyFont,
    fontWeight = FontWeight.Normal,
    fontSize = 30.sp,
    letterSpacing = 1.2.sp
)