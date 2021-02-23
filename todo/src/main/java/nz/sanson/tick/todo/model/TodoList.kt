package nz.sanson.tick.todo.model

import arrow.optics.optics

@optics
data class TodoList(
    val id: Long? = null,
    val title: String,
    val items: List<Todo>
) {
  companion object
}