package mgoeminne.scalagit.git

import org.joda.time.format.DateTimeFormat

import scala.sys.process.Process

case class Branch(name: String, repository: Git)
{
   private val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss Z")

   /**
    * @return all commits in a branch
    */
   def commits: Seq[Commit] =
   {
      Process(Seq("git", "log", name, "--format=%H,%ci,%T,%ae"), repository.directory).lineStream.map(line =>
      {
         val split = line.split(',')
         val id = split(0)
         val date = formatter.parseDateTime(split(1))
         val tree = Tree(split(2), repository)
         val author = if (split.size >= 4)   Some(split(3))
                      else                   None

         Commit(date, id, repository, tree, author)
      })
   }
}

