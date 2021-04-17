package dev.sanson.tick.model

data class Todo(
    val id: Long? = null,
    val text: String,
    val isDone: Boolean
)
