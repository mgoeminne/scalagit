package mgoeminne.scalagit.test

import java.io.File

import mgoeminne.scalagit.{Commit, Git}
import org.joda.time.LocalDateTime
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, FlatSpec}


class GitTest extends FlatSpec with Matchers
{
   val classLoader = getClass().getClassLoader();
   val file = new File(classLoader.getResource("scalagit.git").getFile());
   val repository = Git(file)

   "The resource repository" should "have 19 commits" in {
       repository.commits.size should be === 19
   }

   it should "contains 33 different files over its entire history" in{
      repository.files.size should be (33)
   }

   "All commits from the resource repository" should "have mgoeminne@gmail.com as author" in {
      repository.commits.foreach(commit => commit.author shouldBe Some("mgoeminne@gmail.com"))
   }

   it should "be ordered by date" in {
      repository.commits.map(_.date).sliding(2,1).foreach(pair =>
      {
         assertResult(true)
         {
            pair(0) isAfter pair(1)
         }
      })
   }
}
