@file:Suppress("FunctionName")

package dev.sanson.tick.todo

import dev.sanson.tick.model.Todo
import dev.sanson.tick.model.TodoList
import dev.sanson.tick.todo.feature.navigation.Screen

sealed class Action {
    sealed class Navigation : Action() {
        object Back : Navigation()
        data class To(val screen: Screen) : Navigation()
    }

    data class UpdateListTitle(val list: TodoList, val title: String) : Action()
    data class UpdateTodoDone(val item: Todo, val isDone: Boolean) : Action()
    data class UpdateTodoText(val item: Todo, val text: String) : Action()
    data class AddTodo(val list: TodoList) : Action()
    data class AddTodoAsSibling(val sibling: Todo) : Action()
    data class DeleteTodo(val item: Todo) : Action()

    data class RequestFocus(val item: Any) : Action()
    object ClearFocus : Action()

    data class ListsLoaded(val lists: List<TodoList>) : Action()
}
