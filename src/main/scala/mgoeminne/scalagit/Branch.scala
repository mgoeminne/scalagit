package mgoeminne.scalagit

import org.joda.time.format.DateTimeFormat

import scala.sys.process.Process

case class Branch(name: String, repository: Git)
{
   private val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss Z")

   /**
    * @return all commits in a branch
    */
   def commits: Set[Commit] =
   {
      Process(Seq("git", "log", name, "--format=%H,%ci,%T,%ae"), repository.directory)
         .lineStream
         .map(Git.processCommitLine(_, repository))
         .toSet
   }
}

