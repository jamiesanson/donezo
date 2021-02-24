package dev.sanson.tick.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
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
  onTitleTextChanged: (String) -> Unit = {},
  onDoneChanged: (Boolean) -> Unit = {},
) {
  Row(
    modifier = Modifier
      .height(Dp(56f))
      .fillMaxWidth()
      .clickable { onDoneChanged(!item.isDone) },
    verticalAlignment = Alignment.CenterVertically
  ) {

    Spacer(
      modifier = Modifier.width(width = Dp(16f))
    )

    Checkbox(
      checked = item.isDone,
      onCheckedChange = onDoneChanged
    )

    Spacer(
      modifier = Modifier.width(width = Dp(16f))
    )

    BasicTextField(
      value = TextFieldValue(item.text),
      onValueChange = { onTitleTextChanged(it.text) },
      keyboardOptions = KeyboardOptions.Default.copy(
        capitalization = KeyboardCapitalization.Sentences
      ),
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
      TodoRow(item = Todo(text = "Hang the washing out", isDone = false))
    }
  }
}
//endregion