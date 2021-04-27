package dev.sanson.tick.todo.feature.database

import dev.sanson.tick.model.Todo
import dev.sanson.tick.model.TodoList

interface DatabaseTodoList : TodoList {
    val id: Long
}

interface DatabaseTodo : Todo {
    val id: Long
}

internal fun DatabaseTodo.copyInternal(text: String) = object : DatabaseTodo by this {
    override val text: String = text
}

internal fun DatabaseTodo.copyInternal(isDone: Boolean) = object : DatabaseTodo by this {
    override val isDone: Boolean = isDone
}


internal fun Todo.copy(id: Long): DatabaseTodo = object : DatabaseTodo, Todo by this {
    override val id: Long = id
}