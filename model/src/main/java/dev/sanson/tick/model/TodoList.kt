package dev.sanson.tick.model

data class TodoList(
    val id: Long = -1,
    val title: String,
    val items: List<Todo>
)
