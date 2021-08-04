package dev.sanson.donezo.todo.store

import dev.sanson.donezo.todo.AppState
import dev.sanson.donezo.todo.feature.navigation.Screen
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
}
