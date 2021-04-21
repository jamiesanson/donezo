package dev.sanson.tick.todo.store

import dev.sanson.tick.model.TodoList
import dev.sanson.tick.todo.Action
import dev.sanson.tick.todo.Screen
import dev.sanson.tick.todo.feature.list.ListsReducer
import io.kotest.matchers.shouldBe
import org.junit.Test

class ListReducerTests {

    @Test
    fun `adding todo item updates state`() {
        val list = TodoList(
            id = 1,
            title = "",
            items = emptyList()
        )

        val initialState = Screen.Lists(
            lists = listOf(list)
        )

        val newState = ListsReducer(initialState, Action.AddNewTodo(list = list))

        newState.lists.first().items.isNotEmpty() shouldBe true
    }


    @Test
    fun `editing todo item updates state`() {
        // TODO - fill in
    }

    @Test
    fun `removing todo item updates state`() {
        // TODO - fill in
    }
}