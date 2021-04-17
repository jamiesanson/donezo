package dev.sanson.tick.db

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import dev.sanson.tick.model.TodoList
import dev.sanson.tick.todo.db.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import dev.sanson.tick.model.Todo as TodoModel

fun Todo.toModel(): TodoModel = TodoModel(
    id, text, isDone
)

fun Database.getAllAsFlow(): Flow<List<TodoList>> {
    fun <T : Any> Query<T>.asListFlow() = asFlow().map { it.executeAsList() }

    val lists = listQueries.selectAll().asListFlow()
    val todos = todoQueries.selectAll().asListFlow()

    return lists
        .combine(todos) { allLists, allTodos ->
            allLists.map { list ->
                val todoItems = allTodos.filter { it.listId == list.id }
                TodoList(
                    id = list.id,
                    title = list.title,
                    items = todoItems.map(Todo::toModel)
                )
            }
        }
}