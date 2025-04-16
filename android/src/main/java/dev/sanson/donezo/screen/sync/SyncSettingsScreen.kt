package dev.sanson.donezo.screen.sync

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.sanson.donezo.android.LocalDispatch
import dev.sanson.donezo.backend.BackendMenuItem
import dev.sanson.donezo.todo.Action
import dev.sanson.donezo.todo.AppState

@OptIn(ExperimentalMaterial3Api::class)
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
                        },
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back to Lists")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            items(state.backends) {
                BackendRow(backend = it.ui.backendMenuItem)
            }
        }
    }
}

@Composable
fun BackendRow(backend: BackendMenuItem) {
    ListItem(
        leadingContent = {
            backend.Icon()
        },
        supportingContent = {
            Text(backend.description)
        },
        headlineContent = {
            Text(backend.title)
        },
    )
}
