package dev.sanson.tick.screen.sync

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import dev.sanson.tick.android.LocalDispatch
import dev.sanson.tick.backend.PresentableBackend
import dev.sanson.tick.todo.Action
import dev.sanson.tick.todo.Screen

@Composable
fun SyncSettingsScreen(state: Screen.SyncSettings) {
    val dispatch = LocalDispatch.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sync settings") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                           dispatch(Action.Navigation.Back)
                        }
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back to Lists")
                    }
                }
            )
        }
    ) {
        LazyColumn {
            items(state.backends) {
                BackendRow(backend = it)
            }
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BackendRow(backend: PresentableBackend) {
    ListItem(
        icon = {
            backend.Icon()
        },
        secondaryText = {
            Text(backend.description)
        },
        text = {
            Text(backend.title)
        }
    )
}