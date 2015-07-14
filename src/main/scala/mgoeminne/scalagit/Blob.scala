package mgoeminne.scalagit

import scala.sys.process.Process

class Blob(id: String, repository: Git) extends GitObject(id, repository)
{
  /**
   * @return A list of all files that have been associated to the blob.
   */
  def files: Stream[String] =
  {
    val p1 = Process(Seq("git", "rev-list", "--objects", "--all"), repository.directory)
    val p2 = p1 #| Process(Seq("git", "cat-file", "--batch-check='%(objectname) %(objecttype) %(rest)'"), repository.directory)
    val p3 = p2 #| Process(Seq("grep", id))

    p3.lineStream.map(_.replaceAll("'", "").split(' ')(2))
  }

  /**
   * @return all the commits in which this blob can be found
   */
  def commits: Set[Commit] = repository.commits.filter(_.blobs.contains(this)).toSet

  override def toString = "Blob(" + id + ")"

  def lines: Stream[String] = Process(Seq("git", "cat-file", "blob", id), repository.directory).lineStream
}
