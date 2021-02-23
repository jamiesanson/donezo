package dev.sanson.tick.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import dev.sanson.tick.ui.theme.TickTheme
import dev.sanson.tick.ui.theme.purple200

@Composable
fun TickTextField(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier
) {
    BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences
            ),
            maxLines = 1,
            cursorColor = MaterialTheme.colors.onSurface.copy(alpha = 0.54f),
            textStyle = MaterialTheme.typography.h4.copy(
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Medium
            ),
            visualTransformation = TickTitleVisualTransformation,
    )
}

/**
 * Visual transformation allowing for syntax highlighting
 */
private val TickTitleVisualTransformation = VisualTransformation { text ->
    // Titles shouldn't contain a #, but we put one on there to feel like a wysiwyg editor
    val textToTransform = "# ${text.text}"

    val markedUpString = AnnotatedString(
            text = textToTransform,
            spanStyles = listOf(
                    AnnotatedString.Range(
                            item = SpanStyle(color = purple200),
                            start = 0,
                            end = 1
                    )
            )
    )

    val offsetMapping = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int = offset + 2
        override fun transformedToOriginal(offset: Int): Int = offset - 2
    }
    TransformedText(
            text = markedUpString,
            offsetMapping = offsetMapping)
}

//region previews
@Preview(showBackground = true, name = "Title TextField")
@Composable
fun TickTitleTextFieldPreview() {
    TickTheme {
        Scaffold(modifier = Modifier.padding(Dp(8f))) {
            TickTextField(value = "Work things this week", onValueChange = { /*TODO*/ })
        }
    }
}
//endregion