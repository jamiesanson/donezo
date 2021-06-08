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

@Composable
fun ListScreen(lists: List<TodoList>, currentFocus: Any?) {
    val dispatch = LocalDispatch.current
    Column(
        modifier = Modifier
            .padding(top = Dp(16f))
            .onFocusEvent { if (!it.isFocused) dispatch(Action.ClearFocus) }
    ) {
        LazyColumn {
            for (list in lists) {
                item {
                    val requester = remember { FocusRequester() }
                    ListTitle(
                        title = list.title,
                        onValueChange = { dispatch(Action.UpdateListTitle(list, it)) },
                        onDoneAction = { dispatch(Action.AddTodo(list)) },
                        modifier = Modifier
                            .focusRequester(requester)
                            .onFocusChanged { if (it.isFocused) dispatch(Action.RequestFocus(list)) }
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
                        text = item.text,
                        isDone = item.isDone,
                        callbacks = TodoRowCallbacks(item, dispatch),
                        modifier = Modifier
                            .focusRequester(requester)
                            .onFocusChanged { if (it.isFocused) dispatch(Action.RequestFocus(item)) }
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
                ),
                null
            )
        }
    }
}
