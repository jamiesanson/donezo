@file:Suppress("FunctionName", "unused")

package dev.sanson.tick.todo.feature.navigation

import dev.sanson.tick.todo.Action
import dev.sanson.tick.todo.AppState
import org.reduxkotlin.middleware
import org.reduxkotlin.reducerForActionType

/**
 * Navigation state for Tick
 */
data class Navigation(
    /**
     * Backstack of previous screens, with index zero being the bottom of the stack
     */
    val backstack: List<Screen> = emptyList(),
    val currentScreen: Screen = Screen.Lists
)

/**
 * Screen keys representing where in the navigation hierarchy the app should be
 */
sealed class Screen {
    object Lists : Screen()
    object SyncSettings : Screen()
}

fun BackNavigationMiddleware(exitApplication: () -> Unit) =
    middleware<AppState> { store, next, action ->
        if (action == Action.Navigation.Back && store.state.navigation.backstack.isEmpty()) {
            exitApplication()
        } else {
            next(action)
        }
    }

val NavigationReducer = reducerForActionType<AppState, Action.Navigation> { state, action ->
    when (action) {
        Action.Navigation.Back -> state.pop()
        is Action.Navigation.To -> {
            if (state.navigation.currentScreen is Screen.Lists && action.screen is Screen.SyncSettings) {
                state.push(Screen.SyncSettings)
            } else {
                state
            }
        }
    }
}

private fun AppState.replace(screen: Screen): AppState =
    copy(navigation = navigation.copy(currentScreen = screen))

private fun AppState.push(screen: Screen): AppState = copy(
    navigation = navigation.copy(
        backstack = listOf(*navigation.backstack.toTypedArray(), navigation.currentScreen),
        currentScreen = screen
    )
)

private fun AppState.pop(): AppState = copy(
    navigation = navigation.copy(
        backstack = navigation.backstack.dropLast(1),
        currentScreen = navigation.backstack.last()
    )
)
