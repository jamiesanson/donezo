package dev.sanson.donezo.screen.list

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.layout.RelocationRequester
import androidx.compose.ui.layout.relocationRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import dev.sanson.donezo.android.LocalDispatch
import dev.sanson.donezo.model.Todo
import dev.sanson.donezo.model.TodoList
import dev.sanson.donezo.theme.DonezoTheme
import dev.sanson.donezo.todo.Action
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun ListScreen(lists: List<TodoList>, dispatch: (Any) -> Any = LocalDispatch.current) {
    val focusManager = LocalFocusManager.current

    var previousAction by remember { mutableStateOf<Any?>(null) }

    val wrappedDispatch: (Any) -> Any = { action ->
        dispatch(action).also {
            previousAction = action
        }
    }

    TodoListColumn(lists, wrappedDispatch)

    SideEffect {
        val action = previousAction ?: return@SideEffect

        val focusDirection = when {
            action is Action.DeleteList -> FocusDirection.Up
            action is Action.DeleteTodo && lists.find { it.items.contains(action.item) }?.items?.size ?: 0 > 1 -> FocusDirection.Up
            else -> null
        }

        if (focusDirection != null) {
            focusManager.moveFocus(focusDirection)
        }
    }
}

/**
 * Custom scroll modifier extension which ties together focus listening and relocation requesting
 */
@SuppressLint("UnnecessaryComposedModifier")
@ExperimentalComposeUiApi
private fun Modifier.scrollToOnFocus() = composed {
    val scope = rememberCoroutineScope()
    val requester = remember { RelocationRequester() }

    relocationRequester(requester).onFocusEvent {
        if (it.isFocused) {
            scope.launch {
                delay(250)
                requester.bringIntoView()
            }
        }
    }
}

@SuppressLint("UnnecessaryComposedModifier")
private fun Modifier.focusOnEntry() = composed {
    val imeInsets = LocalWindowInsets.current.ime
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(true) {
        if (imeInsets.isVisible) {
            focusRequester.requestFocus()
        }
    }

    focusRequester(focusRequester = focusRequester)
}


// NOTE: This would be more performant if it was using [items], however due to a current
// limitation of relocationRequester, the composable being relocated to much be in the composition
// but off screen. When not in a [Column], the composable offscreen does not exist within the composition
// and therefore behaves badly
@OptIn(ExperimentalComposeUiApi::class, ExperimentalStdlibApi::class)
@Composable
private fun TodoListColumn(
    lists: List<TodoList>,
    dispatch: (Any) -> Any,
) {
    val scrollState = rememberScrollState()

    val imeInsets = LocalWindowInsets.current.ime

    // Ensure the column scrolls as the IME insets change
    LaunchedEffect(true) {
        snapshotFlow { imeInsets.bottom }
            .collect { value ->
                scrollState.scrollBy(value.toFloat())
            }
    }

    Column(modifier = Modifier.verticalScroll(scrollState)) {

        Spacer(modifier = Modifier.height(16.dp))

        for (list in lists) {
            ListTitle(
                title = list.title,
                onValueChange = { dispatch(Action.UpdateListTitle(list, it)) },
                onDoneAction = { dispatch(Action.AddTodo(list)) },
                onDelete = { dispatch(Action.DeleteList(list)) },
                modifier = Modifier
                    .scrollToOnFocus()
                    .focusOnEntry()
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
