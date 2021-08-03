package dev.sanson.donezo.todo.feature.list.database

import dev.sanson.donezo.todo.Action
import dev.sanson.donezo.todo.AppState
import org.reduxkotlin.Dispatcher
import org.reduxkotlin.Middleware
import org.reduxkotlin.Store

/**
 * Middleware for ensuring the runtime state and database keep in sync, effectively housing all
 * DB-related side-effects.
 *
 * In addition, this middleware maintains the relation between Todo models and database row IDs,
 * by emitting actions which are then
 */
object DatabaseMiddleware : Middleware<AppState> {

    override fun invoke(store: Store<AppState>): (next: Dispatcher) -> (action: Any) -> Any =
        { next ->
            { action ->
                // Pass most other things through
                next(action)

                val followupAction = when (action) {
                    /**
                     * Adding a todo to a given list
                     */
                    is Action.AddTodo ->
                        DatabaseAction.AddTodo(listId = action.list.id, index = 0)

                    /**
                     * Add a todo as a sibling of the input todo
                     */
                    is Action.AddTodoAsSibling -> {
                        val list = store.state.lists.find { it.items.contains(action.sibling) }
                            ?: throw IllegalArgumentException("No list found for sibling: ${action.sibling}")

                        val index = list.items.indexOf(action.sibling) + 1

                        DatabaseAction.AddTodo(listId = list.id, index = index)
                    }

                    /**
                     * Deleting a todo
                     */
                    is Action.DeleteTodo ->
                        DatabaseAction.DeleteTodo(itemId = action.item.id)

                    /**
                     * Updating a list's title
                     */
                    is Action.UpdateListTitle ->
                        DatabaseAction.UpdateListTitle(listId = action.list.id, title = action.title)

                    /**
                     * Updating a todo
                     */
                    is Action.UpdateTodoText ->
                        DatabaseAction.UpdateTodo(itemId = action.item.id, item = action.item.copy(text = action.text))

                    /**
                     * Updating a todo
                     */
                    is Action.UpdateTodoDone ->
                        DatabaseAction.UpdateTodo(itemId = action.item.id, item = action.item.copy(isDone = action.isDone))

                    /**
                     * Else, no DB ops to perform
                     */
                    else -> null
                }

                // Dispatch the followup
                followupAction?.let(next) ?: Unit
            }
        }
}
