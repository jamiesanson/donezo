package dev.sanson.donezo.backend

import dev.sanson.donezo.model.TodoList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface Backend {

    val dataSource: BackendDataSource

    val ui: UI

    interface UI {
        val setupFlow: BackendSetupFlow
        val backendMenuItem: BackendMenuItem
    }
}

/**
 * A [BackendDataSource] is a remote destination for syncing todo items to. Backend implementations, when enabled,
 * are responsible for syncing content on their own schedule, and providing synced todo items
 * in a [Snapshot].
 */
abstract class BackendDataSource {
    val status: StateFlow<Status> = MutableStateFlow(value = Status.Disabled)

    abstract fun update(items: List<TodoList>)

    abstract fun syncNow()

    private fun updateStatus(
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
