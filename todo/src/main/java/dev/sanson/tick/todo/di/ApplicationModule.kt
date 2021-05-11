package dev.sanson.tick.todo.di

import dev.sanson.tick.db.Database
import dev.sanson.tick.todo.AppSettings
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module

fun ApplicationModule(applicationScope: CoroutineScope, appSettings: AppSettings) = module {
    single { applicationScope }

    // Application Database
    single { Database(appSettings.databaseDriver) }

    // Backends
    single { appSettings.availableBackends }
}
