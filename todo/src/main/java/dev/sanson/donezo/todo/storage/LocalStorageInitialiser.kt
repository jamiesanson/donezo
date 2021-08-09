package dev.sanson.donezo.todo.storage

import dev.sanson.donezo.arch.di.inject
import dev.sanson.donezo.arch.redux.asyncAction
import dev.sanson.donezo.model.TodoList
import dev.sanson.donezo.todo.Action
import dev.sanson.donezo.todo.AppState
import org.reduxkotlin.Store

/**
 * Thunk function for loading initial state from local storage asynchronously
 */
private fun LoadFromStorage(initialState: List<TodoList>) = asyncAction<AppState> { dispatch, _ ->
    val storage by inject<LocalStorage>()

    dispatch(Action.ListsLoaded(storage.load().ifEmpty { initialState }))
}

/**
 * Thunk function for persisting current state to local storage when it changes
 */
private val PersistToStorage = asyncAction<AppState> { _, getState ->
    val storage by inject<LocalStorage>()
    val state = getState()

    if (storage.load() == state.lists) return@asyncAction Unit

    storage.save(state.lists)
}

fun Store<AppState>.initialiseLocalStorage(initialState: List<TodoList>) {
    // Load lists from storage immediately
    dispatch(LoadFromStorage(initialState))

    // Publish changes back to local storage
    subscribe {
        dispatch(PersistToStorage)
    }
}
