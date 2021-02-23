package nz.sanson.tick.todo.feature.navigation

import nz.sanson.tick.todo.*
import org.reduxkotlin.reducerForActionType

/**
 * Navigation actions to be handled by [NavigationReducer]
 */

val NavigationReducer = reducerForActionType<AppState, Action.Navigation> { state, action ->
  when (state.screen) {
    Screen.Splash -> when (action) {
      Action.Navigation.Todo -> AppState.screen.set(state, Screen.Lists())
    }
    is Screen.Lists -> state
  }
}