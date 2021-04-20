package dev.sanson.tick.backend.git.github

import dev.sanson.tick.backend.Backend
import dev.sanson.tick.backend.BackendMenuItem
import dev.sanson.tick.backend.BackendSetupFlow
import dev.sanson.tick.backend.PresentableBackend
import dev.sanson.tick.backend.git.internal.GitBackend

/**
 * [Backend] for syncing to do items to and from a git repo hosted on GitHub.
 */
object GitHubBackend :
    PresentableBackend,
    Backend by GitBackend(repoSshUrl = "TODO"),
    BackendMenuItem by GitHubMenuItem,
    BackendSetupFlow by GitHubSetupFlow
