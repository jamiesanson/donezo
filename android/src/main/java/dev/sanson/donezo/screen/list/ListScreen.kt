package dev.sanson.donezo.screen.list

import androidx.compose.animation.core.Spring.StiffnessHigh
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sanson.donezo.android.LocalDispatch
import dev.sanson.donezo.model.Todo
import dev.sanson.donezo.model.TodoList
import dev.sanson.donezo.theme.DonezoTheme
import dev.sanson.donezo.todo.Action

@Composable
fun ListScreen(lists: List<TodoList>, dispatch: (Any) -> Any = LocalDispatch.current) {
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    var focusDirectionToMove by remember { mutableStateOf<FocusDirection?>(null) }

    val wrappedDispatch: (Any) -> Any = { action ->
        when (action) {
            is Action.AddTodo, is Action.AddTodoAfter -> focusDirectionToMove = FocusDirection.Down
            is Action.DeleteTodo, is Action.DeleteList -> focusDirectionToMove = FocusDirection.Up
        }

        dispatch(action)
    }

    val listState = rememberLazyListState()

    TodoListColumn(listState, lists, wrappedDispatch)

    val itemCount = derivedStateOf { lists.fold(0) { acc, list -> acc + 1 + list.items.size } }
    LaunchedEffect(itemCount) {
        val focusDirection = focusDirectionToMove

        if (focusDirection != null) {
            val itemHeightPx = density.run { 64.dp.toPx() }
            listState.animateScrollBy(
                value = if (focusDirection == FocusDirection.Down) itemHeightPx else -itemHeightPx,
                animationSpec = spring(stiffness = StiffnessHigh)
            )

            focusManager.moveFocus(focusDirection = focusDirection)
        }

        focusDirectionToMove = null
    }
}

@Composable
private fun TodoListColumn(
    lazyListState: LazyListState,
    lists: List<TodoList>,
    dispatch: (Any) -> Any,
) {
    LazyColumn(state = lazyListState) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

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
