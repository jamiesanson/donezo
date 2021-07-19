package dev.sanson.donezo.backend.git.github

import dev.sanson.donezo.backend.Backend
import dev.sanson.donezo.backend.BackendMenuItem
import dev.sanson.donezo.backend.BackendSetupFlow
import dev.sanson.donezo.backend.PresentableBackend
import dev.sanson.donezo.backend.git.internal.GitBackend

/**
 * [Backend] for syncing to do items to and from a git repo hosted on GitHub.
 */
object GitHubBackend :
    PresentableBackend,
    Backend by GitBackend(repoSshUrl = "TODO"),
    BackendMenuItem by GitHubMenuItem,
    BackendSetupFlow by GitHubSetupFlow
