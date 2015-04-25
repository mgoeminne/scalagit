package mgoeminne.scalagit.git.visitors

import java.io.File

import mgoeminne.scalagit.git.Git

/**
 * Lists the repositories that contains one of the given Java annotation
 */
object ContainAnnotation
{
  def main(args: Array[String])
  {
    val pred = predicate(args.drop(1))

    val directory: File = new File(args(0))

    directory .listFiles()
      .filter(Git.isGitRepository(_))
      .toStream
      .filter(repo => pred(Git(repo)))
      .foreach(println)
  }

  def predicate(annotations: Seq[String]): Git => Boolean =
  {
      val preparedAnnotations = annotations.map(a => "@" + a)
      return (repo: Git) => repo.blobs.par.exists(blob => blob.lines.exists(line => preparedAnnotations.exists(annotation => line contains annotation)))
  }
}
