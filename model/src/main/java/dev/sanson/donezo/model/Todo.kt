package dev.sanson.donezo.model

import kotlinx.serialization.Serializable

@Serializable
data class Todo(
    val id: Long = -1L,
    val text: String,
    val isDone: Boolean,
)
