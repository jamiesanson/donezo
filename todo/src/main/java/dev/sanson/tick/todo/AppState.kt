package dev.sanson.tick.todo

import dev.sanson.tick.backend.Backend
import dev.sanson.tick.backend.PresentableBackend
import dev.sanson.tick.model.TodoList

data class AppState(
    val backends: List<Backend>,
    val backstack: List<Screen> = emptyList(),
    val currentScreen: Screen = Screen.Splash
)

/**
 * A screen is represented as a portion of the app state, given that the app
 * is an incredibly simple one. This pattern isn't hugely extensible, but works nicely for what
 * Tick is.
 */
sealed class Screen {

    object Splash : Screen()

    data class Lists(
        val loading: Boolean = true,
        val lists: List<TodoList> = emptyList(),
    ) : Screen()

    data class SyncSettings(
        val backends: List<PresentableBackend>
    ) : Screen() {
        companion object {
            fun fromBackends(backends: List<Backend>) = SyncSettings(backends = backends.mapNotNull { it as? PresentableBackend })
        }
    }
}
