package mgoeminne.scalagit.visitors

import mgoeminne.scalagit.{Commit, Git}

/**
 * An element extractor is an entity able to extract some elements
 * from a Git repository and to associate them to commits in this repository.
 * 
 * @tparam B type of elements for which a value is computed
 */
trait ElementExtractor[B,T]
{
   /**
    * Transforms an element into something more valuable
    * @param element the element to map.
    * @return the value associated to <i>element</i>.
    */
   def map(element: B): T

   /**
    * Generates the basic elements for which a value must be computed
    * @return all the basic value for which a value must be computed
    */
   def elements(git: Git): Seq[B]

   /**
    * Determines the elements associated to a particular commit.
    * @param commit the considered commit
    * @return the elements associated to <i>commit</i>
    */
   def associator(commit: Commit): Seq[B]
}
