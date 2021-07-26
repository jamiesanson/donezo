package dev.sanson.donezo.todo.feature.list.database

import dev.sanson.donezo.arch.redux.Thunk
import dev.sanson.donezo.model.Todo
import dev.sanson.donezo.model.TodoList
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

    fun InitialiseMapping(mapping: Map<Any, Long>): Thunk<AppState> = { _, _ ->
        idMapping.putAll(from = mapping)
        Unit
    }

    fun HydrateMapping(id: Long): Thunk<AppState> = { _, getState ->
        val state = getState()

        // Find an item without an ID - it's probably that one that needs to be added to the mapping
        val newKey =
            state.lists.find { !idMapping.containsKey(it) }
                ?: state.lists.flatMap { it.items }.find { !idMapping.containsKey(it) }
                ?: throw IllegalStateException("Hydrate called before new item added to state")

        idMapping[newKey] = id
        Unit
    }

    fun FlushMapping(item: Any? = null, id: Long? = null): Thunk<AppState> = { _, _ ->
        when {
            item != null -> idMapping.remove(item)
            id != null ->
                idMapping
                    .filterValues { id == id }
                    .also { results -> idMapping.remove(results.keys.first()) }
        }

        Unit
    }

    // TODO: This approach doesn't actually work. If you change an item structurally, you change
    // the map key. This is a tomorrow job.
    private val idMapping = mutableMapOf<Any, Long>()

    private val TodoList.id: Long get() = idMapping[this]!!
    private val Todo.id: Long get() = idMapping[this]!!

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
