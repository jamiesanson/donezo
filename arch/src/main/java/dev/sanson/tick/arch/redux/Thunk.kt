package dev.sanson.tick.arch.redux

import dev.sanson.tick.arch.di.inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.reduxkotlin.Dispatcher
import org.reduxkotlin.GetState
import org.reduxkotlin.Middleware

/**
 * Thunk middleware for async action dispatches.
 * Usage:
 *    val store = createStore(myReducer, initialState,
 *          applyMiddleware(thunk, myMiddleware))
 *
 *    fun myNetworkThunk(query: String): Thunk<AppState> = { dispatch, getState, extraArgument ->
 *          launch {
 *              dispatch(LoadingAction())
 *              //do async stuff
 *              val result = api.fetch(query)
 *              dispatch(CompleteAction(result))
 *          }
 *      }
 *
 *    store.dispatch(myNetworkThunk("query"))
 */
typealias Thunk<State> = (dispatch: Dispatcher, getState: GetState<State>) -> Any

/**
 * Convenience function for wrapping thunk functionality in a coroutine
 */
fun <State> asyncAction(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    block: suspend CoroutineScope.(dispatch: Dispatcher, getState: GetState<State>) -> Any
): Thunk<State> = { dispatch, getState ->
    val scope by inject<CoroutineScope>()
    scope.launch(dispatcher) { block(dispatch, getState) }
}

fun <State> createThunkMiddleware(): Middleware<State> =
    { store ->
        { next: Dispatcher ->
            { action: Any ->
                if (action is Function<*>) {
                    @Suppress("UNCHECKED_CAST")
                    val thunk = try {
                        (action as Thunk<State>)
                    } catch (e: ClassCastException) {
                        throw IllegalArgumentException(
                            "Dispatching functions must use type Thunk:",
                            e
                        )
                    }

                    thunk(store.dispatch, store.getState)
                } else {
                    next(action)
                }
            }
        }
    }
