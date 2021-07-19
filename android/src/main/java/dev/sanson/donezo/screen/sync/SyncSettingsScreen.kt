package dev.sanson.donezo.screen.sync

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import dev.sanson.donezo.android.LocalDispatch
import dev.sanson.donezo.backend.PresentableBackend
import dev.sanson.donezo.todo.Action
import dev.sanson.donezo.todo.AppState

@Composable
fun SyncSettingsScreen(state: AppState) {
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
                BackendRow(backend = it as PresentableBackend)
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
