package dev.sanson.donezo.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val orange = Color(0xFFEC9B3B)
private val lemon = Color(0xFFFFEDA3)
private val beige = Color(0xFFF4F4E8)
private val darkGrey = Color(0xFF232323)

val LightDonezoPalette =
    lightColorScheme(
        secondary = orange,
        surface = beige,
        background = beige,
    )

val DarkDonezoPalette =
    darkColorScheme(
        secondary = lemon,
        surface = darkGrey,
        background = darkGrey,
    )
