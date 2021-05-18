package dev.sanson.tick.todo

import dev.sanson.tick.backend.Backend
import dev.sanson.tick.backend.PresentableBackend
import dev.sanson.tick.model.TodoList
import dev.sanson.tick.todo.feature.navigation.Navigation

data class AppState(
    val lists: List<TodoList> = emptyList(),
    val backends: List<Backend> = emptyList(),
    val navigation: Navigation = Navigation(),
)
