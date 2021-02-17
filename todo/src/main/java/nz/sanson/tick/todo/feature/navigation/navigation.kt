package nz.sanson.tick.todo.feature.navigation

import nz.sanson.tick.todo.AppState
import nz.sanson.tick.todo.Screen
import nz.sanson.tick.todo.screen
import org.reduxkotlin.reducerForActionType

/**
 * Navigation actions to be handled by [navigationReducer]
 */
sealed class Navigation {
    object Todo: Navigation()
}

val navigationReducer = reducerForActionType<AppState, Navigation> { state, action ->
    when (state.screen) {
        Screen.Splash -> when (action) {
            Navigation.Todo -> AppState.screen.set(state, Screen.Lists())
        }
        is Screen.Lists -> state
    }
}