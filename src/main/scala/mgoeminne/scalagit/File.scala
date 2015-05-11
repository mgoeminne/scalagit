package mgoeminne.scalagit

case class File(name: String, repository: Git)
{
   /**
    * Find all the blobs that represent this file.
    * @return The blobs that represent the versions of the given file.
    */
   def blobs: Set[Blob] = repository.allFiles filter (af => af._1 == this) map(_._2) toSet
}

