package dev.sanson.tick

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import dev.sanson.tick.ui.TickTextField
import dev.sanson.tick.ui.TodoRow
import dev.sanson.tick.ui.theme.TickTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import nz.sanson.tick.todo.Action
import nz.sanson.tick.todo.AppState
import nz.sanson.tick.todo.Screen
import nz.sanson.tick.todo.model.Todo
import nz.sanson.tick.todo.model.TodoList

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun App(stateFlow: StateFlow<AppState>, dispatch: (Any) -> Any) {
    TickTheme {
        Surface(color = MaterialTheme.colors.surface, modifier = Modifier.fillMaxSize()) {
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
    Column(modifier = Modifier.padding(top = Dp(16f))) {
        state.lists.forEach { list ->
            TodoList(list = list, dispatch = dispatch)
        }
    }
}

@Composable
fun TodoList(list: TodoList, dispatch: (Any) -> Any) {
    Column {
        TickTextField(
            value = list.title,
            onValueChange = { dispatch(Action.ListTitleUpdated(list = list, title = it)) },
            modifier = Modifier.padding(start = Dp(16f))
        )

        list.items.forEach { todo ->
            TodoRow(
                item = todo,
                onTitleTextChanged = { dispatch(Action.TodoItemUpdated(item = todo, text = it)) },
                onDoneChanged = { dispatch(Action.TodoItemUpdated(item = todo, isDone = it)) }
            )
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
}