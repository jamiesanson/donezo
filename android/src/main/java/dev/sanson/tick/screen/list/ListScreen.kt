package dev.sanson.tick.screen.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import dev.sanson.tick.android.LocalDispatch
import dev.sanson.tick.model.Todo
import dev.sanson.tick.model.TodoList
import dev.sanson.tick.theme.TickTheme
import dev.sanson.tick.todo.Action
import dev.sanson.tick.todo.Screen

// TODO - replace with stdlib definition when not experimental
fun <T> buildList(action: MutableList<T>.() -> Unit): List<T> {
    return mutableListOf<T>().apply(action).toList()
}

class ListModel(
    initialState: Screen.Lists,
    private val dispatch: (Any) -> Any
) {

    val rows = derivedStateOf { screenState.value.mapToRows() }
    val currentFocus = mutableStateOf(-1)

    private val screenState = mutableStateOf(initialState)

    sealed class Row {
        data class Title(val title: String) : Row()
        data class Item(val isDone: Boolean, val text: String) : Row()
    }

    fun updateTitleRow(index: Int, title: String) {
        dispatch(Action.UpdateListTitle(list = ))
    }

    fun onStateChange(state: Screen.Lists) {
        screenState.value = state
    }

    private fun Screen.Lists.mapToRows(): List<Row> {
        return screenState.value.lists.flatMap { list ->
            buildList {
                add(Row.Title(list.title))
                addAll(list.items.map { Row.Item(it.isDone, it.text) })
            }
        }
    }
}

@Composable
fun listModel(state: Screen.Lists): ListModel {
    val dispatch = LocalDispatch.current
    val bloc = remember { ListModel(state, dispatch) }

    LaunchedEffect(state) {
        bloc.onStateChange(state)
    }

    return bloc
}

@Composable
fun ListScreen(_state: Screen.Lists) {
    val model = listModel(state = _state)

    Column(modifier = Modifier.padding(top = Dp(16f))) {
        val indexedRows = model.rows.value.withIndex().toList()

        LazyColumn {
            items(indexedRows) { (index, row) ->
                when (row) {
                    is ListModel.Row.Title -> ListTitle(
                        title = row.title,
                        onValueChange = { model.updateTitleRow(index, it) },
                        onDoneAction = { },
                    )
                    is ListModel.Row.Item -> TodoRow(item = TODO())
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
