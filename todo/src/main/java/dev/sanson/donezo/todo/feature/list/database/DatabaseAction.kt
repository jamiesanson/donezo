@file:Suppress("FunctionName")

package dev.sanson.donezo.todo.feature.list.database

import dev.sanson.donezo.arch.di.inject
import dev.sanson.donezo.arch.redux.asyncAction
import dev.sanson.donezo.db.Database
import dev.sanson.donezo.model.Todo
import dev.sanson.donezo.model.TodoList
import dev.sanson.donezo.todo.Action
import dev.sanson.donezo.todo.AppState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object DatabaseAction {

    /**
     * Updates the title of [list] to be [title]
     */
    internal fun UpdateListTitle(listId: Long, title: String) =
        asyncAction<AppState> { _, _ ->
            val database by inject<Database>()
            database.listQueries.update(id = listId, title = title)
        }

    /**
     * Updates a Todo [item]
     */
    internal fun UpdateTodo(itemId: Long, item: Todo) = asyncAction<AppState> { _, _ ->
        val database by inject<Database>()
        database.todoQueries.update(id = itemId, text = item.text, isDone = item.isDone)
    }

    /**
     * Add a new [Todo] to [list]
     */
    internal fun AddTodo(listId: Long, index: Int) =
        asyncAction<AppState> thunk@{ dispatch, getState ->
            val database by inject<Database>()

            val id = with(database.todoQueries) {
                insert(listId = listId, text = "", index = index)

                selectByListId(listId = listId)
                    .executeAsList()
                    .last()
                    .id
            }

            withContext(Dispatchers.Main) {
                dispatch(DatabaseMiddleware.HydrateMapping(id))
            }
        }

    /**
     * Delete a given [Todo] item
     */
    internal fun DeleteTodo(itemId: Long) = asyncAction<AppState> { dispatch, _ ->
        val database by inject<Database>()
        with(database.todoQueries) {
            transaction {
                val currentIndex = selectById(itemId).executeAsOne().idx
                delete(id = itemId)
                decrementAllFrom(index = currentIndex)
            }
        }

        withContext(Dispatchers.Main) {
            dispatch(DatabaseMiddleware.FlushMapping(id = itemId))
        }
    }

    /**
     * Load all lists from database, seeding some starter items if the database is empty
     */
    internal fun FetchAll() = asyncAction<AppState> { dispatch, _ ->
        val database by inject<Database>()

        fun fetchAll(): List<TodoList> {
            val dbLists = database.listQueries.selectAll().executeAsList()
            val todos = database.todoQueries.selectAll().executeAsList()

            val mapping = mutableMapOf<Any, Long>()

            val lists = dbLists.map { list ->
                TodoList(
                    title = list.title,
                    items = todos
                        .filter { it.listId == list.id }
                        .sortedBy { it.idx }
                        .map { todo ->
                            Todo(
                                text = todo.text,
                                isDone = todo.isDone
                            ).also { mapping[it] = todo.id }
                        }
                ).also { mapping[it] = list.id }
            }

            dispatch(DatabaseMiddleware.InitialiseMapping(mapping))

            return lists
        }

        val allLists = fetchAll()
            .ifEmpty {
                database.transaction {
                    database.listQueries.seed()

                    val list = database.listQueries.selectAll().executeAsOne()
                    database.todoQueries.seed(list.id)
                }
                fetchAll()
            }

        withContext(Dispatchers.Main) {
            dispatch(Action.ListsLoaded(allLists))
        }
    }
}
