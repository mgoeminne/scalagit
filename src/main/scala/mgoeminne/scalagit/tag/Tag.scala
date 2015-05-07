package mgoeminne.scalagit.tag

import mgoeminne.scalagit.{Git, Commit}

/**
 * Represents a Git tag.
 */
class Tag(val name: String, val commit: Commit, val repository: Git)
{
   override def toString(): String = name
}
