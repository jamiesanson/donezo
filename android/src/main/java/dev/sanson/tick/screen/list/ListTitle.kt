package dev.sanson.tick.screen.list

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import dev.sanson.tick.theme.TickTheme
import dev.sanson.tick.theme.purple200

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
        maxLines = 1,
        modifier = modifier
            .padding(start = Dp(16f)),
        cursorBrush = SolidColor(MaterialTheme.colors.onSurface.copy(alpha = 0.54f)),
        textStyle = MaterialTheme.typography.h4.copy(
            color = MaterialTheme.colors.onSurface,
            fontWeight = FontWeight.Medium
        ),
        visualTransformation = TickTitleVisualTransformation,
    )

    Spacer(modifier = Modifier.height(Dp(8f)))
}

/**
 * Visual transformation allowing for syntax highlighting
 */
private val TickTitleVisualTransformation = VisualTransformation { text ->
    val markedUpString = AnnotatedString(
        text = text.toString(),
        spanStyles = listOf(
            AnnotatedString.Range(
                item = SpanStyle(color = purple200),
                start = 0,
                end = 1
            )
        )
    )

    TransformedText(
        text = markedUpString,
        offsetMapping = OffsetMapping.Identity
    )
}

//region previews
@Preview(showBackground = true, name = "Title TextField")
@Composable
fun TickTitleTextFieldPreview() {
    TickTheme {
        ListTitle(
            title = "# Live literals are neat",
            onValueChange = { /*TODO*/ },
            onDoneAction = {}
        )
    }
}
//endregion
