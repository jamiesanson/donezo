package dev.sanson.tick

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import nz.sanson.tick.todo.createApp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
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
    val state = liveData {
        for (event in eventChannel) {
            emit(store.state)
        }
    }

    /**
     * An event channel for triggering state emissions
     */
    private val eventChannel = Channel<Unit>()

    init {
        // This isn't ideal, but it's a way to escape the lack of crossinline on the subscribe callback.
        // There's a very slim chance this could race and suspend forever.
        // We'll cross that bridge when we come to it :shrug:
        store.subscribe {
            viewModelScope.launch {
                eventChannel.send(Unit)
            }
        }
    }
}
