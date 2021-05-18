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
     * Turn a plain old [Todo] into a [DatabaseTodo]
     */
    internal data class HydrateTodo(val original: Todo, val id: Long) : DatabaseAction()

    companion object {
        /**
         * Updates the title of [list] to be [title]
         */
        internal fun UpdateListTitle(list: DatabaseTodoList, title: String) =
            asyncAction<AppState> { _, _ ->
                val database by inject<Database>()
                database.listQueries.update(id = list.id, title = title)
            }

        /**
         * Updates a Todo [item]
         */
        internal fun UpdateTodo(item: DatabaseTodo) = asyncAction<AppState> { _, _ ->
            val database by inject<Database>()
            database.todoQueries.update(id = item.id, text = item.text, isDone = item.isDone)
        }

        /**
         * Add a new [Todo] to [list]
         */
        internal fun AddTodo(list: DatabaseTodoList) =
            asyncAction<AppState> thunk@{ dispatch, getState ->
                // If there aren't any items to hydrate, return early
                if (list.items.none { it !is DatabaseTodo }) return@thunk Unit

                val database by inject<Database>()

                val id = with(database.todoQueries) {
                    insert(listId = list.id, text = "")

                    selectByListId(listId = list.id)
                        .executeAsList()
                        .last()
                        .id
                }

                val original = getState().lists
                    .first { (it as? DatabaseTodoList)?.id == list.id }
                    .items
                    .last { it !is DatabaseTodo }

                withContext(Dispatchers.Main) {
                    dispatch(HydrateTodo(original, id))
                }
            }

        /**
         * Delete a given [Todo] item
         */
        internal fun DeleteTodoById(id: Long) = asyncAction<AppState> { _, _ ->
            val database by inject<Database>()
            database.todoQueries.delete(id = id)
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
                    DatabaseTodoList(
                        id = list.id,
                        title = list.title,
                        items = todos
                            .filter { it.listId == list.id }
                            .map {
                                DatabaseTodo(
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
                    database.listQueries.seed()
                    fetchAll()
                }

            withContext(Dispatchers.Main) {
                dispatch(Action.ListsLoaded(allLists))
            }
        }
    }
}
