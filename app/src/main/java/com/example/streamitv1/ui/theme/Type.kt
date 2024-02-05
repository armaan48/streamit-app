package com.example.streamitv1.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.streamitv1.R

val rosarioFamily = FontFamily(
    Font(R.font.rosario_light, FontWeight.Light),
    Font(R.font.rosario_light, FontWeight.Light,  FontStyle.Italic),
    Font(R.font.rosario_regular, FontWeight.Normal),
    Font(R.font.rosario_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.rosario_medium, FontWeight.Medium),
    Font(R.font.rosario_medium, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.rosario_bold, FontWeight.Bold),
    Font(R.font.rosario_bold, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.rosario_semibold, FontWeight.SemiBold),
    Font(R.font.rosario_semibold, FontWeight.SemiBold , FontStyle.Italic),
)
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = rosarioFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)