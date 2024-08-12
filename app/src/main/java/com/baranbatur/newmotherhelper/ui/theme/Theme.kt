package com.baranbatur.newmotherhelper.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    background = BackgroundColor,
    surface = WhiteColor,
    onPrimary = WhiteColor,
    onSecondary = WhiteColor,
    onBackground = AccentColor,
    onSurface = AccentColor
)
private val DarkColors = darkColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    background = BackgroundColor,
    surface = WhiteColor,
    onPrimary = WhiteColor,
    onSecondary = WhiteColor,
    onBackground = AccentColor,
    onSurface = AccentColor
)

@Composable
fun NewMotherHelperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
