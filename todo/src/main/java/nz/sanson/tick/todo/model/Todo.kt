package nz.sanson.tick.todo.model

import arrow.optics.optics

@optics
data class Todo(
    val text: String,
    val isDone: Boolean
) { companion object }