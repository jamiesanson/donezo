package dev.sanson.tick.screen.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import dev.sanson.tick.android.LocalDispatch
import dev.sanson.tick.model.Todo
import dev.sanson.tick.model.TodoList
import dev.sanson.tick.theme.TickTheme
import dev.sanson.tick.todo.Action
import dev.sanson.tick.todo.Screen

@Composable
fun ListScreen(state: Screen.Lists) {
    Column(modifier = Modifier.padding(top = Dp(16f))) {
        val dispatch = LocalDispatch.current
        TopAppBar {
            Text("")
            Icon(
                Icons.Filled.Settings, contentDescription = "Sync settings",
                Modifier.clickable {
                    dispatch(Action.Navigation.SyncSettings)
                }
            )
        }
        state.lists.forEach { list ->
            LazyColumn {
                item {
                    ListTitle(list = list)
                }

                items(list.items) { item ->
                    TodoRow(item = item)
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Single todo list")
@Composable
fun ListPreview() {
    TickTheme {
        Scaffold {
            ListScreen(
                state = Screen.Lists(
                    loading = false,
                    lists = listOf(
                        TodoList(
                            title = "Work, 23rd Feb",
                            items = listOf(
                                Todo(
                                    text = "Book that meeting",
                                    isDone = false
                                )
                            )
                        )
                    )
                )
            )
        }
    }
}
