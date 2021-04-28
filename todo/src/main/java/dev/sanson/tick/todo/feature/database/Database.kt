package dev.sanson.tick.todo.feature.database

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import dev.sanson.tick.arch.di.inject
import dev.sanson.tick.arch.redux.Thunk
import dev.sanson.tick.db.Database
import dev.sanson.tick.model.Todo
import dev.sanson.tick.model.TodoList
import dev.sanson.tick.model.copy
import dev.sanson.tick.todo.Action
import dev.sanson.tick.todo.AppState
import dev.sanson.tick.todo.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.reduxkotlin.middleware
import org.reduxkotlin.reducerForActionType
import java.lang.IllegalStateException

interface DatabaseTodoList : TodoList {
    val id: Long
}

interface DatabaseTodo : Todo {
    val id: Long
}

private fun Todo.copy(id: Long): DatabaseTodo = object : DatabaseTodo, Todo by this {
    override val id: Long = id
}

sealed class DatabaseAction {
    /**
     * Turn a plain old [Todo] into a [DatabaseTodo]
     */
    data class HydrateTodo(val original: Todo, val id: Long) : DatabaseAction()

    companion object {
        /**
         * Load all lists from database, seeding some starter items if the database is empty
         */
        internal fun FetchAll(): Thunk<AppState> = { dispatch, _, _ ->
            launch {
                val database by inject<Database>()
                val allLists = database.getAllAsFlow()
                    .first()
                    .ifEmpty {
                        database.listQueries.seed()
                        database.getAllAsFlow().first()
                    }

                dispatch(Action.ListsLoaded(allLists))
            }
        }
    }
}

val DatabaseReducer = reducerForActionType<AppState, DatabaseAction> { state, action ->
    val screen = state.currentScreen as? Screen.Lists ?: return@reducerForActionType state

    when (action) {
        is DatabaseAction.HydrateTodo -> state.copy(
            currentScreen = screen.copy(
                lists = screen.lists.map { list ->
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
fun DatabaseMiddleware(
    scope: CoroutineScope,
    database: Database
) = middleware<AppState> { store, dispatch, action ->
    // Pass everything through immediately, this middleware only deals with side-effects
    dispatch(action)

    // If we're not on the list screen we shouldn't expect todo-related events
    val screen = store.state.currentScreen as? Screen.Lists ?: return@middleware Unit

    when {
        // TODO: Move the following actions into Thunks, which can then be dispatched.
        /**
         * Adding a todo to the database
         */
        (action is Action.AddTodo && action.list is DatabaseTodoList) || action is Action.AddTodoAsSibling -> scope.launch {
            val list = when (action) {
                is Action.AddTodo ->
                    action.list
                is Action.AddTodoAsSibling ->
                    screen.lists.find { it.items.contains(action.sibling) } as? DatabaseTodoList
                        ?: throw IllegalArgumentException("No list found for sibling: ${action.sibling}")
                else ->
                    throw IllegalStateException("Can't add todo to list not in DB")
            }

            // Smart-casting didn't work here
            list as DatabaseTodoList

            val id = withContext(Dispatchers.IO) {
                with(database.todoQueries) {
                    insert(listId = list.id, text = "")

                    selectByListId(listId = list.id)
                        .executeAsList()
                        .last()
                        .id
                }
            }

            val original = screen.lists
                .first { (it as? DatabaseTodoList)?.id == list.id }
                .items
                .last { it !is DatabaseTodo }

            dispatch(DatabaseAction.HydrateTodo(original, id))
        }

        /**
         * Deleting a todo
         */
        action is Action.DeleteTodo && action.item is DatabaseTodo ->
            scope.launch(Dispatchers.IO) {
                database.todoQueries.delete(id = action.item.id)
            }

        /**
         * Updating a list's title
         */
        action is Action.UpdateListTitle && action.list is DatabaseTodoList ->
            scope.launch(Dispatchers.IO) {
                database.listQueries.update(id = action.list.id, title = action.title)
            }

        /**
         * Updating a todo
         */
        action is Action.UpdateTodo && action.item is DatabaseTodo ->
            scope.launch(Dispatchers.IO) {
                database.todoQueries.update(
                    id = action.item.id,
                    text = action.item.text,
                    isDone = action.item.isDone
                )
            }

        /**
         * Else, no DB ops to perform
         */
        else -> {
        }
    }
}

private fun Database.getAllAsFlow(): Flow<List<TodoList>> {
    fun <T : Any> Query<T>.asListFlow() = asFlow().map { it.executeAsList() }

    val lists = listQueries.selectAll().asListFlow()
    val todos = todoQueries.selectAll().asListFlow()

    return lists
        .combine(todos) { allLists, allTodos ->
            allLists.map { list ->
                val todoItems = allTodos.filter { it.listId == list.id }
                object : DatabaseTodoList {
                    override val id = list.id
                    override val title = list.title
                    override val items = todoItems.map {
                        object : DatabaseTodo {
                            override val id: Long = it.id
                            override val text: String = it.text
                            override val isDone: Boolean = it.isDone
                        }
                    }
                }
            }
        }
}
