package com.nhom5.dhqapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color

val BluePrimary = Color(0xFF2196F3)
val BluePrimaryDark = Color(0xFF1976D2)
val BlueLight = Color(0xFFBBDEFB)
val IncomeGreen = Color(0xFF4CAF50)
val ExpenseRed = Color(0xFFF44336)

private val LightColors = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    primaryContainer = BlueLight,
    background = Color(0xFFF5F7FA),
    surface = Color.White
)
private val DarkColors = darkColorScheme(
    primary = BlueLight,
    onPrimary = Color.Black,
    primaryContainer = BluePrimaryDark
)

@androidx.compose.runtime.Composable
fun DHQAppTheme(darkTheme: Boolean, content: @androidx.compose.runtime.Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}