package mgoeminne.scalagit

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.sys.process.Process

case class Commit(date: DateTime, id: String, repository: Git, tree: TreeNode, author: Option[String]) extends Ordered[Commit]
{

  /**
   * @return All files that are 'living' in this commit, aside with their associated blobs
   */
  def existingFile(): Seq[(String, Blob)] =
  {
    Process(Seq("git", "ls-tree", "-r", tree.id), repository.directory).lineStream.map(line => {
      val split = line.split("\\s")
      (split(3) , new Blob(split(2), repository))
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

object Commit
{
   def apply(id: String, repository: Git): Commit =
   {
      val data = Process(Seq("git", "show", id, "-s", "--format=%ci,%T,%ae"), repository.directory).lineStream.head.split(',')
      val date = Git.formatter.parseDateTime(data(0))
      val tree = new TreeNode(data(1), repository)
      val author = if(data.length>2) Some(data(2).trim) else None

      Commit(date, id, repository, tree, author)
   }
}
