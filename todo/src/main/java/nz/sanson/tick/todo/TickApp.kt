package nz.sanson.tick.todo

import nz.sanson.tick.todo.feature.navigation.navigationReducer
import org.reduxkotlin.Store
import org.reduxkotlin.combineReducers
import org.reduxkotlin.createThreadSafeStore

/**
 * [createApp] wires up all the necessary Redux components, returning the [Store] to the consuming
 * frontend.
 */
fun createApp(): Store<AppState> {
    val reducer = combineReducers(navigationReducer, rootReducer)
    return createThreadSafeStore(reducer, AppState())
}