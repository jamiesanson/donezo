package dev.sanson.donezo.todo.feature.list.database

import dev.sanson.donezo.todo.Action
import dev.sanson.donezo.todo.AppState
import org.reduxkotlin.middleware
import org.reduxkotlin.reducerForActionType

val DatabaseReducer = reducerForActionType<AppState, DatabaseAction> { state, action ->
    when (action) {
        is DatabaseAction.HydrateTodo -> state.copy(
            lists = state.lists.map { list ->
                list.copy(
                    items = list.items.map {
                        if (it == action.original) {
                            it.copy(id = action.id)
                        } else {
                            it
                        }
                    }
                )
            }
        )
    }
}

/**
 * Middleware for ensuring the runtime state and database keep in sync, effectively housing all
 * DB-related side-effects.
 *
 * In addition, this middleware maintains the relation between Todo models and database row IDs,
 * by emitting actions which are then
 */
val DatabaseMiddleware = middleware<AppState> { store, dispatch, action ->
    // Pass everything through immediately, this middleware only deals with side-effects
    dispatch(action)

    when (action) {
        /**
         * Adding a todo to a given list
         */
        is Action.AddTodo -> {
            dispatch(DatabaseAction.AddTodo(list = action.list, index = 0))
        }

        /**
         * Add a todo as a sibling of the input todo
         */
        is Action.AddTodoAsSibling -> {
            val list = store.state.lists.find { it.items.contains(action.sibling) }
                ?: throw IllegalArgumentException("No list found for sibling: ${action.sibling}")

            val index = list.items.indexOf(action.sibling) + 1

            dispatch(DatabaseAction.AddTodo(list = list, index = index))
        }

        /**
         * Deleting a todo
         */
        is Action.DeleteTodo -> dispatch(DatabaseAction.DeleteTodo(item = action.item))

        /**
         * Updating a list's title
         */
        is Action.UpdateListTitle -> dispatch(DatabaseAction.UpdateListTitle(action.list, action.title))

        /**
         * Updating a todo
         */
        is Action.UpdateTodoText -> dispatch(DatabaseAction.UpdateTodo(action.item.copy(text = action.text)))

        /**
         * Updating a todo
         */
        is Action.UpdateTodoDone -> dispatch(DatabaseAction.UpdateTodo(action.item.copy(isDone = action.isDone)))

        /**
         * Else, no DB ops to perform
         */
        else -> {
        }
    }
}
