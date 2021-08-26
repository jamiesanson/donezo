package dev.sanson.donezo.todo

import dev.sanson.donezo.arch.redux.createThunkMiddleware
import dev.sanson.donezo.backend.Backend
import dev.sanson.donezo.model.Todo
import dev.sanson.donezo.model.TodoList
import dev.sanson.donezo.todo.di.ApplicationModule
import dev.sanson.donezo.todo.feature.list.ListInteractionMiddleware
import dev.sanson.donezo.todo.feature.list.ListsReducer
import dev.sanson.donezo.todo.feature.navigation.BackNavigationMiddleware
import dev.sanson.donezo.todo.feature.navigation.NavigationReducer
import dev.sanson.donezo.todo.storage.LocalStorage
import dev.sanson.donezo.todo.storage.initialiseLocalStorage
import kotlinx.coroutines.CoroutineScope
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.reduxkotlin.Store
import org.reduxkotlin.applyMiddleware
import org.reduxkotlin.combineReducers
import org.reduxkotlin.createThreadSafeStore

data class AppSettings(
    val localStorage: LocalStorage,
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
        NavigationReducer,
        ListsReducer
    )

    val middleware = applyMiddleware(
        ListInteractionMiddleware,
        BackNavigationMiddleware(closeApp),
        createThunkMiddleware()
    )

    val initialState = AppState(backends = appSettings.availableBackends)

    val store = createThreadSafeStore(reducer, initialState, middleware)

    store.initialiseLocalStorage(
        initialState = listOf(
            TodoList(
                title = "// TODO: Add your own title",
                items = listOf(
                    Todo(text = "Hi, I'm a todo item!", isDone = true),
                    Todo(text = "<-- try checking me off", isDone = false)
                )
            )
        )
    )

    return store
}

/**
 * [destroyApp] does any tidy-up necessary, when shutting down the app under normal operating conditions
 */
fun destroyApp() {
    stopKoin()
}
