package dev.sanson.donezo.backend.git.internal

import dev.sanson.donezo.backend.Backend
import dev.sanson.donezo.backend.BackendDataSource
import dev.sanson.donezo.backend.BackendMenuItem
import dev.sanson.donezo.backend.BackendSetupFlow
import dev.sanson.donezo.backend.git.github.GitHubMenuItem
import dev.sanson.donezo.backend.git.github.GitHubSetupFlow

class GitBackend(
    private val repoSshUrl: String
) : Backend {

    override val ui: Backend.UI = object : Backend.UI {
        override val setupFlow: BackendSetupFlow = GitHubSetupFlow
        override val backendMenuItem: BackendMenuItem = GitHubMenuItem
    }

    override val dataSource: BackendDataSource
        get() = TODO("Not yet implemented")
}
