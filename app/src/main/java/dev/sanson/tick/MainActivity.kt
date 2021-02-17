package dev.sanson.tick

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import nz.sanson.tick.todo.AppState
import nz.sanson.tick.todo.createApp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: TickViewModel = viewModel()

            // Abstract the ViewModel implementation away from the [App] such that
            // it's decoupled from Android, which might allow for a desktop compose app in the future.
            App(
                state = viewModel.state,
                dispatch = viewModel.store.dispatch
            )
        }
    }
}

class TickViewModel: ViewModel() {
    /**
     * The store holding the whole app's state
     */
    val store = createApp()

    /**
     * The current state held in the [store]. Exposed as [LiveData].
     */
    private val stateFlow = MutableStateFlow(value = store.state)
    val state: StateFlow<AppState> = stateFlow

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
