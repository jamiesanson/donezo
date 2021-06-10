package dev.sanson.tick.backend

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable

interface BackendSetupFlow {

    @SuppressLint("ComposableNaming")
    @Composable
    operator fun invoke()
}
