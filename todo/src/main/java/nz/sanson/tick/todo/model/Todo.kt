package nz.sanson.tick.todo.model

import arrow.optics.optics

@optics
data class Todo(
    val id: Long? = null,
    val text: String,
    val isDone: Boolean
) {
    companion object
}
