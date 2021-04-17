package dev.sanson.tick.sync

import dev.sanson.tick.model.TodoList
import kotlinx.coroutines.flow.StateFlow

interface Syncer: SyncUIProvider {

    val syncerId: String

    val state: StateFlow<State>

    suspend fun enable()
    suspend fun disable()

    fun onChanged(items: List<TodoList>)

    sealed class State {
        object Syncing : State()
        object Idle : State()

        data class SyncError(val resolution: Resolution) : State() {
            sealed class Resolution {
                data class PresentAlert(val message: String): Resolution()
            }
        }
    }
}