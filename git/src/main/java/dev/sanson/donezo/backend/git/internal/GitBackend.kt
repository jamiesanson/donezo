package dev.sanson.donezo.backend.git.internal

import dev.sanson.donezo.backend.Backend
import dev.sanson.donezo.model.TodoList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GitBackend(
    private val repoSshUrl: String
) : Backend {

    override val status: StateFlow<Backend.Status> = MutableStateFlow(Backend.Status.Disabled)

    // TODO: Add reference to repository

    override fun update(items: List<TodoList>) {
        TODO("Implement generic list -> file -> commit whenever functionality")
    }

    override fun syncNow() {
        TODO("Implement generic git syncnow functionality")
    }
}