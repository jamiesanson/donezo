package dev.sanson.tick.model

data class TodoList(
    val id: Long? = null,
    val title: String,
    val items: List<Todo>
)
