package dev.sanson.tick.ui

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import dev.sanson.tick.ui.theme.TickTheme
import nz.sanson.tick.todo.model.Todo

@Composable
fun TodoRow(
    item: Todo,
    onTodoChange: (Todo) -> Unit = {},
    onDoneAction: () -> Unit
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
                textFieldValue.value = it

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
            keyboardActions = KeyboardActions(onNext = {
                onDoneAction()
            }),
            cursorBrush = SolidColor(MaterialTheme.colors.onSurface.copy(alpha = 0.54f)),
            textStyle = MaterialTheme.typography.body1.copy(
                color = MaterialTheme.colors.onSurface,
                fontSize = 18.sp
            ),
        )
    }
}


//region previews
@Preview(showBackground = true, name = "Title TextField")
@Composable
fun TodoPreview() {
    TickTheme {
        Scaffold {
            TodoRow(item = Todo(text = "Hang the washing out", isDone = false), {}, {})
        }
    }
}
//endregion