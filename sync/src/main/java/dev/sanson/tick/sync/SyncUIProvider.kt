package dev.sanson.tick.sync

interface SyncUIProvider {

    val title: String
    val description: String

    // TODO: Make @Composable
    fun MenuItem()

    // TODO: Make @Composable
    fun Setup(syncer: Syncer)

    val hasDisableUi: Boolean

    // TODO: Make @Composable
    fun Disable(syncer: Syncer)
}