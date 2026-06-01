package com.example.coinminder.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    background = BackgroundGray,
    surface = SurfaceWhite,
    onPrimary = SurfaceWhite,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun CoinMinderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}