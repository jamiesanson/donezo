package dev.sanson.donezo.screen.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import dev.sanson.donezo.android.LocalDispatch
import dev.sanson.donezo.model.Todo
import dev.sanson.donezo.model.TodoList
import dev.sanson.donezo.theme.DonezoTheme
import dev.sanson.donezo.todo.Action
import kotlinx.coroutines.delay

@Composable
fun ListScreen(lists: List<TodoList>, dispatch: (Any) -> Any = LocalDispatch.current) {
    val focusManager = LocalFocusManager.current
    var focusDirectionToMove by remember { mutableStateOf<FocusDirection?>(null) }

    val wrappedDispatch: (Any) -> Any = { action ->
        when (action) {
            is Action.AddTodo, is Action.AddTodoAfter -> focusDirectionToMove = FocusDirection.Down
            is Action.DeleteTodo, is Action.DeleteList -> focusDirectionToMove = FocusDirection.Up
        }

        dispatch(action)
    }

    TodoListColumn(lists, wrappedDispatch)

    val itemCount = derivedStateOf { lists.fold(0) { acc, list -> acc + 1 + list.items.size } }
    LaunchedEffect(itemCount) {
        // This makes focus traversals more consistent for now. Ideally we'd be able to use the focus node state as
        // the key for this effect, only traversing after an item can be moved to.
        if (focusDirectionToMove == FocusDirection.Down) delay(10)
        focusDirectionToMove?.let(focusManager::moveFocus)
        focusDirectionToMove = null
    }
}

@Composable
private fun TodoListColumn(
    lists: List<TodoList>,
    dispatch: (Any) -> Any,
) {
    LazyColumn {
        for (list in lists) {
            item {
                ListTitle(
                    title = list.title,
                    onValueChange = { dispatch(Action.UpdateListTitle(list, it)) },
                    onDoneAction = { dispatch(Action.AddTodo(list)) },
                    onDelete = { dispatch(Action.DeleteList(list)) }
                )
            }

            items(list.items) { item ->
                TodoRow(
                    text = item.text,
                    isDone = item.isDone,
                    onTodoTextChange = { dispatch(Action.UpdateTodoText(item, it)) },
                    onTodoCheckedChange = { dispatch(Action.UpdateTodoDone(item, it)) },
                    onImeAction = { dispatch(Action.AddTodoAfter(item)) },
                    onDelete = { dispatch(Action.DeleteTodo(item)) }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Single todo list")
@Composable
fun ListPreview() {
    DonezoTheme {
        Scaffold {
            ListScreen(
                listOf(
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
        }
    }
}
