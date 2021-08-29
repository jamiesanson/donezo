package dev.sanson.donezo.todo.feature.list

import android.util.Log
import dev.sanson.donezo.model.Todo
import dev.sanson.donezo.model.TodoList
import dev.sanson.donezo.todo.Action
import dev.sanson.donezo.todo.AppState
import org.reduxkotlin.Dispatcher
import org.reduxkotlin.Middleware
import org.reduxkotlin.Reducer
import org.reduxkotlin.Store

val ListsReducer: Reducer<AppState> = reducer@{ state, action ->
    Log.d("ListReducer", "On action: $action")

    when (action) {
        is Action.ListsLoaded -> state.copy(lists = action.lists)
        is Action.UpdateListTitle -> state.copy(
            lists = state.lists.map {
                if (it === action.list) {
                    it.copy(title = action.title)
                } else {
                    it
                }
            }
        )
        is Action.UpdateTodoText -> state.copy(
            lists = state.lists.map { list ->
                list.copy(
                    items = list.items.map { item ->
                        if (item === action.item) {
                            item.copy(text = action.text, isDone = item.isDone)
                        } else {
                            item
                        }
                    }
                )
            }
        )
        is Action.UpdateTodoDone -> state.copy(
            lists = state.lists.map { list ->
                list.copy(
                    items = list.items.map { item ->
                        if (item === action.item) {
                            item.copy(isDone = action.isDone, text = item.text)
                        } else {
                            item
                        }
                    }
                )
            }
        )
        is Action.AddTodo -> {
            state.copy(
                lists = state.lists.map {
                    if (it === action.list) {
                        it.copy(
                            items = listOf(
                                Todo(text = "", isDone = false),
                                *it.items.toTypedArray()
                            )
                        )
                    } else {
                        it
                    }
                }
            )
        }
        is Action.AddTodoAfter -> {
            // If the current item is empty, we shouldn't add another one
            if (action.sibling.text.isBlank()) return@reducer state

            // Find the containing list
            val list = state.lists.find { it.items.contains(action.sibling) }
                ?: throw IllegalArgumentException("No list found for sibling: ${action.sibling}")

            state.copy(
                lists = state.lists.map {
                    if (it === list) {
                        it.copy(items = it.items + Todo(text = "", isDone = false))
                    } else {
                        it
                    }
                }
            )
        }
        is Action.AddListAfter -> {
            // Guard to ensure we're not adding another empty list if there already is one there
            val siblingIndex = state.lists.indexOf(action.sibling)
            val nextList = state.lists.getOrNull(siblingIndex + 1)

            if (nextList != null) {
                if (nextList.items.isEmpty() && nextList.title.isBlank()) return@reducer state
            }

            state.copy(
                lists = state.lists.flatMap { list ->
                    if (list === action.sibling) {
                        listOf(list, TodoList(title = "", items = emptyList()))
                    } else {
                        listOf(list)
                    }
                }
            )
        }
        is Action.DeleteTodo -> {
            state.copy(
                lists = state.lists.map { list ->
                    if (list.items.contains(action.item)) {
                        list.copy(items = list.items - action.item)
                    } else {
                        list
                    }
                }
            )
        }
        is Action.DeleteList -> {
            state.copy(
                lists = state.lists - action.list
            )
        }
        else -> state
    }
}

/**
 * Middleware for moderating list interactions. Contains most UX-related list side-effects.
 */
object ListInteractionMiddleware : Middleware<AppState> {

    override fun invoke(store: Store<AppState>): (next: Dispatcher) -> (action: Any) -> Any =
        { next ->
            { action ->
                // TODO: This should probably fire some other event when it doesn't action the list delete
                val shouldPassActionOn = when {
                    action is Action.DeleteList && action.list.items.isNotEmpty() -> false
                    action is Action.DeleteTodo -> false
                    else -> true
                }

                // Pass on the state change
                if (shouldPassActionOn) {
                    next(action)
                }

                val state = store.state
                when (action) {
                    /**
                     * When deleting a todo item, if it's the last in the list but not the only one in the
                     * list, add a new list.
                     */
                    is Action.DeleteTodo -> {
                        val containingList = state.lists.find { it.items.contains(action.item) }!!
                        val isLastItemInList = containingList.items.last() == action.item
                        val isOnlyItemInList = containingList.items.size == 1

                        // Add a new list
                        if (isLastItemInList && !isOnlyItemInList) {
                            next(Action.AddListAfter(containingList))
                        }

                        // Action the delete
                        next(action)
                    }
                    else -> Unit
                }
            }
        }
}
