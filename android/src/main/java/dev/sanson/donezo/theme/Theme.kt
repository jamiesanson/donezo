package dev.sanson.donezo.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

private val DonezoColorPalette = darkColors(
    primary = purple500,
    primaryVariant = purple700,
    secondary = yellow500,
    surface = blueGrey700,
    background = blueGrey700
)

@Composable
fun DonezoTheme(content: @Composable () -> Unit) {
    val colors = DonezoColorPalette

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
