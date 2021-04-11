package dev.sanson.tick

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import dev.sanson.tick.ui.ListTitle
import dev.sanson.tick.ui.TodoRow
import dev.sanson.tick.ui.theme.TickTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import nz.sanson.tick.todo.AppState
import nz.sanson.tick.todo.Screen
import nz.sanson.tick.todo.model.Todo
import nz.sanson.tick.todo.model.TodoList

@OptIn(ExperimentalCoroutinesApi::class)
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

@Composable
fun SplashScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "\uD83D\uDCA6\uD83D\uDCA6\uD83D\uDCA6")
    }
}

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

@Preview(showBackground = true)
@Composable
fun SplashPreview() {
    TickTheme {
        SplashScreen()
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