package dev.sanson.donezo.todo.feature.list

import android.util.Log
import dev.sanson.donezo.model.Todo
import dev.sanson.donezo.model.TodoList
import dev.sanson.donezo.todo.Action
import dev.sanson.donezo.todo.AppState
import org.reduxkotlin.Dispatcher
import org.reduxkotlin.Middleware
import org.reduxkotlin.Reducer
import org.reduxkotlin.Store

val ListsReducer: Reducer<AppState> = reducer@{ state, action ->
    when (action) {
        is Action.ListsLoaded -> state.copy(lists = action.lists)
        is Action.UpdateListTitle -> state.copy(
            lists = state.lists.map {
                if (it === action.list) {
                    it.copy(title = action.title)
                } else {
                    it
                }
            }
        )
        is Action.UpdateTodoText -> state.copy(
            lists = state.lists.map { list ->
                list.copy(
                    items = list.items.map { item ->
                        if (item === action.item) {
                            item.copy(text = action.text, isDone = item.isDone)
                        } else {
                            item
                        }
                    }
                )
            }
        )
        is Action.UpdateTodoDone -> state.copy(
            lists = state.lists.map { list ->
                list.copy(
                    items = list.items.map { item ->
                        if (item === action.item) {
                            item.copy(isDone = action.isDone, text = item.text)
                        } else {
                            item
                        }
                    }
                )
            }
        )
        is Action.AddTodo -> {
            state.copy(
                lists = state.lists.map {
                    if (it === action.list) {
                        it.copy(
                            items = listOf(
                                Todo(text = "", isDone = false),
                                *it.items.toTypedArray()
                            )
                        )
                    } else {
                        it
                    }
                }
            )
        }
        is Action.AddTodoAfter -> {
            // If the current item is empty, we shouldn't add another one
            if (action.sibling.text.isBlank()) return@reducer state

            // Find the containing list
            val list = state.lists.find { it.items.contains(action.sibling) }
                ?: throw IllegalArgumentException("No list found for sibling: ${action.sibling}")

            state.copy(
                lists = state.lists.map {
                    if (it === list) {
                        it.copy(items = it.items + Todo(text = "", isDone = false))
                    } else {
                        it
                    }
                }
            )
        }
        is Action.AddList -> {
            state.copy(
                lists = state.lists + TodoList(title = "", items = emptyList())
            )
        }
        is Action.DeleteTodo -> {
            state.copy(
                lists = state.lists.map { list ->
                    if (list.items.contains(action.item)) {
                        list.copy(items = list.items - action.item)
                    } else {
                        list
                    }
                }
            )
        }
        is Action.DeleteList -> {
            state.copy(
                lists = state.lists - action.list
            )
        }
        else -> state
    }
}
