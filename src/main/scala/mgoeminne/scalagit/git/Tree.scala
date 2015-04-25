package mgoeminne.scalagit.git

import scala.sys.process.Process


case class Tree(id: String, repository: Git)
{
  override def hashCode = id.hashCode

  override def equals(b: Any): Boolean =
  {
    b match
    {
      case other: Tree => this.id == other.id
      case _ => false
    }
  }

  /**
   * @return All the blobs involved in this tree. If a blob is present many times, it will be retrieved each of these times.
   */
  def blobs: Seq[Blob] =
  {
    Process(Seq("git", "ls-tree", "-r", id), repository.directory).lines.map(line => Blob(line.split("\\s")(2), repository))
  }
}
