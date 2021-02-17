package dev.sanson.tick

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import dev.sanson.tick.ui.theme.TickTheme
import kotlinx.coroutines.flow.StateFlow
import nz.sanson.tick.todo.AppState
import nz.sanson.tick.todo.Screen
import nz.sanson.tick.todo.feature.navigation.Navigation

@Composable
fun App(state: StateFlow<AppState>, dispatch: (Any) -> Any) {
    TickTheme {
        Surface(color = MaterialTheme.colors.background) {
            val state = state.collectAsState()

            when (state.value.screen) {
                is Screen.Splash -> SplashScreen(navigateTo = { dispatch(it) } )
                is Screen.Lists -> ListScreen()
            }
        }
    }
}

@Composable
fun SplashScreen(
    navigateTo: (Navigation) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Greeting("Android")
        Button(onClick = { navigateTo(Navigation.Todo) }) {
            Text(text = "Navigate to list screen")
        }
    }
}

@Composable
fun ListScreen() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Welcome to the list of todos!")
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TickTheme {
        Greeting("Android")
    }
}