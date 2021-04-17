package nz.sanson.tick.todo.di

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dev.sanson.tick.db.Database
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module

fun ApplicationModule(context: Context, applicationScope: CoroutineScope) = module {
    single { context }
    single { applicationScope }

    // Application Database
    single { initialiseDb(get()) }
}

private fun initialiseDb(context: Context): Database {
    val driver = AndroidSqliteDriver(Database.Schema, context, "todo.db")
    return Database(driver)
}
