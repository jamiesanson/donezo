package dev.sanson.tick.android

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dev.sanson.tick.App
import dev.sanson.tick.backend.git.github.GitHubBackend
import dev.sanson.tick.db.Database
import dev.sanson.tick.todo.Action
import dev.sanson.tick.todo.AppState
import dev.sanson.tick.todo.AppSettings
import dev.sanson.tick.todo.createApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Composition local providing convenient access to the store dispatch function
 */
val LocalDispatch = compositionLocalOf<(Any) -> Any> { error("No default dispatch") }

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: TickViewModel = viewModel()

            val finishActivity = viewModel.finishActivityTrigger.collectAsState(initial = false)
            if (finishActivity.value) {
                finish()
            }

            DisposableEffect(onBackPressedDispatcher) {
                val callback = object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        viewModel.store.dispatch(Action.Navigation.Back)
                    }
                }

                onBackPressedDispatcher.addCallback(callback)

                onDispose {
                    callback.remove()
                }
            }

            CompositionLocalProvider(LocalDispatch provides viewModel.store.dispatch) {
                // Abstract the ViewModel implementation away from the [App] such that
                // it's decoupled from Android, which might allow for a desktop compose app in the future.
                val state = viewModel.state.collectAsState()
                App(state = state.value)
            }
        }
    }
}

class TickViewModel : ViewModel() {

    /**
     * Backends available to the Android application
     */
    private val availableBackends = listOf(
        GitHubBackend
    )

    /**
     * Database driver for the Android platform
     */
    private val databaseDriver
        get() = AndroidSqliteDriver(Database.Schema, TickApplication.context, "todo.db")

    /**
     * The store holding the whole app's state. We scope all internal
     * coroutines calls to this ViewModel, which represents the lifecycle of the entire application.
     */
    val store = createApp(
        applicationScope = viewModelScope,
        appSettings = AppSettings(
            databaseDriver = databaseDriver,
            availableBackends = availableBackends
        ),
        closeApp = {
            (finishActivityTrigger as MutableStateFlow).compareAndSet(
                expect = false,
                update = true
            )
        }
    )

    /**
     * The current state held in the [store]
     */
    private val stateFlow = MutableStateFlow(value = store.state)
    val state: StateFlow<AppState> = stateFlow

    /**
     * A trigger to finish the activity when the todo app tells us to. This could be better, as
     * if the call to finish doesn't immediately dispose the composition we could call finish more
     * than once if it recomposes, but it works for now.
     */
    val finishActivityTrigger: Flow<Boolean> = MutableStateFlow(false)

    init {
        // This isn't ideal, but it's a way to escape the lack of crossinline on the subscribe callback.
        // There's a very slim chance this could race and suspend forever.
        // We'll cross that bridge when we come to it :shrug:
        store.subscribe {
            viewModelScope.launch {
                stateFlow.compareAndSet(stateFlow.value, store.state)
            }
        }
    }
}
