package dev.sanson.donezo.android

import androidx.datastore.core.Serializer
import dev.sanson.donezo.model.TodoList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

class TodoListSerializer(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
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
                @Suppress("BlockingMethodInNonBlockingContext")
                it.write(jsonList)
            }
        }
    }
}