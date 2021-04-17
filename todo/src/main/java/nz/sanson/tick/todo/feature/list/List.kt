package nz.sanson.tick.todo.feature.list

import dev.sanson.tick.arch.di.inject
import dev.sanson.tick.db.Database
import dev.sanson.tick.db.getAllAsFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import nz.sanson.tick.todo.Action
import nz.sanson.tick.todo.AppState
import nz.sanson.tick.todo.Screen
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
                database.getAllAsFlow()
                    .collect {
                        store.dispatch(Action.ListsLoaded(it))
                    }
            }
        }
        else -> next(action)
    }
}
