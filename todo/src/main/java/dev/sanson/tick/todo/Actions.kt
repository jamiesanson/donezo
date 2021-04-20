@file:Suppress("FunctionName")

package dev.sanson.tick.todo

import dev.sanson.tick.arch.di.inject
import dev.sanson.tick.arch.redux.Thunk
import dev.sanson.tick.db.Database
import dev.sanson.tick.db.getAllAsFlow
import dev.sanson.tick.model.Todo
import dev.sanson.tick.model.TodoList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class Action {
    sealed class Navigation : Action() {
        object Back : Navigation()
        object SyncSettings : Navigation()
    }

    // Internal events fired by Thunks, to not confuse the autocompletion in
    // the consuming modules
    internal data class ListsLoaded(val lists: List<TodoList>)
    internal data class ListTitleChanged(val list: TodoList, val newTitle: String)
    internal data class TodoUpdated(val todo: Todo)
    internal data class TodoIdPopulated(val id: Long, val listId: Long)

    internal data class AddNewTodo(val list: TodoList)
    internal data class RemoveTodo(val todo: Todo)

    /**
     * Thunk functions make sense to put in the companion object, such that they
     * can be referenced like the other actions
     */
    companion object {

        /**
         * Updates the title of [list] to be [title]
         */
        fun UpdateListTitle(list: TodoList, title: String): Thunk<AppState> = { dispatch, _, _ ->
            dispatch(ListTitleChanged(list, title))

            launch {
                val database by inject<Database>()
                database.listQueries.update(id = list.id!!, title = title)
            }
        }

        /**
         * Updates a Todo [item]
         */
        fun UpdateTodo(item: Todo): Thunk<AppState> = { dispatch, _, _ ->
            dispatch(TodoUpdated(item))

            launch {
                val database by inject<Database>()
                database.todoQueries.update(id = item.id!!, text = item.text, isDone = item.isDone)
            }
        }

        /**
         * Add a new [Todo] to [list]
         */
        fun AddTodo(list: TodoList): Thunk<AppState> = { dispatch, _, _ ->
            dispatch(AddNewTodo(list))

            launch {
                val database by inject<Database>()
                val id = withContext(Dispatchers.IO) {
                    with(database.todoQueries) {
                        insert(listId = list.id!!, text = "")

                        selectByListId(listId = list.id!!)
                            .executeAsList()
                            .last()
                            .id
                    }
                }

                dispatch(TodoIdPopulated(id = id, listId = list.id!!))
            }
        }

        /**
         * Add a new [Todo] to the same [TodoList]
         */
        fun AddTodoAsSibling(sibling: Todo): Thunk<AppState> = { dispatch, getState, _ ->
            val state = getState().currentScreen as Screen.Lists
            val list = state.lists.find { it.items.contains(sibling) }
                ?: throw IllegalArgumentException("No list found for sibling: $sibling")
            dispatch(AddTodo(list))
        }

        /**
         * Delete a given [Todo] item
         */
        fun DeleteTodo(item: Todo): Thunk<AppState> = { dispatch, _, _ ->
            dispatch(RemoveTodo(item))

            launch {
                val database by inject<Database>()
                database.todoQueries.delete(id = item.id!!)
            }
        }

        /**
         * Load all lists from database, seeding some starter items if the database is empty
         */
        internal fun LoadFromDatabase(): Thunk<AppState> = { dispatch, _, _ ->
            launch {
                val database by inject<Database>()
                val allLists = database.getAllAsFlow()
                    .first()
                    .ifEmpty {
                        database.listQueries.seed()
                        database.getAllAsFlow().first()
                    }

                dispatch(ListsLoaded(allLists))
            }
        }
    }
}
