package dev.sanson.donezo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.sanson.donezo.screen.list.ListScreen
import dev.sanson.donezo.screen.sync.SyncSettingsScreen
import dev.sanson.donezo.theme.DonezoTheme
import dev.sanson.donezo.todo.AppState
import dev.sanson.donezo.todo.feature.navigation.Screen

@Composable
fun App(state: AppState) {
    DonezoTheme {
        Surface(color = MaterialTheme.colors.surface, modifier = Modifier.fillMaxSize()) {
            when (state.navigation.currentScreen) {
                is Screen.Lists -> ListScreen(lists = state.lists)
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