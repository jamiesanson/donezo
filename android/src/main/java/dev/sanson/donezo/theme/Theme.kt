package dev.sanson.donezo.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun DonezoTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkDonezoPalette else LightDonezoPalette

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,
        content = content,
    )
}
