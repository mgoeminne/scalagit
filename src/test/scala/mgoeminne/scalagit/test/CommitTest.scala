package mgoeminne.scalagit.test

import java.io.File

import mgoeminne.scalagit.Git
import org.scalatest.{Matchers, FlatSpec}

class CommitTest extends FlatSpec with Matchers
{
   val classLoader = getClass().getClassLoader();
   val file = new File(classLoader.getResource("scalagit.git").getFile());
   val repository = Git(file)

   val parents: Seq[Int] = repository.commits map (c => c.parents.size)

   "Any commit" should "have at most 2 parents" in {
      all (parents) should be <= 2
   }

   "exactly 1 commit" should "have no parents" in {
      exactly(1, parents) shouldBe 0
   }

   "diff associated to the last commit" should "have the right length" in {
      repository.commits.head.diff.size should equal (3162)
   }
}
