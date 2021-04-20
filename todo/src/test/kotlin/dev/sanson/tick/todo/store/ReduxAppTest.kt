package dev.sanson.tick.todo.store

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import dev.sanson.tick.db.Database
import dev.sanson.tick.todo.AppState
import dev.sanson.tick.todo.Configuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.After
import org.junit.Before
import org.koin.core.context.stopKoin
import org.reduxkotlin.Store
import dev.sanson.tick.todo.createApp as createAppImpl

abstract class ReduxAppTest {

    lateinit var store: Store<AppState>

    private val coroutineScope = TestCoroutineScope()

    private val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
        Database.Schema.create(it)
    }

    private val configuration = Configuration(
        databaseDriver = driver,
        availableBackends = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun initialiseStore() {
        store = createApp(coroutineScope, configuration) {}
    }

    @After
    fun teardown() {
        stopKoin()
    }

    protected fun createApp(
        scope: CoroutineScope = coroutineScope,
        config: Configuration = configuration,
        closeApp: () -> Unit = {}
    ): Store<AppState> {
        return createAppImpl(scope, config, closeApp)
    }
}
