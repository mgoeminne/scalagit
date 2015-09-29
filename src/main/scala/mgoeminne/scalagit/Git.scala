package mgoeminne.scalagit

import mgoeminne.scalagit.tag.{AnnotatedTag, LightweightTag, Tag}
import org.apache.commons.io.FileUtils
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat

import scala.sys.process.{Process, _}

case class Git(directory: java.io.File)
{
   /**
    * @return All the commits present in the repository,
    *         ordered by decreasing order of time (from the most recent, to the first one)
    */
   def commits: Stream[Commit] =
   {
      Process(Seq("git", "log", "--all", "--format=%H,%ci,%T,%ae"), directory)
         .lineStream
         .map(Git.processCommitLine(_, this))
   }

   /**
    *
    * @return All the blobs involved in the Git repository. If a blob is involved many times, it will be retrieved a single time.
    */
   def blobs: Set[Blob] = allFiles.map(_._2).toSet

   /**
    * @return All the nodes in the repositories
    */
   def tree_nodes: Stream[TreeNode] =
   {
      val p1 = Process(Seq("git", "rev-list", "--objects", "--all"), directory)
      val p2 = p1 #| Process(Seq("git", "cat-file", "--batch-check=%(objectname) %(objecttype) %(rest)"), directory)
      val p3 = p2 #| Process(Seq("grep", "^[^ ]* tree"))

      p3.lineStream.map(l => {
         TreeNode(l.split(' ')(0), this)
      })
   }

   /**
    * @return All the blobs involved in the Git repository. If a file is involved many times, it will be retrieved a single time.
    */
   def files: Set[File] = allFiles.map(_._1).toSet

   /**
    *
    * @return All the files that exist in any commit of the repository, along with their associated blob
    */
   def allFiles: Stream[(File, Blob)] =
   {
      val p1 = Process(Seq("git", "rev-list", "--objects", "--all"), directory)
      val p2 = p1 #| Process(Seq("git", "cat-file", "--batch-check=%(objectname) %(objecttype) %(rest)"), directory)
      val p3 = p2 #| Process(Seq("grep", "^[^ ]* blob"))

      p3.lineStream.map(l =>
      {
         val array = l.split(' ')
         (File(array.drop(2).mkString(" "), this), new Blob(array(0), this))
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

   /**
    * @return the names of all tags in the repository, with their associated commits
    */
   def tags: Seq[Tag] = {

      def lineValue(line: String) = line.split(' ').drop(1).mkString(" ").trim()

      Process(Seq("git", "tag"), directory).lineStream.map(name => {
         var lines = Process(Seq("git", "show", name, "-s", "--date=iso"), directory).lineStream

         if(lines.head startsWith "tag") // This is a annotated tag
         {
            lines = lines.tail
            val tagger = lineValue(lines.head)
            lines = lines.tail
            val date = Git.formatter.parseDateTime(lineValue(lines.head))
            lines = lines.tail.tail

            val comment = new StringBuilder()
            while(! (lines.head.trim isEmpty))
            {
               comment.append(lines.head.trim)
               lines = lines.tail
            }
            lines = lines.tail

            val commit = lineValue(lines.head)
            new AnnotatedTag(name, tagger, date, Commit(commit, this), this)

         }
         else
         {
            new LightweightTag(name, Commit(lineValue(lines.head), this), this)
         }
      })
   }

   override def toString(): String =
   {
      if(directory.getName == ".git")
         directory.getParentFile.getName
      else
         directory.getName.stripSuffix(".git")
   }
}

object Git
{
   val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC()

   def processCommitLine(line: String, repository: Git): Commit =
   {
      val split = line.split(',')
      val id = split(0)
      val date = LocalDateTime.parse(split(1).take(19), formatter)
      val tree = TreeNode(split(2), repository)
      val author = if (split.size >= 4) Some(split(3))
      else None

      Commit(date, id, repository, tree, author)
   }

   def clone(source: String, dest: java.io.File): Int =
   {
      FileUtils.deleteDirectory(dest)
      Seq("git", "clone", "--bare", "--quiet", source, dest.getAbsolutePath).!
   }

   def isGitRepository(file: java.io.File): Boolean = file.isDirectory && file.getAbsolutePath.endsWith((".git"))
}
