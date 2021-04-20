package dev.sanson.tick.backend

import dev.sanson.tick.model.TodoList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * [Backend] implementation exposing UI for presenting in menu item and perform first time setup.
 */
interface PresentableBackend : Backend, BackendMenuItem, BackendSetupFlow

/**
 * A [Backend] is a remote destination for syncing todo items to. Backend implementations, when enabled,
 * are responsible for syncing content on their own schedule, and providing synced todo items
 * in a [Snapshot].
 */
interface Backend {
    val status: StateFlow<Status>

    fun update(items: List<TodoList>)

    fun syncNow()

    fun updateStatus(
        enabled: Boolean = status.value.enabled,
        currentSnapshot: Snapshot = status.value.currentSnapshot,
        syncState: State = status.value.syncState
    ) {
        (status as MutableStateFlow).compareAndSet(
            expect = status.value,
            update = Status(enabled, currentSnapshot, syncState)
        )
    }

    data class Status(
        val enabled: Boolean,
        val currentSnapshot: Snapshot,
        val syncState: State
    ) {
        companion object {
            val Disabled =
                Status(enabled = false, currentSnapshot = Snapshot.Empty, syncState = State.Idle)
        }
    }

    enum class State {
        Idle,
        Syncing,
        Error
    }
}
