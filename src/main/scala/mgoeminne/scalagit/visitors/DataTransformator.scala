package mgoeminne.scalagit.visitors

/**
 * The entities of such a trait are able to transform elements into
 * valuable products
 * @tparam T
 * @tparam V
 */
trait DataTransformator[T,V]
{


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
}
