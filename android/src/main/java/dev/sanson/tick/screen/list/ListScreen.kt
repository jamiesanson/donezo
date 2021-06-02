package dev.sanson.tick.screen.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import dev.sanson.tick.android.LocalDispatch
import dev.sanson.tick.model.Todo
import dev.sanson.tick.model.TodoList
import dev.sanson.tick.theme.TickTheme
import dev.sanson.tick.todo.Action
import dev.sanson.tick.todo.Screen
import org.reduxkotlin.Dispatcher

// Focus-related actions intercepted in [ListScreen]
private data class OnFocusRequested(val item: Any)
private object OnFocusCleared

@Composable
fun ListScreen(state: Screen.Lists) {
    val dispatch = LocalDispatch.current

    val currentFocus = remember { mutableStateOf<Any?>(null) }
    val updatedState = rememberUpdatedState(state)

    val wrappedDispatch: Dispatcher = { action ->
        when (action) {
            is OnFocusRequested -> currentFocus.value = action.item
            OnFocusCleared -> currentFocus.value = null
            is Action.AddTodo -> {
                dispatch(action)
                val focus = updatedState.value.lists
                    .first { it == action.list }
                    .items.last()

                currentFocus.value = focus
            }
            else -> dispatch(action)
        }
    }

    TodoLists(lists = state.lists, currentFocus = currentFocus.value, dispatch = wrappedDispatch)
}

@Composable
fun TodoLists(lists: List<TodoList>, currentFocus: Any?, dispatch: (Any) -> Any) {
    Column(
        modifier = Modifier
            .padding(top = Dp(16f))
            .onFocusEvent { if (!it.isFocused) dispatch(OnFocusCleared) }
    ) {
        LazyColumn {
            lists.forEach { list ->
                item {
                    val requester = remember { FocusRequester() }
                    ListTitle(
                        title = list.title,
                        onValueChange = { dispatch(Action.UpdateListTitle(list, it)) },
                        onDoneAction = { dispatch(Action.AddTodo(list)) },
                        modifier = Modifier
                            .focusRequester(requester)
                            .onFocusChanged { if (it.isFocused) dispatch(OnFocusRequested(list)) }
                    )

                    SideEffect {
                        if (list == currentFocus) {
                            requester.requestFocus()
                        }
                    }
                }

                items(list.items) { item ->
                    val requester = remember { FocusRequester() }

                    TodoRow(
                        item = item,
                        onTodoTextChange = { dispatch(Action.UpdateTodoText(item, it)) },
                        onTodoCheckedChange = { dispatch(Action.UpdateTodoDone(item, it)) },
                        onDeleteItem = { dispatch(Action.DeleteTodo(item)) },
                        onImeAction = {
                            dispatch(Action.AddTodoAsSibling(item))
                        },
                        modifier = Modifier
                            .focusRequester(requester)
                            .onFocusChanged { if (it.isFocused) dispatch(OnFocusRequested(item)) }
                    )

                    SideEffect {
                        if (item == currentFocus) {
                            requester.requestFocus()
                        }
                    }
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
