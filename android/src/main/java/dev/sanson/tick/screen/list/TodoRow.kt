package dev.sanson.tick.screen.list

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import dev.sanson.tick.android.LocalDispatch
import dev.sanson.tick.theme.TickTheme
import dev.sanson.tick.todo.Action
import dev.sanson.tick.model.Todo

@Composable
fun TodoRow(item: Todo) {
    val dispatch = LocalDispatch.current
    TodoRow(
        item = item,
        onTodoChange = { todo ->
            dispatch(Action.OnTodoChange(item = todo))
        },
        onDoneAction = {
            dispatch(Action.NewTodoItemInSameList(item))
        },
        onDeleteItem = {
            dispatch(Action.DeleteTodoItem(item))
        }
    )
}

@Composable
private fun TodoRow(
    item: Todo,
    onTodoChange: (Todo) -> Unit = {},
    onDoneAction: () -> Unit,
    onDeleteItem: () -> Unit,
) {
    Row(
        modifier = Modifier
            .height(Dp(56f))
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Spacer(
            modifier = Modifier.width(width = Dp(16f))
        )

        Checkbox(
            checked = item.isDone,
            onCheckedChange = {
                onTodoChange(item.copy(isDone = !item.isDone))
            }
        )

        Spacer(
            modifier = Modifier.width(width = Dp(16f))
        )

        val textFieldValue = remember { mutableStateOf(TextFieldValue(item.text)) }

        BasicTextField(
            value = textFieldValue.value,
            onValueChange = {
                textFieldValue.value = it.copy(text = it.text.replace("\n", ""))

                onTodoChange(
                    item.copy(
                        text = it.text
                    )
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    onDoneAction()
                }
            ),
            cursorBrush = SolidColor(MaterialTheme.colors.onSurface.copy(alpha = 0.54f)),
            textStyle = MaterialTheme.typography.body1.copy(
                color = MaterialTheme.colors.onSurface,
                fontSize = 18.sp
            ),
            modifier = Modifier.onKeyEvent {
                when (it.key) {
                    Key.Backspace, Key.Delete -> {
                        if (textFieldValue.value.text.isEmpty()) {
                            onDeleteItem()
                            true
                        } else {
                            false
                        }
                    }
                    Key.Enter -> {
                        onDoneAction()
                        true
                    }
                    else -> false
                }
            }
        )
    }
}

//region previews
@Preview(showBackground = true, name = "Title TextField")
@Composable
fun TodoPreview() {
    TickTheme {
        Scaffold {
            TodoRow(item = Todo(
                text = "Hang the washing out",
                isDone = false
            ), {}, {}, {})
        }
    }
}
//endregion
