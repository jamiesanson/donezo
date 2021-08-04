package dev.sanson.donezo.android

import android.content.Context
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import dev.sanson.donezo.model.TodoList
import dev.sanson.donezo.todo.storage.LocalStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

/**
 * LocalStorage implementation making use of [DataStore] to
 * persist the lists to disk.
 *
 * @param context Context to use when creating/accessing the DataStore singleton
 */
@Suppress("BlockingMethodInNonBlockingContext")
class AndroidLocalStorage(
    private val context: Context,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : LocalStorage {

    private val Context.dataStore by dataStore("todo-lists", TodoListSerializer(ioDispatcher))

    override suspend fun load(): List<TodoList> = with(context) {
        dataStore.data.first()
    }

    override suspend fun save(todos: List<TodoList>): Unit = with(context) {
        dataStore.updateData { todos }
    }

    private class TodoListSerializer(
        private val ioDispatcher: CoroutineDispatcher,
    ) : Serializer<List<TodoList>> {
        override val defaultValue: List<TodoList> = emptyList()

        override suspend fun readFrom(input: InputStream): List<TodoList> {
            val jsonList = withContext(ioDispatcher) { input.bufferedReader().readText() }

            return Json.decodeFromString(jsonList)
        }

        override suspend fun writeTo(t: List<TodoList>, output: OutputStream) {
            val jsonList = Json.encodeToString(t)

            withContext(ioDispatcher) {
                output.bufferedWriter().use {
                    it.write(jsonList)
                }
            }
        }
    }
}
