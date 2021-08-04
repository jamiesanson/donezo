package dev.sanson.donezo.model

import kotlinx.serialization.Serializable

@Serializable
data class TodoList(
    val id: Long = -1L,
    val title: String,
    val items: List<Todo>
)
