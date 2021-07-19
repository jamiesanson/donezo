package dev.sanson.donezo.backend

import androidx.compose.runtime.Composable

interface BackendMenuItem {
    @Composable
    fun Icon()

    val title: String
    val description: String
}
