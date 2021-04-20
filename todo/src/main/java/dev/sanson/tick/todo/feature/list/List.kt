package dev.sanson.tick.todo.feature.list

import dev.sanson.tick.model.Todo
import dev.sanson.tick.todo.Action
import dev.sanson.tick.todo.Screen
import org.reduxkotlin.Reducer

/**
 * Reducer for the main list screen
 */
val ListsReducer: Reducer<Screen.Lists> = { state, action ->
    when (action) {
        is Action.ListsLoaded -> state.copy(lists = action.lists)
        is Action.ListTitleChanged -> state.copy(
            lists = state.lists.map {
                if (it.id == action.list.id) {
                    it.copy(title = action.newTitle)
                } else {
                    it
                }
            }
        )
        is Action.TodoUpdated -> state.copy(
            lists = state.lists.map { list ->
                list.copy(
                    items = list.items.map { item ->
                        if (item.id == action.todo.id) {
                            action.todo
                        } else {
                            item
                        }
                    }
                )
            }
        )
        is Action.TodoIdPopulated -> state.copy(
            lists = state.lists.map { list ->
                if (list.id == action.listId) {
                    list.copy(
                        items = list.items
                            .reversed()
                            .map { item ->
                                if (item.id == null) {
                                    item.copy(id = action.id)
                                } else {
                                    item
                                }
                            }.reversed()
                    )
                } else {
                    list
                }
            }
        )
        is Action.AddNewTodo -> state.copy(
            lists = state.lists.map {
                if (it == action.list) {
                    it.copy(items = it.items + Todo(text = "", isDone = false))
                } else {
                    it
                }
            }
        )
        is Action.RemoveTodo -> state.copy(
            lists = state.lists.map { list ->
                if (list.items.contains(action.todo)) {
                    list.copy(items = list.items - action.todo)
                } else {
                    list
                }
            }
        )
        else -> state
    }
}
