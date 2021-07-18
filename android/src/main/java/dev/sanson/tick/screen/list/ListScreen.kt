package dev.sanson.tick.screen.list

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import dev.sanson.tick.android.LocalDispatch
import dev.sanson.tick.model.Todo
import dev.sanson.tick.model.TodoList
import dev.sanson.tick.theme.TickTheme
import dev.sanson.tick.todo.Action
import kotlinx.coroutines.delay

@Composable
fun ListScreen(lists: List<TodoList>) {
    val focusManager = LocalFocusManager.current
    var focusDirectionToMove by remember { mutableStateOf<FocusDirection?>(null) }

    val dispatch = LocalDispatch.current
    val wrappedDispatch: (Any) -> Any = { action ->
        when (action) {
            is Action.AddTodo, is Action.AddTodoAsSibling -> focusDirectionToMove = FocusDirection.Down
            is Action.DeleteTodo -> focusDirectionToMove = FocusDirection.Up
        }

        dispatch(action)
    }

    TodoListColumn(lists, wrappedDispatch)

    val itemCount = derivedStateOf { lists.fold(0) { acc, list -> acc + 1 + list.items.size } }
    LaunchedEffect(itemCount) {
        // This makes focus traversals more consistent for now. Ideally we'd be able to use the focus node state as
        // the key for this effect, only traversing after an item can be moved to.
        delay(10)
        focusDirectionToMove?.let(focusManager::moveFocus)
        focusDirectionToMove = null
    }
}

@Composable
private fun TodoListColumn(
    lists: List<TodoList>,
    dispatch: (Any) -> Any
) {
    LazyColumn {
        for (list in lists) {
            item {
                ListTitle(
                    title = list.title,
                    onValueChange = { dispatch(Action.UpdateListTitle(list, it)) },
                    onDoneAction = { dispatch(Action.AddTodo(list)) }
                )
            }

            items(list.items, key = { it.id }) { item ->
                AnimatedTodoVisibility {
                    TodoRow(
                        text = item.text,
                        isDone = item.isDone,
                        callbacks = TodoRowCallbacks(item, dispatch)
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedTodoVisibility(block: @Composable () -> Unit) {
    val alpha = remember { Animatable(0F) }

    LaunchedEffect(true) {
        alpha.animateTo(1F)
    }

    Box(modifier = Modifier.graphicsLayer(alpha = alpha.value)) {
        block()
    }
}

@Preview(showBackground = true, name = "Single todo list")
@Composable
fun ListPreview() {
    TickTheme {
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
