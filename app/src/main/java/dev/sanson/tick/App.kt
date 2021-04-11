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
import dev.sanson.tick.ui.ListTitleTextField
import dev.sanson.tick.ui.TodoRow
import dev.sanson.tick.ui.theme.TickTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import nz.sanson.tick.todo.Action.Companion.OnListTitleChange
import nz.sanson.tick.todo.Action.Companion.NewTodoItem
import nz.sanson.tick.todo.Action.Companion.OnTodoChange
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
    LazyColumn {
        item {
            ListTitle(list = list, dispatch = dispatch)

            Spacer(modifier = Modifier.height(Dp(8f)))
        }

        items(list.items) { item ->
            TodoRow(
                item = item,
                onTodoChange = { todo ->
                    dispatch(OnTodoChange(item = todo))
                },
                onDoneAction = {
                    dispatch(NewTodoItem(list = list))
                }
            )
        }
    }
}

@Composable
fun ListTitle(list: TodoList, dispatch: (Any) -> Any) {
    ListTitleTextField(
        value = list.title,
        onValueChange = {
            dispatch(OnListTitleChange(list = list, title = it))
        },
        onDoneAction = {
            dispatch(NewTodoItem(list = list))
        },
        modifier = Modifier.padding(start = Dp(16f))
    )
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