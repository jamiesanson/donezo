@file:Suppress("FunctionName")

package dev.sanson.tick.todo.feature.database

import dev.sanson.tick.arch.di.inject
import dev.sanson.tick.arch.redux.Thunk
import dev.sanson.tick.db.Database
import dev.sanson.tick.model.Todo
import dev.sanson.tick.model.TodoList
import dev.sanson.tick.todo.Action
import dev.sanson.tick.todo.AppState
import dev.sanson.tick.todo.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        internal fun UpdateListTitle(list: DatabaseTodoList, title: String): Thunk<AppState> = { _, _, _ ->
            launch(Dispatchers.IO) {
                val database by inject<Database>()
                database.listQueries.update(id = list.id, title = title)
            }
        }

        /**
         * Updates a Todo [item]
         */
        internal fun UpdateTodo(item: DatabaseTodo): Thunk<AppState> = { _, _, _ ->
            launch(Dispatchers.IO) {
                val database by inject<Database>()
                database.todoQueries.update(id = item.id, text = item.text, isDone = item.isDone)
            }
        }

        /**
         * Add a new [Todo] to [list]
         */
        internal fun AddTodo(list: DatabaseTodoList, state: Screen.Lists): Thunk<AppState> = { dispatch, _, _ ->
            launch {
                val database by inject<Database>()

                val id = withContext(Dispatchers.IO) {
                    with(database.todoQueries) {
                        insert(listId = list.id, text = "")

                        selectByListId(listId = list.id)
                            .executeAsList()
                            .last()
                            .id
                    }
                }

                val original = state.lists
                    .first { (it as? DatabaseTodoList)?.id == list.id }
                    .items
                    .last { it !is DatabaseTodo }

                dispatch(HydrateTodo(original, id))
            }

        }

        /**
         * Delete a given [Todo] item
         */
        internal fun DeleteTodoById(id: Long): Thunk<AppState> = { _, _, _ ->
            launch(Dispatchers.IO) {
                val database by inject<Database>()
                database.todoQueries.delete(id = id)
            }
        }

        /**
         * Load all lists from database, seeding some starter items if the database is empty
         */
        internal fun FetchAll(): Thunk<AppState> = { dispatch, _, _ ->
            launch(Dispatchers.IO) {
                val database by inject<Database>()

                fun fetchAll(): List<TodoList> {
                    val lists = database.listQueries.selectAll().executeAsList()
                    val todos = database.todoQueries.selectAll().executeAsList()

                    return lists.map { list ->
                        val todoItems = todos.filter { it.listId == list.id }
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
}
