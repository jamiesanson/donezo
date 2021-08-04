package dev.sanson.donezo.android

import android.content.Context
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import dev.sanson.donezo.model.TodoList
import dev.sanson.donezo.todo.storage.LocalStorage
import kotlinx.coroutines.flow.first
import java.io.InputStream
import java.io.OutputStream

// TODO: Inject in createApp
class AndroidLocalStorage(
    private val context: Context,
) : LocalStorage {

    private val Context.dataStore by dataStore("todo-lists", TodoListSerializer)

    override suspend fun load(): List<TodoList> = with(context) {
        dataStore.data.first()
    }

    override suspend fun save(todos: List<TodoList>): Unit = with(context) {
        dataStore.updateData { todos }
    }


    private object TodoListSerializer : Serializer<List<TodoList>> {
        override val defaultValue: List<TodoList> = emptyList()

        override suspend fun readFrom(input: InputStream): List<TodoList> {
            TODO("Deserialise from inputstream")
        }

        override suspend fun writeTo(t: List<TodoList>, output: OutputStream) {
            TODO("Serialise to outputstream")
        }

    }
}