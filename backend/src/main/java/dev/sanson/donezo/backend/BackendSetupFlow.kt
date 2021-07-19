package dev.sanson.donezo.backend

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable

interface BackendSetupFlow {

    @SuppressLint("ComposableNaming")
    @Composable
    operator fun invoke()
}
