package dev.sanson.tick

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.sanson.tick.screen.list.ListScreen
import dev.sanson.tick.screen.sync.SyncSettingsScreen
import dev.sanson.tick.theme.TickTheme
import dev.sanson.tick.todo.AppState
import dev.sanson.tick.todo.feature.navigation.Screen

@Composable
fun App(state: AppState) {
    TickTheme {
        Surface(color = MaterialTheme.colors.surface, modifier = Modifier.fillMaxSize()) {
            when (val screen = state.navigation.currentScreen) {
                is Screen.Lists -> ListScreen(
                    lists = state.lists,
                    currentFocus = screen.focussedItem
                )
                is Screen.SyncSettings -> SyncSettingsScreen(state = state)
            }
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    App(state = AppState())
}
