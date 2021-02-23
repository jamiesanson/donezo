package me.saket.kgit

import org.eclipse.jgit.revwalk.RevCommit
import java.time.Instant
import java.time.ZoneId

actual class GitCommit(internal val commit: RevCommit) {
  actual val sha1: GitSha1
    get() = GitSha1(commit.id)

  actual val message: String
    get() = commit.fullMessage

  actual val utcTimestamp: UtcTimestamp
    get() = UtcTimestamp(
        Instant.ofEpochMilli(commit.authorIdent.`when`.time)
            .atZone(ZoneId.of(commit.authorIdent.timeZone.id))
            .toInstant()
            .toEpochMilli()
    )

  override fun toString(): String {
    return "${sha1.abbreviated} - $shortMessage"
  }

  override fun equals(other: Any?): Boolean {
    return sha1 == (other as? GitCommit)?.sha1
  }

  override fun hashCode(): Int {
    return sha1.hashCode()
  }
}
