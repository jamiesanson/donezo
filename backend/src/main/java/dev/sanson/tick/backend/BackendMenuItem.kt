package dev.sanson.tick.backend

interface BackendMenuItem {
    // TODO: This should be a composable
    val iconUri: String
    val title: String
    val description: String
}