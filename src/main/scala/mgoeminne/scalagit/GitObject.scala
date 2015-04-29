package mgoeminne.scalagit

import scala.sys.process.Process

/**
 * An object may be a blob, a treenode, etc.
 */
case class GitObject(id: String, repository: Git)
{
   override def hashCode = id.hashCode

   override def equals(b: Any): Boolean =
   {
      b match
      {
         case other: GitObject => this.id.equals(other.id)
         case _ => false
      }
   }

   /**
    * Determines the size, in bytes, that it takes up on disk.
    *
    * Note that the sizes of objects on disk are reported accurately, but care should be taken in drawing conclusions
    * about which refs or objects are responsible for disk usage.
    * The size of a packed non-delta object may be much larger than the size of objects which delta against it,
    * but the choice of which object is the base and which is the delta
    * is arbitrary and is subject to change during a repack.
    *
    * Note also that multiple copies of an object may be present in the object database;
    * in this case, it is undefined which copy’s size or delta base will be reported.
    *
    * @return The size, in bytes, that the object takes up on disk.
    */
   def size: Long = Process(Seq("git", "cat-file", "-s", id), repository.directory).lineStream.head.toLong
}
