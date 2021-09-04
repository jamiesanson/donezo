package dev.sanson.donezo.screen.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sanson.donezo.android.LocalDispatch
import dev.sanson.donezo.model.Todo
import dev.sanson.donezo.model.TodoList
import dev.sanson.donezo.modifiers.focusOnEntry
import dev.sanson.donezo.modifiers.scrollToOnFocus
import dev.sanson.donezo.theme.DonezoTheme
import dev.sanson.donezo.todo.Action

@Composable
fun ListScreen(lists: List<TodoList>, dispatch: (Any) -> Any = LocalDispatch.current) {
    val focusManager = LocalFocusManager.current

    val wrappedDispatch: (Any) -> Any = { action ->
        dispatch(action).also {
            if (action is Action.DeleteList || action is Action.DeleteTodo) {
                focusManager.moveFocus(FocusDirection.Up)
            }
        }
    }

    TodoListColumn(lists, wrappedDispatch)
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalStdlibApi::class)
@Composable
private fun TodoListColumn(
    lists: List<TodoList>,
    dispatch: (Any) -> Any,
) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.verticalScroll(scrollState)) {

        Spacer(modifier = Modifier.height(24.dp))

        for (list in lists) {
            val focusLastListOnEntry =
                lists.indexOf(list) == lists.size - 1 && list.title.isEmpty() && list.items.isEmpty()

            ListTitle(
                title = list.title,
                onValueChange = { dispatch(Action.UpdateListTitle(list, it)) },
                onDoneAction = { dispatch(Action.AddTodo(list)) },
                onDelete = { dispatch(Action.DeleteList(list)) },
                modifier = Modifier
                    .scrollToOnFocus()
                    .focusOnEntry(ignoreImeVisibility = focusLastListOnEntry)
            )

            for (item in list.items) {
                TodoRow(
                    text = item.text,
                    isDone = item.isDone,
                    onTodoTextChange = { dispatch(Action.UpdateTodoText(item, it)) },
                    onTodoCheckedChange = { dispatch(Action.UpdateTodoDone(item, it)) },
                    onImeAction = { dispatch(Action.AddTodoAfter(item)) },
                    onDelete = { dispatch(Action.DeleteTodo(item)) },
                    modifier = Modifier
                        .scrollToOnFocus()
                        .focusOnEntry()
                )
            }
        }

        val showAddListRow =
            lists.isEmpty() || lists.last().title.isNotEmpty() && lists.last().items.isNotEmpty()

        if (showAddListRow) {
            AddListRow(dispatch)
        }
    }
}

@Composable
private fun AddListRow(dispatch: (Any) -> Any) {
    val source = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = source,
                indication = null
            ) { dispatch(Action.AddList) }
    ) {
        Text(
            text = "Start something new",
            style = MaterialTheme.typography.h5.copy(
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.4f),
            ),
            modifier = Modifier.padding(24.dp)
        )
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
