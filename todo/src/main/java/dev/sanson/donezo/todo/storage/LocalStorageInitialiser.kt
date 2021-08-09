package dev.sanson.donezo.todo.storage

import dev.sanson.donezo.arch.di.inject
import dev.sanson.donezo.arch.redux.asyncAction
import dev.sanson.donezo.model.TodoList
import dev.sanson.donezo.todo.Action
import dev.sanson.donezo.todo.AppState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.reduxkotlin.Store

/**
 * Thunk function for loading initial state from local storage asynchronously
 */
private fun LoadFromStorage(initialState: List<TodoList>) = asyncAction<AppState> { dispatch, _ ->
    val storage by inject<LocalStorage>()

    dispatch(Action.ListsLoaded(storage.load().ifEmpty { initialState }))
}

fun Store<AppState>.initialiseLocalStorage(initialState: List<TodoList>) {
    // Load lists from storage immediately
    dispatch(LoadFromStorage(initialState))

    // Publish changes back to local storage
    subscribe {
        val state = getState()
        val scope by inject<CoroutineScope>()
        scope.launch {
            val storage by inject<LocalStorage>()

            if (storage.load() == state.lists) return@launch

            storage.save(state.lists)
        }
    }
}
