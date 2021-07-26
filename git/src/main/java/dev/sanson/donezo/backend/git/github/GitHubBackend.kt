package dev.sanson.donezo.backend.git.github

import dev.sanson.donezo.backend.Backend
import dev.sanson.donezo.backend.git.internal.GitBackend

/**
 * [Backend] for syncing to do items to and from a git repo hosted on GitHub.
 */
object GitHubBackend : Backend by GitBackend(repoSshUrl = "TODO")
