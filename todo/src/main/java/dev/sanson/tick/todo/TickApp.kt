package dev.sanson.tick.todo

import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineScope
import dev.sanson.tick.todo.di.ApplicationModule
import dev.sanson.tick.todo.feature.list.ListObservationMiddleware
import dev.sanson.tick.todo.feature.navigation.NavigationReducer
import dev.sanson.tick.arch.redux.createThunkMiddleware
import dev.sanson.tick.backend.Backend
import dev.sanson.tick.todo.feature.navigation.BackNavigationMiddleware
import org.koin.core.context.startKoin
import org.reduxkotlin.Store
import org.reduxkotlin.applyMiddleware
import org.reduxkotlin.combineReducers
import org.reduxkotlin.createThreadSafeStore

data class Configuration(
    val databaseDriver: SqlDriver,
    val availableBackends: List<Backend>
)

/**
 * [createApp] wires up all the necessary Redux components, returning the [Store] to the consuming
 * frontend.
 */
fun createApp(
    applicationScope: CoroutineScope,
    appConfiguration: Configuration,
    closeApp: () -> Unit
): Store<AppState> {
    startKoin {
        modules(ApplicationModule(applicationScope, appConfiguration))
    }

    val reducer = combineReducers(NavigationReducer, RootReducer)

    val middleware = applyMiddleware(
        createThunkMiddleware(),
        ListObservationMiddleware,
        BackNavigationMiddleware(closeApp)
    )

    val initialState = AppState(backends = appConfiguration.availableBackends)

    val store = createThreadSafeStore(reducer, initialState, middleware)

    store.dispatch(Action.SeedDatabaseIfEmpty())

    return store
}
