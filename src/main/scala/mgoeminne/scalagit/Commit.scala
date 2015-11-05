package mgoeminne.scalagit

import org.joda.time.{LocalDateTime, DateTime}
import org.joda.time.format.DateTimeFormat

import scala.sys.process.Process

case class Commit(date: LocalDateTime,
                  id: String,
                  repository: Git,
                  tree: TreeNode,
                  author: Option[String],
                  committer: Option[String] = None) extends Ordered[Commit]
{

   /**
    * @return All files that are 'living' in this commit, aside with their associated blobs
    */
   def existingFile(): Stream[(File, Blob)] =
   {
      Process(Seq("git", "ls-tree", "-r", tree.id), repository.directory).lineStream.map(line => {
         val split = line.split("\\s")
         (File(split(3), repository) , new Blob(split(2), repository))
      })
   }

   /**
    * @return the blobs involved in this commit
    */
   def blobs: Set[Blob] = existingFile.map(_._2).toSet

   /**
    * @return The commit parents.
    */
   def parents: Seq[Commit] = {
      val ids = Process(Seq("git", "log", "--pretty=%P", "-n", "1", id), repository.directory).lineStream
      ids.filterNot(_.isEmpty)
         .map(i => Commit.apply(i, repository))
   }

   /**
    * @return the files living in this commit
    */
   def files: Stream[File] = existingFile.map(_._1).distinct

   override def hashCode = id.hashCode

   override def equals(b: Any): Boolean =
   {
      b match
      {
         case other: Commit => this.id == other.id
         case _ => false
      }
   }

   /**
    * @return the diff associated to the commit
    */
   def diff: Stream[String] = Process(Seq("git", "log", "-p", id), repository.directory).lineStream

   override def compare(that: Commit): Int = this.date.compareTo(that.date)

   override def toString = "Commit(" + id + ")"
}

object Commit
{
   def apply(id: String, repository: Git): Commit =
   {
      val data = Process(Seq("git", "show", id, "-s", "--format=%ci,%T,%ae,%ce"), repository.directory).lineStream.head.split(',')
      val date = LocalDateTime.parse(data(0).substring(0,19), Git.formatter)
      val tree = new TreeNode(data(1), repository)
      val author = if(data.length>2) Some(data(2).trim) else None
      val committer = if(data.length>3) Some(data(3).trim) else None

      Commit(date, id, repository, tree, author, committer)
   }
}
