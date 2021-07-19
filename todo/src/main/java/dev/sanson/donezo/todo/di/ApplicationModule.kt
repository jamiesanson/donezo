package dev.sanson.donezo.todo.di

import dev.sanson.donezo.db.Database
import dev.sanson.donezo.todo.AppSettings
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module

fun ApplicationModule(applicationScope: CoroutineScope, appSettings: AppSettings) = module {
    single { applicationScope }

    // Application Database
    single { Database(appSettings.databaseDriver) }

    // Backends
    single { appSettings.availableBackends }
}
