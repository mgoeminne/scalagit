package mgoeminne.scalagit.visitors

import mgoeminne.scalagit.{Commit, Git}

/**
 * A visitor responsible of extracting information from the elements of a Git repository
 * @tparam B type of elements for which a value is computed
 * @tparam T The type of value mapped from the generated elements
 * @tparam V the type of the final value associated to an element.
 *
 */
trait Visitor[B,T,V] extends ElementExtractor[B,T] with DataTransformator[T,V]