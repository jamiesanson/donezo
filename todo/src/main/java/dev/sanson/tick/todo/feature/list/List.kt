package dev.sanson.tick.todo.feature.list

import dev.sanson.tick.model.Todo
import dev.sanson.tick.todo.Action
import dev.sanson.tick.todo.AppState
import org.reduxkotlin.Reducer

val ListsReducer: Reducer<AppState> = reducer@{ state, action ->
    when (action) {
        is Action.ListsLoaded -> state.copy(lists = action.lists)
        is Action.UpdateListTitle -> state.copy(
            lists = state.lists.map {
                if (it == action.list) {
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
                        if (item == action.item) {
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
                        if (item == action.item) {
                            item.copy(isDone = action.isDone, text = item.text)
                        } else {
                            item
                        }
                    }
                )
            }
        )
        is Action.AddTodo -> {
            if (action.list.items.lastOrNull()?.text?.isBlank() == true) return@reducer state

            state.copy(
                lists = state.lists.map {
                    if (it == action.list) {
                        it.copy(items = it.items + Todo(text = "", isDone = false))
                    } else {
                        it
                    }
                }
            )
        }
        is Action.AddTodoAsSibling -> {
            val list = state.lists.find { it.items.contains(action.sibling) }
                ?: throw IllegalArgumentException("No list found for sibling: ${action.sibling}")

            // Don't permit duplicate blank items
            if (list.items.last().text.isBlank()) return@reducer state

            state.copy(
                lists = state.lists.map {
                    if (it == list) {
                        it.copy(items = it.items + Todo(text = "", isDone = false))
                    } else {
                        it
                    }
                }
            )
        }
        is Action.DeleteTodo -> state.copy(
            lists = state.lists.map { list ->
                if (list.items.contains(action.item)) {
                    list.copy(items = list.items - action.item)
                } else {
                    list
                }
            }
        )
        else -> state
    }
}