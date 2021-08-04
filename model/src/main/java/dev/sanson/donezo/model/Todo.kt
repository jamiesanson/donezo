package dev.sanson.donezo.model

import kotlinx.serialization.Serializable

@Serializable
data class Todo(
    val text: String,
    val isDone: Boolean,
)
