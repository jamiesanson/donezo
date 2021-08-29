package dev.sanson.donezo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.ui.Scaffold
import dev.sanson.donezo.screen.list.ListScreen
import dev.sanson.donezo.screen.sync.SyncSettingsScreen
import dev.sanson.donezo.theme.DonezoTheme
import dev.sanson.donezo.todo.AppState
import dev.sanson.donezo.todo.feature.navigation.Screen

@Composable
fun App(state: AppState) {
    DonezoTheme {
        ProvideWindowInsets {
            Scaffold(
                backgroundColor = MaterialTheme.colors.surface,
            ) {
                Box(
                    modifier = Modifier
                        .navigationBarsWithImePadding()
                        .fillMaxSize()
                ) {
                    when (state.navigation.currentScreen) {
                        is Screen.Lists -> ListScreen(lists = state.lists)
                        is Screen.SyncSettings -> SyncSettingsScreen(state = state)
                    }
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
