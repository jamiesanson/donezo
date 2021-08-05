package dev.sanson.donezo.android

import androidx.datastore.core.DataStore
import dev.sanson.donezo.model.TodoList
import dev.sanson.donezo.todo.storage.LocalStorage
import kotlinx.coroutines.flow.first

/**
 * LocalStorage implementation making use of [DataStore] to
 * persist the lists to disk.
 *
 * @param dataStore Instance of datastore used for persisting list
 */
@Suppress("BlockingMethodInNonBlockingContext")
class AndroidLocalStorage(
    private val dataStore: DataStore<List<TodoList>>
) : LocalStorage {

    override suspend fun load(): List<TodoList> = dataStore.data.first()

    override suspend fun save(todos: List<TodoList>) {
        dataStore.updateData { todos }
    }
}
