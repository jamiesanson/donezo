package nz.sanson.tick.todo.feature.list

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import nz.sanson.tick.todo.Action
import nz.sanson.tick.todo.AppState
import nz.sanson.tick.todo.Database
import nz.sanson.tick.todo.Screen
import nz.sanson.tick.todo.di.inject
import nz.sanson.tick.todo.model.Todo
import nz.sanson.tick.todo.model.TodoList
import org.reduxkotlin.Reducer
import org.reduxkotlin.middleware

/**
 * Reducer for the main list screen
 */
val ListsReducer: Reducer<Screen.Lists> = { state, action ->
    when (action) {
        is Action.ListsLoaded -> state.copy(
            loading = false,
            lists = action.lists
        )
        else -> state
    }
}

private var observationJob: Job? = null

/**
 * Start observing the TODO list when we land on the todo screen
 */
val ListObservationMiddleware = middleware<AppState> { store, next, action ->
    when (action) {
        is Action.Navigation.Todo -> {
            val scope by inject<CoroutineScope>()

            // Pass the navigation action along so that the list reducer is applied
            // before the first emission from the database
            next(action)

            // Start observation
            observationJob = scope.launch {
                val database by inject<Database>()

                fun <T : Any> Query<T>.asListFlow() = asFlow().map { it.executeAsList() }

                val lists = database.listQueries.selectAll().asListFlow()
                val todos = database.todoQueries.selectAll().asListFlow()

                lists
                    .combine(todos) { allLists, allTodos ->
                        allLists.map { list ->
                            val todoItems = allTodos.filter { it.listId == list.id }
                            TodoList(
                                id = list.id,
                                title = list.title,
                                items = todoItems.map { Todo(it.id, it.text, it.isDone) }
                            )
                        }
                    }
                    .collect {
                        store.dispatch(Action.ListsLoaded(it))
                    }
            }
        }
        else -> next(action)
    }
}