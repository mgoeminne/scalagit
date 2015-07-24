package mgoeminne.scalagit.visitors

import mgoeminne.scalagit.{Commit, Git, Blob}

/**
 * A visitor based on blobs
 */
abstract class BlobVisitor[T, V] extends Visitor[Blob, T, V]
{
  override def elements(repo: Git) = repo.blobs.toSeq
  override def associator(commit: Commit) = commit.blobs.toSeq
}
