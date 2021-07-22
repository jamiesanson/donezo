package dev.sanson.donezo.todo.store

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import dev.sanson.donezo.db.Database
import dev.sanson.donezo.todo.AppSettings
import dev.sanson.donezo.todo.AppState
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

    private val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
        Database.Schema.create(it)
    }

    private val configuration = AppSettings(
        databaseDriver = driver,
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
        closeApp: () -> Unit = {}
    ): Store<AppState> {
        return createAppImpl(scope, config, closeApp)
    }
}