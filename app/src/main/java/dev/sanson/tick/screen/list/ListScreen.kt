package dev.sanson.tick.screen.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import dev.sanson.tick.ui.ListTitle
import dev.sanson.tick.ui.TodoRow
import dev.sanson.tick.ui.theme.TickTheme
import nz.sanson.tick.todo.Screen
import nz.sanson.tick.todo.model.Todo
import nz.sanson.tick.todo.model.TodoList

@Composable
fun ListScreen(state: Screen.Lists) {
    Column(modifier = Modifier.padding(top = Dp(16f))) {
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