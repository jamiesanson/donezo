package nz.sanson.tick.todo

import nz.sanson.tick.todo.feature.list.ListsReducer
import nz.sanson.tick.todo.feature.splash.SplashReducer
import org.reduxkotlin.Reducer

/**
 * The root reducer for Tick. Dispatches actions to screen-specific reducers. Nothing within this
 * reducer should change the screen - that is done in the [NavigationReducer].
 */
val RootReducer: Reducer<AppState> = { state, action ->
    AppState.screen.modify(state) {
        when (val current = it) {
            is Screen.Splash -> SplashReducer(current, action)
            is Screen.Lists -> ListsReducer(current, action)
        }
    }
}