package dev.sanson.tick.backend.git

import dev.sanson.tick.model.TodoList
import dev.sanson.tick.backend.Backend

abstract class GitBackend: Backend() {

    // TODO: Add reference to repository

    override val description: String =
        "Store your to do list in a git repo using a markdown file as the source of truth & capturing changes as commits"

    override fun update(items: List<TodoList>) {
        TODO("Implement generic list -> file -> commit whenever functionality")
    }

    override fun syncNow() {
        TODO("Implement generic git syncnow functionality")
    }
}