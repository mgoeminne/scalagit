package mgoeminne.scalagit.tag

import mgoeminne.scalagit.{Git, Commit}

/**
 * A simple, and not recommanded tag,
 * that simply refers to a commit.
 */
class LightweightTag(name: String, commit: Commit, repository: Git) extends Tag(name, commit, repository)
