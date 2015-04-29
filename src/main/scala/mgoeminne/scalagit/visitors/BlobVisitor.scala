package mgoeminne.scalagit.visitors

import mgoeminne.scalagit.{Commit, Git, Blob}

/**
 * A visitor based on blobs
 */
abstract class BlobVisitor[T, V] extends Visitor[Blob, T, V]
{
  override def generator(repo: Git) = repo.blobs
  override def associator(commit: Commit) = commit.blobs.toSeq
}
