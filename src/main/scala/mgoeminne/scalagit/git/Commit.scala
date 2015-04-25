package mgoeminne.scalagit.git

import org.joda.time.DateTime

import scala.sys.process.Process

case class Commit(date: DateTime, id: String, repository: Git, tree: Tree, author: Option[String]) extends Ordered[Commit]
{
  /**
   * @return All files that are 'living' in this commit, aside with their associated blobs
   */
  def existingFile(): Seq[(String, Blob)] =
  {
    Process(Seq("git", "ls-tree", "-r", tree.id), repository.directory).lineStream.map(line => {
      val split = line.split("\\s")
      (split(3) , Blob(split(2), repository))
    }).toSeq
  }

  /**
   * @return the blobs involved in this commit
   */
  def blobs: Set[Blob] = existingFile().map(_._2).toSet

  /**
   * @return the files living in this commit
   */
  def files: Set[String] = existingFile().map(_._1).toSet

  override def hashCode = id.hashCode

  override def equals(b: Any): Boolean =
  {
    b match
    {
      case other: Commit => this.id == other.id
      case _ => false
    }
  }

  override def compare(that: Commit): Int = this.date.compareTo(that.date)

  override def toString = "Commit(" + id + ")"
}
