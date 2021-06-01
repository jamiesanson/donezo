package dev.sanson.tick.screen.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import dev.sanson.tick.android.LocalDispatch
import dev.sanson.tick.model.Todo
import dev.sanson.tick.model.TodoList
import dev.sanson.tick.theme.TickTheme
import dev.sanson.tick.todo.Action
import dev.sanson.tick.todo.Screen

@Composable
fun ListScreen(state: Screen.Lists) {
    val dispatch = LocalDispatch.current

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
                        onImeAction = { dispatch(Action.AddTodoAsSibling(item)) }
                    )
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
