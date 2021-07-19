package dev.sanson.donezo.model

data class Todo(
    val id: Long = -1,
    val text: String,
    val isDone: Boolean,
)
