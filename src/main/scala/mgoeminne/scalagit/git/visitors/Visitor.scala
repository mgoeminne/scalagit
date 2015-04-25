package mgoeminne.scalagit.git.visitors

import mgoeminne.scalagit.git.{Commit, Git}

/**
 * A visitor responsible of extracting information from the elements of a Git repository
 * @tparam B type of elements for which a value is computed
 * @tparam T The type of value mapped from the generated elements
 * @tparam V the type of the final value associated to an element.
 *
 */
trait Visitor[B,T,V]
{
  /**
   * Transforms an element into something more valuable
   * @param element the element to map.
   * @return the value associated to <i>element</i>.
   */
   def map(element: B): T

  /**
   * A function applied to element values to obtain the commit value.
   * @param a an element value
   * @param b an other element value
   * @return the merge of <i>a</i> and <i>b</i>
   */
   def reductor(a: T, b: T): T

   def defaultValue: T

  /**
   * A function applied to commit values to obtain the definitive
   * value associated to a function.
   * @param value the value associated to a commit
   * @return the image <i>value</i>, as it will be stored.
   */
   def transform(value: T): V

  /**
   * Generates the basic elements for which a value must be computed
   * @return all the basic value for which a value must be computed
   */
   def generator(git: Git): Set[B]

  /**
   * Determines the elements associated to a particular commit.
   * @param commit the considered commit
   * @return the elements associated to <i>commit</i>
   */
   def associator(commit: Commit): Seq[B]
}
