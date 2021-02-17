package nz.sanson.tick.todo

import nz.sanson.tick.todo.feature.list.listsReducer
import nz.sanson.tick.todo.feature.splash.splashReducer
import org.reduxkotlin.Reducer

/**
 * The root reducer for Tick. Dispatches actions to screen-specific reducers. Nothing within this
 * reducer should change the screen - that is done in the [navigationReducer].
 */
val rootReducer: Reducer<AppState> = { state, action ->
    AppState.screen.modify(state) {
        when (val current = it) {
            is Screen.Splash -> splashReducer(current, action)
            is Screen.Lists -> listsReducer(current, action)
        }
    }
}