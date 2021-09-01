package dev.sanson.donezo.screen.list

import androidx.compose.animation.core.Spring.StiffnessHigh
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.layout.RelocationRequester
import androidx.compose.ui.layout.relocationRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sanson.donezo.android.LocalDispatch
import dev.sanson.donezo.model.Todo
import dev.sanson.donezo.model.TodoList
import dev.sanson.donezo.theme.DonezoTheme
import dev.sanson.donezo.todo.Action
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

        // Move up immediately, as the composable above the current one probably exists
        if (focusDirectionToMove == FocusDirection.Up) {
            focusManager.moveFocus(FocusDirection.Up)
            focusDirectionToMove = null
        }

        dispatch(action)
    }

    val scrollState = rememberScrollState()

    TodoListColumn(scrollState, lists, wrappedDispatch)

    val itemCount = derivedStateOf { lists.fold(0) { acc, list -> acc + 1 + list.items.size } }
    LaunchedEffect(itemCount) {
        val focusDirection = focusDirectionToMove

        if (focusDirection != null) {
            val itemHeightPx = density.run { 52.dp.toPx() }
            scrollState.animateScrollBy(
                value = if (focusDirection == FocusDirection.Down) itemHeightPx else -itemHeightPx,
                animationSpec = spring(stiffness = StiffnessHigh)
            )

            focusManager.moveFocus(focusDirection = focusDirection)
        }

        focusDirectionToMove = null
    }
}

/**
 * Custom scroll modifier extension which ties together focus listening and relocation requesting
 */
@ExperimentalComposeUiApi
private fun Modifier.scrollToOnFocus(scope: CoroutineScope, requester: RelocationRequester): Modifier =
    relocationRequester(requester)
    .onFocusEvent {
        if (it.isFocused) {
            // Wait 250ms for the keyboard to animate in, such that the view
            // resizes and [bringIntoView] scrolls down entirely.
            scope.launch {
                delay(250)
                requester.bringIntoView()
            }
        }
    }


// NOTE: This would be more performant if it was using [items], however due to a current
// limitation of relocationRequester, the composable being relocated to much be in the composition
// but off screen. When not in a [Column], the composable offscreen does not exist within the composition
// and therefore behaves badly
@OptIn(ExperimentalComposeUiApi::class, ExperimentalStdlibApi::class)
@Composable
private fun TodoListColumn(
    scrollableState: ScrollState,
    lists: List<TodoList>,
    dispatch: (Any) -> Any,
) {
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.verticalScroll(scrollableState)) {

        Spacer(modifier = Modifier.height(16.dp))

        for (list in lists) {
            val titleRequester = remember { RelocationRequester() }
            ListTitle(
                title = list.title,
                onValueChange = { dispatch(Action.UpdateListTitle(list, it)) },
                onDoneAction = { dispatch(Action.AddTodo(list)) },
                onDelete = { dispatch(Action.DeleteList(list)) },
                modifier = Modifier.scrollToOnFocus(scope, titleRequester)
            )

            for (item in list.items) {
                val itemRequester = remember { RelocationRequester() }

                TodoRow(
                    text = item.text,
                    isDone = item.isDone,
                    onTodoTextChange = { dispatch(Action.UpdateTodoText(item, it)) },
                    onTodoCheckedChange = { dispatch(Action.UpdateTodoDone(item, it)) },
                    onImeAction = { dispatch(Action.AddTodoAfter(item)) },
                    onDelete = { dispatch(Action.DeleteTodo(item)) },
                    modifier = Modifier.scrollToOnFocus(scope, itemRequester)
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
