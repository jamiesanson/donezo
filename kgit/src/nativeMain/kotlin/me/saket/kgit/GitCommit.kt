package me.saket.kgit

actual class GitCommit {
  actual val sha1: GitSha1 get() = TODO()
  actual val message: String get() = TODO()
  actual val utcTimestamp: UtcTimestamp get() = TODO()
}
