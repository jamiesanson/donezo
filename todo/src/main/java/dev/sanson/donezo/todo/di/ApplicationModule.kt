package dev.sanson.donezo.todo.di

import dev.sanson.donezo.todo.AppSettings
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module

fun ApplicationModule(applicationScope: CoroutineScope, appSettings: AppSettings) = module {
    single { applicationScope }

    // Local storage
    single { appSettings.localStorage }

    // Backends
    single { appSettings.availableBackends }
}
