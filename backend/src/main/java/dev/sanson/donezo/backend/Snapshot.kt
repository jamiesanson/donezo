package dev.sanson.donezo.backend

import dev.sanson.donezo.model.TodoList
import java.time.Instant

data class Snapshot(
    val asAt: Instant,
    val items: List<TodoList>
) {
    companion object {
        val Empty = Snapshot(asAt = Instant.EPOCH, items = emptyList())
    }
}
