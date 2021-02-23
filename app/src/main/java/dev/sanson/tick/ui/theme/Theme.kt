package dev.sanson.tick.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

private val TickColorPalette = darkColors(
    primary = purple500,
    primaryVariant = purple700,
    secondary = yellow500,
    surface = blueGrey700,
    background = blueGrey700
)

@Composable
fun TickTheme(content: @Composable () -> Unit) {
    val colors = TickColorPalette

    MaterialTheme(
            colors = colors,
            typography = typography,
            shapes = shapes,
            content = content
    )
}