package nz.sanson.tick.todo

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import nz.sanson.tick.todo.feature.navigation.navigationReducer
import org.reduxkotlin.Store
import org.reduxkotlin.combineReducers
import org.reduxkotlin.createThreadSafeStore

/**
 * [createApp] wires up all the necessary Redux components, returning the [Store] to the consuming
 * frontend.
 */
fun createApp(context: Context): Store<AppState> {
    val database = initialiseDb(context)

    val reducer = combineReducers(navigationReducer, rootReducer)

    return createThreadSafeStore(reducer, AppState())
}

private fun initialiseDb(context: Context): Database {
    val driver = AndroidSqliteDriver(Database.Schema, context, "todo.db")
    return Database(driver)
}