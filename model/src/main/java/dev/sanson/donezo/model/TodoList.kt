package dev.sanson.donezo.model

data class TodoList(
    val id: Long = -1L,
    val title: String,
    val items: List<Todo>
)
