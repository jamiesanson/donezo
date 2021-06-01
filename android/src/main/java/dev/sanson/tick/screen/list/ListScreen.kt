package dev.sanson.tick.screen.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import dev.sanson.tick.android.LocalDispatch
import dev.sanson.tick.model.Todo
import dev.sanson.tick.model.TodoList
import dev.sanson.tick.theme.TickTheme
import dev.sanson.tick.todo.Action
import dev.sanson.tick.todo.Screen
import kotlinx.coroutines.delay

@Composable
fun ListScreen(state: Screen.Lists) {
    val dispatch = LocalDispatch.current
    val focusManager = LocalFocusManager.current

    // TODO: Figure out a better way to react to state changes after the composition was successful
    val needsFocusDown = remember { mutableStateOf(false) }
    val updatedFocusDownState by rememberUpdatedState(needsFocusDown.value)

    Column(modifier = Modifier.padding(top = Dp(16f))) {
        LazyColumn {
            state.lists.forEach { list ->
                item {
                    ListTitle(
                        title = list.title,
                        onValueChange = { dispatch(Action.UpdateListTitle(list, it)) },
                        onDoneAction = { dispatch(Action.AddTodo(list)) },
                    )
                }

                items(list.items) { item ->
                    TodoRow(
                        item = item,
                        onTodoTextChange = { dispatch(Action.UpdateTodoText(item, it)) },
                        onTodoCheckedChange = { dispatch(Action.UpdateTodoDone(item, it)) },
                        onDeleteItem = { dispatch(Action.DeleteTodo(item)) },
                        onImeAction = {
                            dispatch(Action.AddTodoAsSibling(item))
                            needsFocusDown.value = true
                        }
                    )
                }
            }
        }
    }

    LaunchedEffect(needsFocusDown.value) {
        delay(100)

        if (updatedFocusDownState) {
            focusManager.moveFocus(FocusDirection.Down)
            needsFocusDown.value = false
        }
    }
}

@Preview(showBackground = true, name = "Single todo list")
@Composable
fun ListPreview() {
    TickTheme {
        Scaffold {
            ListScreen(
                Screen.Lists(
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
