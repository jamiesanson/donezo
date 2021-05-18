package dev.sanson.tick.todo.feature.list.database

import dev.sanson.tick.model.Todo
import dev.sanson.tick.model.TodoList

data class DatabaseTodoList(
    val id: Long,
    override val title: String,
    override val items: List<Todo>
) : TodoList {

    override fun copy(title: String, items: List<Todo>): DatabaseTodoList {
        return DatabaseTodoList(id, title, items)
    }
}

data class DatabaseTodo(
    val id: Long,
    override val text: String,
    override val isDone: Boolean
) : Todo {

    override fun copy(text: String, isDone: Boolean): DatabaseTodo {
        return DatabaseTodo(id, text, isDone)
    }
}