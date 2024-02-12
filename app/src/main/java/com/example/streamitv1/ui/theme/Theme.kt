package com.example.streamitv1.ui.theme

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val DarkColorScheme = darkColorScheme(
    primary = ChalkBlack,
    secondary = OffWhite,
    tertiary = OffGrey,
    onTertiary =OffGrey2,
    onError = ErrorColorB,
    onPrimary = DarkBlack
)

val LightColorScheme = lightColorScheme(
    primary = OffWhite,
    secondary = ChalkBlack,
    tertiary = OffGrey,
    onTertiary =OffGrey2,
    onError = ErrorColorW2,
    onErrorContainer = ErrorColorW ,
    onPrimary = OffGrey2
)

@Composable
fun StreamitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val primaryColor = colorScheme.primary
            window.statusBarColor = primaryColor.toArgb()
            window.navigationBarColor = primaryColor.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}