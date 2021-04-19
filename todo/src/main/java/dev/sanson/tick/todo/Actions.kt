@file:Suppress("FunctionName")

package dev.sanson.tick.todo

import dev.sanson.tick.arch.di.inject
import dev.sanson.tick.db.Database
import kotlinx.coroutines.launch
import dev.sanson.tick.model.Todo
import dev.sanson.tick.model.TodoList
import dev.sanson.tick.arch.redux.Thunk

sealed class Action {
    sealed class Navigation : Action() {
        object Back : Navigation()
        object Todo : Navigation()
        object SyncSettings: Navigation()
    }

    data class ListsLoaded(val lists: List<TodoList>) : Action()

    /**
     * Thunk functions make sense to put in the companion object, such that they
     * can be referenced like the other actions
     */
    companion object {

        /**
         * Updates the title of [list] to be [title]
         */
        fun OnListTitleChange(list: TodoList, title: String): Thunk<AppState> = { _, _, _ ->
            val database by inject<Database>()

            launch {
                database.listQueries.update(id = list.id!!, title = title)
            }
        }

        /**
         * Updates a Todo [item]
         */
        fun OnTodoChange(item: Todo): Thunk<AppState> = { _, _, _ ->
            val database by inject<Database>()

            launch {
                database.todoQueries.update(id = item.id!!, text = item.text, isDone = item.isDone)
            }
        }

        /**
         * Add a new [Todo] to [list]
         */
        fun NewTodoItem(list: TodoList): Thunk<AppState> = { _, _, _ ->
            val database by inject<Database>()

            launch {
                database.todoQueries.insert(listId = list.id!!, text = "")
            }
        }

        /**
         * Add a new [Todo] to [list]
         */
        fun NewTodoItemInSameList(item: Todo): Thunk<AppState> = { _, _, _ ->
            val database by inject<Database>()

            launch {
                val dbItem = database.todoQueries.select(id = item.id!!).executeAsOne()
                database.todoQueries.insert(listId = dbItem.listId, text = "")
            }
        }

        /**
         * Delete a given [Todo] item
         */
        fun DeleteTodoItem(item: Todo): Thunk<AppState> = { _, _, _ ->
            val database by inject<Database>()

            launch {
                database.todoQueries.delete(id = item.id!!)
            }
        }

        /**
         * Thunk fired on app start to seed the DB if there's no lists
         */
        internal fun SeedDatabaseIfEmpty(): Thunk<AppState> = { _, _, _ ->
            val database by inject<Database>()

            launch {
                database.listQueries.seed()
            }
        }
    }
}
