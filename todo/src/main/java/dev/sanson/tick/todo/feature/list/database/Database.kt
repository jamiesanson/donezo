package dev.sanson.tick.todo.feature.list.database

import dev.sanson.tick.todo.Action
import dev.sanson.tick.todo.AppState
import org.reduxkotlin.middleware
import org.reduxkotlin.reducerForActionType

val DatabaseReducer = reducerForActionType<AppState, DatabaseAction> { state, action ->
    when (action) {
        is DatabaseAction.HydrateTodo -> state.copy(
            lists = state.lists.map { list ->
                list.copy(
                    items = list.items.map {
                        if (it == action.original) {
                            DatabaseTodo(id = action.id, text = it.text, isDone = it.isDone)
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

    when {
        /**
         * Adding a todo to a given list
         */
        action is Action.AddTodo && action.list is DatabaseTodoList -> {
            dispatch(DatabaseAction.AddTodo(list = action.list))
        }

        /**
         * Add a todo as a sibling of the input todo
         */
        action is Action.AddTodoAsSibling -> {
            val list = store.state.lists.find { it.items.contains(action.sibling) } as? DatabaseTodoList
                ?: throw IllegalArgumentException("No list found for sibling: ${action.sibling}")

            dispatch(DatabaseAction.AddTodo(list = list))
        }

        /**
         * Deleting a todo
         */
        action is Action.DeleteTodo && action.item is DatabaseTodo ->
            dispatch(DatabaseAction.DeleteTodoById(id = action.item.id))

        /**
         * Updating a list's title
         */
        action is Action.UpdateListTitle && action.list is DatabaseTodoList ->
            dispatch(DatabaseAction.UpdateListTitle(action.list, action.title))

        /**
         * Updating a todo
         */
        action is Action.UpdateTodoText && action.item is DatabaseTodo ->
            dispatch(DatabaseAction.UpdateTodo(action.item.copy(text = action.text)))

        /**
         * Updating a todo
         */
        action is Action.UpdateTodoDone && action.item is DatabaseTodo ->
            dispatch(DatabaseAction.UpdateTodo(action.item.copy(isDone = action.isDone)))

        /**
         * Else, no DB ops to perform
         */
        else -> {
        }
    }
}
