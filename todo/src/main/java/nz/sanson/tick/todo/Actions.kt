package nz.sanson.tick.todo

import nz.sanson.tick.todo.model.TodoList

sealed class Action {
    sealed class Navigation : Action() {
        object Todo: Navigation()
    }

    data class ListsLoaded(val lists: List<TodoList>) : Action()
}

