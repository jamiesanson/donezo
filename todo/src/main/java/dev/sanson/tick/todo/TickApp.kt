package dev.sanson.tick.todo

import com.squareup.sqldelight.db.SqlDriver
import dev.sanson.tick.arch.redux.createThunkMiddleware
import dev.sanson.tick.backend.Backend
import dev.sanson.tick.todo.di.ApplicationModule
import dev.sanson.tick.todo.feature.list.ListsReducer
import dev.sanson.tick.todo.feature.list.database.DatabaseAction
import dev.sanson.tick.todo.feature.list.database.DatabaseMiddleware
import dev.sanson.tick.todo.feature.list.database.DatabaseReducer
import dev.sanson.tick.todo.feature.navigation.BackNavigationMiddleware
import dev.sanson.tick.todo.feature.navigation.NavigationReducer
import kotlinx.coroutines.CoroutineScope
import org.koin.core.context.startKoin
import org.reduxkotlin.Store
import org.reduxkotlin.applyMiddleware
import org.reduxkotlin.combineReducers
import org.reduxkotlin.createThreadSafeStore

data class AppSettings(
    val databaseDriver: SqlDriver,
    val availableBackends: List<Backend>
)

/**
 * [createApp] wires up all the necessary Redux components, returning the [Store] to the consuming
 * frontend.
 */
fun createApp(
    applicationScope: CoroutineScope,
    appSettings: AppSettings,
    closeApp: () -> Unit
): Store<AppState> {
    startKoin {
        modules(ApplicationModule(applicationScope, appSettings))
    }

    val reducer = combineReducers(
        DatabaseReducer,
        NavigationReducer,
        ListsReducer
    )

    val middleware = applyMiddleware(
        DatabaseMiddleware,
        BackNavigationMiddleware(closeApp),
        createThunkMiddleware()
    )

    val initialState = AppState(backends = appSettings.availableBackends)

    val store = createThreadSafeStore(reducer, initialState, middleware)

    store.dispatch(DatabaseAction.FetchAll())

    return store
}
