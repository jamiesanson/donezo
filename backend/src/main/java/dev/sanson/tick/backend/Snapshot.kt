package dev.sanson.tick.backend

import dev.sanson.tick.model.TodoList
import java.time.Instant

data class Snapshot(
    val asAt: Instant,
    val items: List<TodoList>
) {
    companion object {
        val Empty = Snapshot(asAt = Instant.EPOCH, items = emptyList())
    }
}
