@file:Suppress("FunctionName")

package dev.sanson.tick.todo.feature.list.database

import dev.sanson.tick.arch.di.inject
import dev.sanson.tick.arch.redux.asyncAction
import dev.sanson.tick.db.Database
import dev.sanson.tick.model.Todo
import dev.sanson.tick.model.TodoList
import dev.sanson.tick.todo.Action
import dev.sanson.tick.todo.AppState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal sealed class DatabaseAction {

    /**
     * Add an [id] to a [Todo]
     */
    internal data class HydrateTodo(val original: Todo, val id: Long) : DatabaseAction()

    companion object {
        /**
         * Updates the title of [list] to be [title]
         */
        internal fun UpdateListTitle(list: TodoList, title: String) =
            asyncAction<AppState> { _, _ ->
                val database by inject<Database>()
                database.listQueries.update(id = list.id, title = title)
            }

        /**
         * Updates a Todo [item]
         */
        internal fun UpdateTodo(item: Todo) = asyncAction<AppState> { _, _ ->
            val database by inject<Database>()
            database.todoQueries.update(id = item.id, text = item.text, isDone = item.isDone)
        }

        /**
         * Add a new [Todo] to [list]
         */
        internal fun AddTodo(list: TodoList, index: Int) =
            asyncAction<AppState> thunk@{ dispatch, getState ->
                val database by inject<Database>()

                val id = with(database.todoQueries) {
                    insert(listId = list.id, text = "", index = index)

                    selectByListId(listId = list.id)
                        .executeAsList()
                        .last()
                        .id
                }

                val original = getState().lists
                    .first { it.id == list.id }
                    .items
                    .last { it.id == -1L }

                withContext(Dispatchers.Main) {
                    dispatch(HydrateTodo(original, id))
                }
            }

        /**
         * Delete a given [Todo] item
         */
        internal fun DeleteTodo(item: Todo) = asyncAction<AppState> { _, _ ->
            val database by inject<Database>()
            with(database.todoQueries) {
                transaction {
                    val currentIndex = selectById(item.id).executeAsOne().idx
                    delete(id = item.id)
                    decrementAllFrom(index = currentIndex)
                }
            }
        }

        /**
         * Load all lists from database, seeding some starter items if the database is empty
         */
        internal fun FetchAll() = asyncAction<AppState> { dispatch, _ ->
            val database by inject<Database>()

            fun fetchAll(): List<TodoList> {
                val lists = database.listQueries.selectAll().executeAsList()
                val todos = database.todoQueries.selectAll().executeAsList()

                return lists.map { list ->
                    TodoList(
                        id = list.id,
                        title = list.title,
                        items = todos
                            .filter { it.listId == list.id }
                            .sortedBy { it.idx }
                            .map {
                                Todo(
                                    id = it.id,
                                    text = it.text,
                                    isDone = it.isDone
                                )
                            }
                    )
                }
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
}
