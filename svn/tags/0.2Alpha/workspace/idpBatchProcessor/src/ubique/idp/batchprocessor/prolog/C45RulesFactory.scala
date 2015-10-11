package ubique.idp.batchprocessor.prolog;

import scala.xml.Node
import scala.collection.mutable.ArrayBuffer

import jp.ac.kobe_u.cs.prolog.lang.{Predicate, Term, ListTerm, SymbolTerm}

import ubique.idp.batchprocessor.xml.ElementsPathCollector
import ubique.idp.batchprocessor.prolog.adapter.Mapping2PredicatesAdapter
import ubique.idp.batchprocessor.jprolog.dynamic.predicates.PredicateC45Rule

object C45RulesFactory {
  
  type PredicatesMappingArray = ArrayBuffer[Tuple2[Term, SymbolTerm]]
  
  /**
    *  Creates Predicates like 
    * <code>c45([path], subst) :- path = specified_path</code>
    * on base of XML tree with specified attributes, their values defines substitution
    * @param attributeName the name of attribute that denotes substitution 
    * @param tree Node of the tree to be processed
    * @return Predicate result
    * @throws IllegalArgumentException in case attributeName or tree is not specified
    */
   def createC45Predicate(attributeName: String, tree:Node): Option[Predicate] = {
      if (attributeName == null || attributeName == "") 
        throw new IllegalArgumentException("No attribute name set")
      if (tree == null) 
        throw new IllegalArgumentException("No tree to process is set")
      
      val predicatesArray = Mapping2PredicatesAdapter.map(
          ElementsPathCollector.collectPathMapAttributes(attributeName, tree))
      if (predicatesArray.size > 0) {
        Some(createPredicatesAndWrap(predicatesArray))
      } else {
        None
      }
   }
   /**
     *  Creates wrapping root predicate and adds all sub predicates
     * @param predicatesMappingArray ArrayBuffer[Tuple[Term,SymbolTerm]]
     * @return Predicate root
     */
   private def createPredicatesAndWrap(predicatesMappingArray: PredicatesMappingArray): Predicate = {
     val root = new PredicateC45Rule()
     predicatesMappingArray.map[Unit]((predicateMapping) => {
       root.add(new C45Rule(predicateMapping._1, predicateMapping._2))
     })
     root
   }
}
