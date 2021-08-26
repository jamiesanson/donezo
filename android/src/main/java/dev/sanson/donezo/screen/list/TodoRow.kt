package dev.sanson.donezo.screen.list

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.ExperimentalComposeUiApi
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
import dev.sanson.donezo.theme.DonezoTheme

@OptIn(ExperimentalComposeUiApi::class) // Opt-in for Key._Blah_ APIs
@Composable
fun TodoRow(
    text: String,
    isDone: Boolean,
    onTodoTextChange: (String) -> Unit,
    onTodoCheckedChange: (Boolean) -> Unit,
    onImeAction: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = Dp(52f)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(Dp(4f)))

        Checkbox(
            checked = isDone,
            onCheckedChange = onTodoCheckedChange,
            modifier = Modifier
                .alignByBaseline()
                .padding(top = Dp(14f), bottom = Dp(12f), start = Dp(16f), end = Dp(16f))
        )

        val textFieldValue = remember { mutableStateOf(TextFieldValue(text)) }

        BasicTextField(
            value = textFieldValue.value,
            onValueChange = {
                textFieldValue.value = it.copy(text = it.text)
                onTodoTextChange(it.text)
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    onImeAction()
                }
            ),
            cursorBrush = SolidColor(MaterialTheme.colors.onSurface.copy(alpha = 0.54f)),
            textStyle = MaterialTheme.typography.body1.copy(
                color = MaterialTheme.colors.onSurface,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .alignByBaseline()
                .animateContentSize()
                .padding(top = Dp(12f), bottom = Dp(12f), end = Dp(16f))
                .onKeyEvent {
                    when (it.key) {
                        Key.Backspace, Key.Delete -> {
                            if (textFieldValue.value.text.isEmpty()) {
                                onDelete()
                                true
                            } else {
                                false
                            }
                        }
                        Key.Enter -> {
                            onImeAction()
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
    DonezoTheme {
        Scaffold {
            TodoRow(
                text = "Hang the washing out",
                isDone = false,
                onDelete = {},
                onImeAction = {},
                onTodoCheckedChange = {},
                onTodoTextChange = {}
            )
        }
    }
}
//endregion
