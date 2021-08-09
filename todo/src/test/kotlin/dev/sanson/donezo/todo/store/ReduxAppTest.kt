package dev.sanson.donezo.todo.store

import dev.sanson.donezo.model.TodoList
import dev.sanson.donezo.todo.AppSettings
import dev.sanson.donezo.todo.AppState
import dev.sanson.donezo.todo.storage.LocalStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.After
import org.junit.Before
import org.koin.core.context.stopKoin
import org.reduxkotlin.Store
import dev.sanson.donezo.todo.createApp as createAppImpl

abstract class ReduxAppTest {

    lateinit var store: Store<AppState>

    @OptIn(ExperimentalCoroutinesApi::class)
    val testScope = TestCoroutineScope()

    private val configuration = AppSettings(
        localStorage = object : LocalStorage {
            var data = emptyList<TodoList>()
            override suspend fun load(): List<TodoList> {
                return data
            }

            override suspend fun save(todos: List<TodoList>) {
                data = todos
            }
        },
        availableBackends = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun initialiseStore() {
        store = createApp(testScope, configuration) {}
    }

    @After
    fun teardown() {
        stopKoin()
    }

    protected fun createApp(
        scope: CoroutineScope = testScope,
        config: AppSettings = configuration,
        closeApp: () -> Unit = {},
    ): Store<AppState> {
        return createAppImpl(scope, config, closeApp)
    }
}
