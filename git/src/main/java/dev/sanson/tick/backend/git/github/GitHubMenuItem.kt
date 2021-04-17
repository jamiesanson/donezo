package dev.sanson.tick.backend.git.github

import dev.sanson.tick.backend.BackendMenuItem

object GitHubMenuItem: BackendMenuItem {
    override val iconUri: String
        get() = TODO("Not yet implemented")
    override val title: String
        get() = "GitHub"
    override val description: String
        get() = "Sync your to do items with a git repository on GitHub"
}