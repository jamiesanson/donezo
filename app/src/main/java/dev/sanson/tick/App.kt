package dev.sanson.tick

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import dev.sanson.tick.ui.theme.TickTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import nz.sanson.tick.todo.AppState
import nz.sanson.tick.todo.Screen
import nz.sanson.tick.todo.model.Todo
import nz.sanson.tick.todo.model.TodoList

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun App(stateFlow: StateFlow<AppState>, dispatch: (Any) -> Any) {
    TickTheme {
        Surface(color = MaterialTheme.colors.background) {
            val state = stateFlow.collectAsState()

            when (val screen = state.value.screen) {
                is Screen.Splash -> SplashScreen()
                is Screen.Lists -> ListScreen(state = screen, dispatch = dispatch)
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
fun ListScreen(state: Screen.Lists, dispatch: (Any) -> Any) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        state.lists.forEach { list ->
            TodoList(list = list, dispatch = dispatch)
        }
    }
}

@Composable
fun TodoList(list: TodoList, dispatch: (Any) -> Any) {
    Column {
        TextField(value = list.title, onValueChange = { })
        list.items.forEach {
            TodoRow(item = it)
        }
    }
}

@Composable
fun TodoRow(item: Todo) {
    Text(text = item.text + "; done: ${item.isDone}")
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
        TodoList(
            list = TodoList(
                title = "Work, 23rd Feb",
                items = listOf(
                    Todo(
                        text = "Book that meeting",
                        isDone = false
                    )
                )
            ),
            dispatch = {}
        )
    }
}