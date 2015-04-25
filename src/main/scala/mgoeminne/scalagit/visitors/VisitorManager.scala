package mgoeminne.scalagit.visitors

import mgoeminne.scalagit.{Commit, Git}
import mgoeminne.scalagit.git.Commit

import scala.collection.parallel.ParSeq
import scala.collection.parallel.immutable.ParMap

object VisitorManager
{
  /**
   * Creates associations between commits and their values, following a given visitor.
   * @param repo      the Git repository to analyse.
   * @param visitor   the visitor used to transform the repository contents to a valuable information.
   * @tparam T        type of the value associated to each blob in the Git repository.
   * @tparam V        type of the value associated to each commit.
   * @return          a value associated to each commit in the Git repository.
   */
  def process[B, T, V](repo: Git, visitor: Visitor[B, T, V]): Map[Commit, V] =
  {
    val elements: ParSeq[B] = visitor.generator(repo).toSeq.par
    val values: ParMap[B,T] = elements.map(element => element -> visitor.map(element)).toMap

    repo.commits.par.map(commit => commit -> visitor.transform( visitor   .associator   (commit)
                                                                          .filter       (values.contains)
                                                                          .map          (values.apply)
                                                                          .reduceOption (visitor.reductor)
                                                                          .getOrElse    (visitor.defaultValue)))
                    .toMap.seq
  }

  /**
   * Creates a stream of associations between commits and their values, following a given visitor.
   * This function consumes less memory than process, since it doesn't store the values of all commits at a time.
   * @param repo     the Git repository to analyse.
   * @param visitor  the visitor used to transform the repository contents to a valuable information.
   * @tparam T       type of the value associated to each blob in the Git repository.
   * @tparam V       type of the value associated to each commit.
   * @return         a value associated to each commit in the Git repository.
   */
  def streamProcess[B, T, V](repo: Git, visitor: Visitor[B, T, V]): Stream[(Commit, V)] =
  {
    val elements: ParSeq[B] = visitor.generator(repo).toSeq.par
    val values: ParMap[B,T] = elements.map(element => element -> visitor.map(element)).toMap


    repo.commits.toStream.map(commit => commit -> visitor.transform( visitor    .associator   (commit)
                                                                                .filter       (values.contains)
                                                                                .map          (values.apply)
                                                                                .reduceOption (visitor.reductor)
                                                                                .getOrElse    (visitor.defaultValue)))
  }
}
