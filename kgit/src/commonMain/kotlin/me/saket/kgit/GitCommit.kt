package me.saket.kgit

expect class GitCommit {
  val sha1: GitSha1
  val message: String
  val utcTimestamp: UtcTimestamp
}

val GitCommit.shortMessage
  get() = message.lineSequence().first()
