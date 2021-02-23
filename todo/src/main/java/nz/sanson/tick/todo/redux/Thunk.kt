package nz.sanson.tick.todo.redux

import kotlinx.coroutines.CoroutineScope
import nz.sanson.tick.todo.di.inject
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
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
typealias Thunk<State> = CoroutineScope.(dispatch: Dispatcher, getState: GetState<State>, extraArg: Any?) -> Any
typealias ThunkMiddleware<State> = Middleware<State>

fun <State> ThunkMiddleware<State>.withExtraArgument(arg: Any?) = createThunkMiddleware<State>(arg)

fun <State> createThunkMiddleware(extraArgument: Any? = null): ThunkMiddleware<State> =
    { store ->
      { next: Dispatcher ->
        { action: Any ->
          if (action is Function<*>) {
            @Suppress("UNCHECKED_CAST")
            val thunk = try {
              (action as Thunk<State>)
            } catch (e: ClassCastException) {
              throw IllegalArgumentException("Dispatching functions must use type Thunk:", e)
            }
            val scope by inject<CoroutineScope>()
            scope.thunk(store.dispatch, store.getState, extraArgument)
          } else {
            next(action)
          }
        }
      }
    }