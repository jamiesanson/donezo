package dev.sanson.tick.todo.store

import dev.sanson.tick.todo.Action
import dev.sanson.tick.todo.AppState
import dev.sanson.tick.todo.feature.list.database.DatabaseTodo
import dev.sanson.tick.todo.feature.navigation.Screen
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TodoDbTests : ReduxAppTest() {

    private val stateFlow = flow {
        store.subscribe {
            testScope.launch { emit(store.state) }
        }
    }.stateIn(
        testScope,
        started = SharingStarted.Eagerly,
        initialValue = AppState()
    )

    @Test
    fun `database is seeded correctly`() {
        runBlocking {
            testScope.launch {
                delay(100)

                stateFlow.value.navigation.currentScreen::class shouldBe Screen.Lists::class

                stateFlow.value.lists.size shouldNotBe 0
            }
        }
    }

    @Test
    fun `adding todo item is populated with id`() {
        runBlocking {
            testScope.launch {
                delay(100)

                // Add todo
                store.dispatch(Action.AddTodo(list = stateFlow.value.lists.first()))

                delay(100)

                val firstItem = stateFlow.value.lists.first().items.first()
                firstItem::class shouldBe DatabaseTodo::class
            }
        }
    }
}
