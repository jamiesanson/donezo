package dev.sanson.tick

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.sanson.tick.screen.list.ListScreen
import dev.sanson.tick.screen.splash.SplashScreen
import dev.sanson.tick.theme.TickTheme
import nz.sanson.tick.todo.AppState
import nz.sanson.tick.todo.Screen

@Composable
fun App(state: AppState) {
    TickTheme {
        Surface(color = MaterialTheme.colors.surface, modifier = Modifier.fillMaxSize()) {
            when (val screen = state.screen) {
                is Screen.Splash -> SplashScreen()
                is Screen.Lists -> ListScreen(state = screen)
            }
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    App(state = AppState(screen = Screen.Splash))
}
