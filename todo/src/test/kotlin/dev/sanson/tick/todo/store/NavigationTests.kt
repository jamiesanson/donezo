package dev.sanson.tick.todo.store

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import dev.sanson.tick.todo.Action
import dev.sanson.tick.todo.AppState
import dev.sanson.tick.todo.feature.navigation.Navigation
import dev.sanson.tick.todo.feature.navigation.NavigationReducer
import dev.sanson.tick.todo.feature.navigation.Screen
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(TestParameterInjector::class)
class NavigationTests : ReduxAppTest() {

    //region parameterised tests
    data class AllowedTransition(val from: Screen, val to: Screen, val on: Action.Navigation)

    private val allowedTransitions = listOf(
        AllowedTransition(Screen.Lists(), Screen.SyncSettings, Action.Navigation.To(Screen.SyncSettings)),
    )

    private class ScreenProvider : TestParameter.TestParameterValuesProvider {
        override fun provideValues(): MutableList<*> {
            return mutableListOf(Screen.Lists(), Screen.SyncSettings)
        }
    }

    private class ActionProvider : TestParameter.TestParameterValuesProvider {
        override fun provideValues(): MutableList<*> {
            return mutableListOf(Action.Navigation.To(Screen.SyncSettings))
        }
    }

    @Test
    fun `validate navigation combinations`(
        @TestParameter(valuesProvider = ScreenProvider::class) initialScreen: Screen,
        @TestParameter(valuesProvider = ActionProvider::class) navigationAction: Action.Navigation
    ) {

        val state = AppState(navigation = Navigation(currentScreen = initialScreen))

        val newScreen = NavigationReducer(state, navigationAction).navigation.currentScreen

        if (newScreen != initialScreen) {
            val transition = AllowedTransition(from = initialScreen, to = newScreen, on = navigationAction)

            val matching = allowedTransitions.find { it == transition }

            transition shouldBe matching
        }
    }

    //endregion

    @Test
    fun `list screen is initial destination`() {
        store.state.navigation.currentScreen shouldBe Screen.Lists()
    }

    @Test
    fun `back invoked when going back from list screen`() {
        stopKoin()

        var closeAppCalled = false
        val store = createApp { closeAppCalled = true }

        // Backstack should be empty
        store.state.navigation.backstack shouldBe emptyList()
        // Current screen should be lists
        store.state.navigation.currentScreen should { it as Screen.Lists }

        // Navigate back
        store.dispatch(Action.Navigation.Back)

        closeAppCalled shouldBe true
    }
}
