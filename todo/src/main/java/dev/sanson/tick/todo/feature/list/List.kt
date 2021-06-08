package dev.sanson.tick.todo.feature.list

import dev.sanson.tick.model.Todo
import dev.sanson.tick.model.TodoList
import dev.sanson.tick.todo.Action
import dev.sanson.tick.todo.AppState
import dev.sanson.tick.todo.feature.list.database.DatabaseTodoList
import dev.sanson.tick.todo.feature.navigation.Screen
import org.reduxkotlin.Reducer

/**
 * Reducer for the main list screen. Includes focusing
 */
val ListsReducer: Reducer<AppState> = reducer@{ state, action ->
    val interimState = SimpleListReducer(state, action)

    when (state.navigation.currentScreen) {
        is Screen.Lists -> {
            when (action) {
                // Focus the new item
                is Action.AddTodo, is Action.AddTodoAsSibling -> {
                    val oldItems = state.lists.flatMap { it.items }
                    val newItems = interimState.lists.flatMap { it.items }

                    interimState.copy(
                        navigation = state.navigation.copy(
                            currentScreen = state.navigation.currentScreen.copy(
                                focussedItem = newItems.find { it !in oldItems }
                            )
                        )
                    )
                }

                // Shift focus up one item
                is Action.DeleteTodo -> {
                    val containingList = state.lists.find { it.items.contains(action.item) }!!

                    // If the item to be deleted was top of the list, focus the title, else shift it up one
                    val newFocus: Any? = if (containingList.items.indexOf(action.item) == 0) {
                        interimState.lists.find { (it as? DatabaseTodoList)?.id == (containingList as? DatabaseTodoList)?.id }
                    } else {
                        containingList.items[containingList.items.indexOf(action.item) - 1]
                    }

                    interimState.copy(
                        navigation = state.navigation.copy(
                            currentScreen = state.navigation.currentScreen.copy(
                                focussedItem = newFocus
                            )
                        )
                    )
                }

                is Action.RequestFocus -> {
                    interimState.copy(
                        navigation = state.navigation.copy(
                            currentScreen = state.navigation.currentScreen.copy(
                                focussedItem = if (action.item is TodoList || action.item is Todo) action.item else null
                            )
                        )
                    )
                }

                is Action.ClearFocus -> {
                    interimState.copy(
                        navigation = state.navigation.copy(
                            currentScreen = state.navigation.currentScreen.copy(
                                focussedItem = null
                            )
                        )
                    )
                }

                else -> interimState
            }

        }
        else -> interimState
    }
}

val SimpleListReducer: Reducer<AppState> = reducer@{ state, action ->
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
