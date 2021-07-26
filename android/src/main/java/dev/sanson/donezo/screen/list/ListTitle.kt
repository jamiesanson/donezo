package dev.sanson.donezo.screen.list

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import dev.sanson.donezo.theme.DonezoTheme

@Composable
fun ListTitle(
    title: String,
    onValueChange: (String) -> Unit,
    onDoneAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textFieldValue = remember { mutableStateOf(TextFieldValue(title)) }

    BasicTextField(
        value = textFieldValue.value,
        onValueChange = {
            textFieldValue.value = it
            onValueChange(it.text)
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
        modifier = modifier
            .padding(Dp(24f))
            .animateContentSize(),
        cursorBrush = SolidColor(MaterialTheme.colors.onSurface.copy(alpha = 0.54f)),
        textStyle = MaterialTheme.typography.h5.copy(
            color = MaterialTheme.colors.onSurface,
        ),
    )
}

//region previews
@Preview(showBackground = true, name = "Title TextField")
@Composable
fun DonezoTitleTextFieldPreview() {
    DonezoTheme {
        ListTitle(
            title = "Live literals are neat",
            onValueChange = { /*TODO*/ },
            onDoneAction = {}
        )
    }
}
//endregion
