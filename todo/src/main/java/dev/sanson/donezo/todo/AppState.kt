package dev.sanson.donezo.todo

import dev.sanson.donezo.backend.Backend
import dev.sanson.donezo.model.TodoList
import dev.sanson.donezo.todo.feature.navigation.Navigation

data class AppState(
    val lists: List<TodoList> = emptyList(),
    val backends: List<Backend> = emptyList(),
    val navigation: Navigation = Navigation(),
)
