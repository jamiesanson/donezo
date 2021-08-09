package dev.sanson.donezo.todo.storage

import dev.sanson.donezo.model.TodoList

interface LocalStorage {

    suspend fun load(): List<TodoList>

    suspend fun save(todos: List<TodoList>)
}
