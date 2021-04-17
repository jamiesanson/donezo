package dev.sanson.tick.sync

import dev.sanson.tick.model.TodoList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class Syncer : SyncMenuItem, SyncSetupFlow {
    val status: StateFlow<Status> = MutableStateFlow(Status.Disabled)

    abstract fun onChanged(items: List<TodoList>)

    abstract fun syncNow()

    protected fun updateStatus(
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