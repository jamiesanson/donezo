package dev.sanson.donezo.android

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.core.view.WindowCompat
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.sanson.donezo.App
import dev.sanson.donezo.backend.git.github.GitHubBackend
import dev.sanson.donezo.todo.Action
import dev.sanson.donezo.todo.AppSettings
import dev.sanson.donezo.todo.AppState
import dev.sanson.donezo.todo.createApp
import dev.sanson.donezo.todo.destroyApp
import dev.sanson.donezo.todo.storage.LocalStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Composition local providing convenient access to the store dispatch function
 */
val LocalDispatch = compositionLocalOf<(Any) -> Any> { error("No default dispatch") }

/**
 * Utility function for referencing the datastore singleton
 */
private val Context.dataStore by dataStore("todo-lists", TodoListSerializer())

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val viewModel: DonezoViewModel = viewModel()

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

class DonezoViewModel : ViewModel() {

    /**
     * Backends available to the Android application
     */
    private val availableBackends = listOf(
        GitHubBackend
    )

    /**
     * Local storage instance for the Android platform
     */
    private val localStorage: LocalStorage = AndroidLocalStorage(
        dataStore = DonezoApplication.context.dataStore
    )

    /**
     * The store holding the whole app's state. We scope all internal
     * coroutines calls to this ViewModel, which represents the lifecycle of the entire application.
     */
    val store = createApp(
        applicationScope = viewModelScope,
        appSettings = AppSettings(
            localStorage = localStorage,
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
            stateFlow.compareAndSet(stateFlow.value, store.state)
        }
    }

    override fun onCleared() {
        destroyApp()
    }
}
