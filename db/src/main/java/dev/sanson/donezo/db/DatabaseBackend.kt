package dev.sanson.donezo.db

import com.squareup.sqldelight.db.SqlDriver
import dev.sanson.donezo.backend.Backend
import dev.sanson.donezo.model.TodoList
import kotlinx.coroutines.flow.StateFlow
import kotlin.collections.List

class DatabaseBackend(
    private val driver: SqlDriver
): Backend {

    private val database by lazy { Database(driver) }

    init {

    }

    override val status: StateFlow<Backend.Status>
        get() = TODO("Not yet implemented")

    override fun update(items: List<TodoList>) {
        TODO("Not yet implemented")
    }

    override fun syncNow() {
        // Nothing to do here m8
    }
}