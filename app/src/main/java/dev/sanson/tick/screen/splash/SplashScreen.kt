package dev.sanson.tick.screen.splash

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import dev.sanson.tick.theme.TickTheme

@Composable
fun SplashScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "\uD83D\uDCA6\uD83D\uDCA6\uD83D\uDCA6")
    }
}

@Preview(showBackground = true)
@Composable
fun SplashPreview() {
    TickTheme {
        SplashScreen()
    }
}
