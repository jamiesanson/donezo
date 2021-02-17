package nz.sanson.tick.todo

import arrow.optics.optics
import nz.sanson.tick.todo.model.TodoList

@optics
data class AppState(
    val screen: Screen = Screen.Splash
) { companion object }

/**
 * A screen is represented as a portion of the app state, given that the app
 * is an incredibly simple one. This pattern isn't hugely extensible, but works nicely for what
 * Tick is.
 */
sealed class Screen {

    object Splash: Screen()

    @optics
    data class Lists(
        val loading: Boolean = true,
        val lists: List<TodoList> = emptyList(),
    ): Screen() { companion object }
}