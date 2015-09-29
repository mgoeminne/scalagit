package mgoeminne.scalagit

case class File(name: String, repository: Git)
{
   /**
    * Find all the blobs that represent this file.
    * @return The blobs that represent the versions of the given file.
    */
   def blobs: Set[Blob] = repository.allFiles filter (af => af._1 == this) map(_._2) toSet

   /**
    * Find all commits that contain this file, as well as the blob associated to the file for these commits.
    * With the current implementation, this call is in O(n), where n is the number of commits in the
    * repository.
    * @return for each commit in which the file exists, a tuple (_commit_, _blob_), where _commit_ is the commit
    *         containing the file, and _blob_ is the blob associated to this file for the considered commit.
    */
   def commits: Stream[(Commit,Blob)] = {
      repository.commits.map(commit => {
         (commit, commit.existingFile().filter(_._1 equals this))
      }).filterNot(entry => entry._2.isEmpty)
         .map(entry => (entry._1, entry._2.head._2))
   }
}

