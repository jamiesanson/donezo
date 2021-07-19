package dev.sanson.donezo.todo.store

import dev.sanson.donezo.model.Todo
import dev.sanson.donezo.model.TodoList
import dev.sanson.donezo.todo.Action
import dev.sanson.donezo.todo.AppState
import dev.sanson.donezo.todo.feature.list.ListsReducer
import io.kotest.matchers.shouldBe
import org.junit.Test

class ListReducerTests {

    @Test
    fun `adding todo item updates state`() {
        val list = TodoList(
            title = "",
            items = emptyList()
        )

        val initialState = AppState(
            lists = listOf(list)
        )

        val newState = ListsReducer(initialState, Action.AddTodo(list = list))

        newState.lists.first().items.isNotEmpty() shouldBe true
    }

    @Test
    fun `editing todo item updates state`() {
        val todo = Todo(
            text = "",
            isDone = false
        )

        val initialState = AppState(
            lists = listOf(
                TodoList(
                    title = "",
                    items = listOf(todo)
                )
            )
        )

        val action = Action.UpdateTodoText(item = todo, text = "Hello world!")

        val newState = ListsReducer(initialState, action)

        newState.lists.first().items.first().text shouldBe "Hello world!"
    }

    @Test
    fun `removing todo item updates state`() {
        val todo = Todo(
            text = "",
            isDone = false
        )

        val initialState = AppState(
            lists = listOf(
                TodoList(
                    title = "",
                    items = listOf(todo)
                )
            )
        )

        val action = Action.DeleteTodo(todo)

        val newState = ListsReducer(initialState, action)

        newState.lists.first().items.size shouldBe 0
    }
}
