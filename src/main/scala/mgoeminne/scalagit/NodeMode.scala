package mgoeminne.scalagit

/**
 * Represents the mode of a node. A node being a file, in the Unix sense of the term.
 */
class NodeMode(rights: String)
{
   val permissions = Integer.parseInt(rights, 8)

   private val userPrivilege =  ( Integer.parseInt("00400", 8) , Integer.parseInt("00200", 8) , Integer.parseInt("00100", 8) )
   private val groupPrivilege = ( Integer.parseInt("00040", 8) , Integer.parseInt("00020", 8) , Integer.parseInt("00010", 8) )
   private val otherPrivilege = ( Integer.parseInt("00004", 8) , Integer.parseInt("00002", 8) , Integer.parseInt("00001", 8) )
   private val SET_UID = Integer.parseInt("0004000", 8)
   private val SET_GROUP_ID = Integer.parseInt("0004000", 8)
   private val STICKY = Integer.parseInt("0001000", 8)
   private val FIFO = Integer.parseInt("0010000", 8)
   private val CHARACTER_DEVICE = Integer.parseInt("0020000", 8)
   private val DIRECTORY = Integer.parseInt("0040000", 8)
   private val BLOCK_DEVICE = Integer.parseInt("0060000", 8)
   private val REGULAR_FILE = Integer.parseInt("0100000", 8)
   private val SYMBOLIC_LINK = Integer.parseInt("0120000", 8)
   private val SOCKET = Integer.parseInt("0140000", 8)


   /**
    * @return the privileges associated to the owner of the node.
    */
   def user: Privilege =   ConcretePrivilege(permissions, userPrivilege)

   /**
    * @return the privileges associated to the group owning the node.
    */
   def group: Privilege =  ConcretePrivilege(permissions, groupPrivilege)

   /**
    * @return the privileges associated to those who are not the user nor the group.
    */
   def other: Privilege =  ConcretePrivilege(permissions, otherPrivilege)

   /**
    * @return the value of the UID bit.
    */
   def uIdBit: Boolean = (permissions & SET_UID) != 0

   /**
    * @return the value of the group ID bit.
    */
   def groupIdBit: Boolean = (permissions & SET_GROUP_ID) != 0

   /**
    * @return the value of the sticky bit.
    */
   def stickyBit: Boolean = (permissions & STICKY) != 0

   /**
    * @return true if the node is a fifo file; false otherwise.
    */
   def fifo: Boolean = (permissions & FIFO) != 0

   def characterDevice: Boolean = (permissions & CHARACTER_DEVICE) != 0
   def directory: Boolean = (permissions & DIRECTORY) != 0
   def blockDevice: Boolean = (permissions & BLOCK_DEVICE) != 0

   /**
    *
    * @return true if the node is a regular file, false otherwise.
    */
   def isRegularFile: Boolean = (permissions & REGULAR_FILE) != 0

   /**
    *
    * @return true if the node is a symbolic link, false otherwise.
    */
   def isSymbolicLink: Boolean = (permissions & SYMBOLIC_LINK) != 0

   /**
    *
    * @return true if the node is a socket, false otherwise.
    */
   def isSocket: Boolean = (permissions & SOCKET) != 0


   override def toString = Integer.toOctalString(permissions)
}

trait Privilege
{
   def canRead: Boolean
   def canWrite: Boolean
   def canExecute: Boolean
}

private case class ConcretePrivilege(permissions: Int, privileges: (Int, Int, Int)) extends Privilege
{
   override def canRead: Boolean =     (permissions & privileges._1) != 0
   override def canWrite: Boolean =    (permissions & privileges._2) != 0
   override def canExecute: Boolean =  (permissions & privileges._3) != 0
}
