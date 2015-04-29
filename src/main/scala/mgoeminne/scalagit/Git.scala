package mgoeminne.scalagit

import java.io.File

import org.apache.commons.io.FileUtils
import org.joda.time.format.DateTimeFormat

import scala.sys.process.{Process, _}

case class Git(directory: File)
{
   def commits: Set[Commit] =
   {
      Process(Seq("git", "log", "--all", "--format=%H,%ci,%T,%ae"), directory)
         .lineStream
         .map(Git.processCommitLine(_, this))
         .toSet
   }

   /**
    *
    * @return All the blobs involved in the Git repository. If a blob is involved many times, it will be retrieved a single time.
    */
   def blobs: Set[Blob] =
   {
      allFiles.map(_._2).toSet
   }

   /**
    * For a given file, find all the blobs that represent this file.
    * @param file The file to found. Ex: foo/bar/file.txt
    * @return The blobs that represent the versions of the given file.
    */
   def findBlobs(file: String): Seq[Blob] = allFiles.filter(element => element._1 == file).map(_._2)

   /**
    *
    * @return All the files that exist in any commit of the repository, along with their associated blob
    */
   def allFiles: Stream[(String, Blob)] =
   {
      val p1 = Process(Seq("git", "rev-list", "--objects", "--all"), directory)
      val p2 = p1 #| Process(Seq("git", "cat-file", "--batch-check=%(objectname) %(objecttype) %(rest)"), directory)
      val p3 = p2 #| Process(Seq("grep", "^[^ ]* blob"))

      p3.lineStream.map(l =>
      {
         val array = l.split(' ')
         (array.drop(2).mkString(" "), new Blob(array(0), this))
      })
   }

   /**
    * @return all known remote branches of the repository.
    */
   def remoteBranches: Seq[String] = Process(Seq("git", "branch", "-r")).lineStream.map(_.trim)

   /**
    * @return all local branches
    */
   def localBranches: Seq[Branch] = Process(Seq("git", "branch")).lineStream.map(line => Branch(line replace('*',' ') trim , this))
}

object Git
{
   private val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss Z")

   def processCommitLine(line: String, repository: Git): Commit =
   {
      val split = line.split(',')
      val id = split(0)
      val date = formatter.parseDateTime(split(1))
      val tree = TreeNode(split(2), repository)
      val author = if (split.size >= 4) Some(split(3))
      else None

      Commit(date, id, repository, tree, author)
   }

   def clone(source: String, dest: File): Int =
   {
      FileUtils.deleteDirectory(dest)
      Seq("git", "clone", "--bare", "--quiet", source, dest.getAbsolutePath).!
   }

   def isGitRepository(file: File): Boolean = file.isDirectory && file.getAbsolutePath.endsWith((".git"))
}
