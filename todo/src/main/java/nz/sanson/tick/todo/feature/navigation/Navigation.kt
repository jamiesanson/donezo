package nz.sanson.tick.todo.feature.navigation

import nz.sanson.tick.todo.Action
import nz.sanson.tick.todo.AppState
import nz.sanson.tick.todo.Screen
import org.reduxkotlin.middleware
import org.reduxkotlin.reducerForActionType

fun BackNavigationMiddleware(exitApplication: () -> Unit) = middleware<AppState> { store, next, action ->
    if (action == Action.Navigation.Back && store.state.backstack.isEmpty()) {
        exitApplication()
    } else {
        next(action)
    }
}

val NavigationReducer = reducerForActionType<AppState, Action.Navigation> { state, action ->
    when (action) {
        Action.Navigation.Back -> state.pop()
        Action.Navigation.SyncSettings -> state.push(Screen.SyncSettings.fromBackends(state.backends))
        Action.Navigation.Todo -> if (state.currentScreen == Screen.Splash) state.replace(Screen.Lists()) else state
    }
}

private fun AppState.replace(screen: Screen): AppState = copy(currentScreen = screen)

private fun AppState.push(screen: Screen): AppState = copy(
    backstack = listOf(*backstack.toTypedArray(), currentScreen),
    currentScreen = screen
)

private fun AppState.pop(): AppState = copy(
    backstack = backstack.dropLast(1),
    currentScreen = backstack.last()
)
