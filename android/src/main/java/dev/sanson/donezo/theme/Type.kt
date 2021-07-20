package dev.sanson.donezo.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.sanson.donezo.R

/**
 * The raleway family is a san-serif typeface used for body text.
 * https://fonts.google.com/specimen/Raleway
 */
val ralewayFontFamily = FontFamily(
    Font(R.font.raleway_light, FontWeight.Light),
    Font(R.font.raleway_regular, FontWeight.Normal),
    Font(R.font.raleway_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.raleway_medium, FontWeight.Medium),
    Font(R.font.raleway_semibold, FontWeight.SemiBold),
    Font(R.font.raleway_bold, FontWeight.Bold),
)

/**
 * The quicksand font family is a san-serif typeface used for headers.
 * https://fonts.google.com/specimen/Quicksand
 */
val quicksandFontFamily = FontFamily(
    Font(R.font.quicksand_light, FontWeight.Light),
    Font(R.font.quicksand_regular, FontWeight.Normal),
    Font(R.font.quicksand_medium, FontWeight.Medium),
    Font(R.font.quicksand_semibold, FontWeight.SemiBold),
    Font(R.font.quicksand_bold, FontWeight.Bold),
)

val typography = Typography().run {
    copy(
        h1 = TextStyle(
            fontFamily = quicksandFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = h1.fontSize
        ),
        h2 = TextStyle(
            fontFamily = quicksandFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = h2.fontSize
        ),
        h3 = TextStyle(
            fontFamily = quicksandFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = h3.fontSize
        ),
        h4 = TextStyle(
            fontFamily = quicksandFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = h4.fontSize
        ),
        h5 = TextStyle(
            fontFamily = quicksandFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 30.sp
        ),
        h6 = TextStyle(
            fontFamily = quicksandFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = h6.fontSize
        ),
        body1 = TextStyle(
            fontFamily = ralewayFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp
        )
    )
}
