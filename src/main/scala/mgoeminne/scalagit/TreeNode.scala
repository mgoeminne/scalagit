package mgoeminne.scalagit

import scala.sys.process.Process


case class TreeNode(id: String, repository: Git)
{
  override def hashCode = id.hashCode

  override def equals(b: Any): Boolean =
  {
    b match
    {
      case other: TreeNode => this.id == other.id
      case _ => false
    }
  }

   /**
    *
    * @return All the children directly included in the node.
    */
   def children: Seq[(String, NodeMode, Either[Blob,TreeNode])] =
   {
       Process(Seq("git", "ls-tree", id), repository.directory)
          .lineStream
          .map(line => {
            val splitted = line.split("\\s")
            val access = new NodeMode(splitted(0))
            val name = splitted(3)
            val content: Either[Blob, TreeNode] = splitted(1) match {
               case "blob" => Left(Blob(splitted(2), repository))
               case "tree" => Right(TreeNode(splitted(2), repository))
            }

            (name, access, content)
         })
   }

  /**
   * @return All the blobs involved in this tree, recursively. If a blob is present many times, it will be retrieved each of these times.
   */
  def blobs: Seq[Blob] =
  {
    Process(Seq("git", "ls-tree", "-r", id), repository.directory)
       .lineStream
       .map(line => Blob(line.split("\\s")(2), repository))
  }
}
