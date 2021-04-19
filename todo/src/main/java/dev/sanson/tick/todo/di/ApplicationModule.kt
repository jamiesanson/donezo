package dev.sanson.tick.todo.di

import dev.sanson.tick.db.Database
import kotlinx.coroutines.CoroutineScope
import dev.sanson.tick.todo.Configuration
import org.koin.dsl.module

fun ApplicationModule(applicationScope: CoroutineScope, configuration: Configuration) = module {
    single { applicationScope }

    // Application Database
    single { Database(configuration.databaseDriver) }

    // Backends
    single { configuration.availableBackends }
}
