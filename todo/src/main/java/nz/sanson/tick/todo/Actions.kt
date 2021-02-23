package nz.sanson.tick.todo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import nz.sanson.tick.todo.di.inject
import nz.sanson.tick.todo.model.Todo
import nz.sanson.tick.todo.model.TodoList
import nz.sanson.tick.todo.redux.Thunk

sealed class Action {
    sealed class Navigation : Action() {
        object Todo: Navigation()
    }

    data class ListsLoaded(val lists: List<TodoList>) : Action()

    /**
     * Thunk functions make sense to put in the companion object, such that they
     * can eb
     */
    companion object {

        fun ListTitleUpdated(list: TodoList, title: String): Thunk<AppState> = { _, _, _ ->
            val database by inject<Database>()
            val scope by inject<CoroutineScope>()

            scope.launch {
                database.listQueries.update(id = list.id!!, title = title)
            }
        }

        fun TodoItemUpdated(item: Todo, text: String = item.text, isDone: Boolean = item.isDone): Thunk<AppState> = { _, _, _ ->
            val database by inject<Database>()
            val scope by inject<CoroutineScope>()

            scope.launch {
                database.todoQueries.update(id = item.id!!, text = text, isDone = isDone)
            }
        }
    }
}

