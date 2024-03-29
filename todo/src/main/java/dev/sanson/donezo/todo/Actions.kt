@file:Suppress("FunctionName")

package dev.sanson.donezo.todo

import dev.sanson.donezo.model.Todo
import dev.sanson.donezo.model.TodoList
import dev.sanson.donezo.todo.feature.navigation.Screen

sealed class Action {
    sealed class Navigation : Action() {
        object Back : Navigation()
        data class To(val screen: Screen) : Navigation()
    }

    data class UpdateListTitle(val list: TodoList, val title: String) : Action()
    data class UpdateTodoDone(val item: Todo, val isDone: Boolean) : Action()
    data class UpdateTodoText(val item: Todo, val text: String) : Action()

    data class AddTodo(val list: TodoList) : Action()
    data class AddTodoAfter(val sibling: Todo) : Action()
    object AddList : Action()

    data class DeleteTodo(val item: Todo) : Action()
    data class DeleteList(val list: TodoList) : Action()

    data class ListsLoaded(val lists: List<TodoList>) : Action()
}
