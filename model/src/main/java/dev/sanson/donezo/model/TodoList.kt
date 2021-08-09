package dev.sanson.donezo.model

import kotlinx.serialization.Serializable

@Serializable
data class TodoList(
    val title: String,
    val items: List<Todo>
)
