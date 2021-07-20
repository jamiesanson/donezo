package dev.sanson.donezo.todo

import com.squareup.sqldelight.db.SqlDriver
import dev.sanson.donezo.arch.redux.createThunkMiddleware
import dev.sanson.donezo.backend.Backend
import dev.sanson.donezo.todo.di.ApplicationModule
import dev.sanson.donezo.todo.feature.list.ListsReducer
import dev.sanson.donezo.todo.feature.list.database.DatabaseAction
import dev.sanson.donezo.todo.feature.list.database.DatabaseMiddleware
import dev.sanson.donezo.todo.feature.list.database.DatabaseReducer
import dev.sanson.donezo.todo.feature.navigation.BackNavigationMiddleware
import dev.sanson.donezo.todo.feature.navigation.NavigationReducer
import kotlinx.coroutines.CoroutineScope
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
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

/**
 * [destroyApp] does any tidy-up necessary, when shutting down the app under normal operating conditions
 */
fun destroyApp() {
    stopKoin()
}
