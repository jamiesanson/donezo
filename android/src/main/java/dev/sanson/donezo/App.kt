package dev.sanson.donezo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
        Scaffold { contentPadding ->
            Box(
                modifier =
                    Modifier
                        .padding(contentPadding)
                        .imePadding()
                        .fillMaxSize(),
            ) {
                when (state.navigation.currentScreen) {
                    is Screen.Lists -> ListScreen(lists = state.lists)
                    is Screen.SyncSettings -> SyncSettingsScreen(state = state)
                }
            }
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    App(state = AppState())
}
