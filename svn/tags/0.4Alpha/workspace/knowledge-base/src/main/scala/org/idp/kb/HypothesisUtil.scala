package org.idp.kb


import java.net.URI

import model.cogitative.decision.{Value, CogitativeHypothesis}
import model.domain.{Module, AnyDomain}
import org.apache.log4j.Logger

import org.semanticweb.owl.model._

object HypothesisUtil {
  
  val log = Logger.getLogger(this.getClass)
  
   /*
  * Returns the result of the implication of specified CogitativeHypothesises, if found any positive result.
  * @param hypothesises Set[CogitativeHypothesis] to find most probable
  * @return most probable Some[CogitativeHypothesis] if not found any CogitativeHypothesis with non false result None.
  */
  def findMostProbableHypothesis(hypothesises: Set[CogitativeHypothesis], statement: Statement): Option[CogitativeHypothesis] = {
    // find only positive results
    val posRes = hypothesises.filter(h => {
      val hRes: Option[Pair[AnyDomain, Double]] = h(statement)
      hRes match {
        case Some(pair) => {
          true
        }
        case None => false
      }
    })
    if (!posRes.isEmpty) {
      // sort and get most probable hypothesis
      val res = posRes.toList.sort((e1, e2) => {
        e1.getProbability match {
          case None => true
          case Some(e1Pr) => {
            e2.getProbability match {
              case None => false
              case Some(e2Pr) => e1Pr < e2Pr
            }
          }
        }
      })
      Some(res(0))
    } else {
      None
    }
  }

  /*
  * Returns Set of CogitativeHiypothesis of the specified hypothesis OWLClass that returns non false individual.
  * @param hypothesisClas OWLClass of hypothesises
  * @param statement Statement to execute
  * @returns Set of the CogitativeHypothesis-es
  */
  def filterHypothesis(hypothesisClass: OWLClass, statement: Statement): Set[CogitativeHypothesis] = {
    val hypoIndivids = scala.collection.jcl.Set(hypothesisClass.getIndividuals(Constant.ontology))

    log debug ("hypoIndividuals " + hypoIndivids)

    // initiate hypothesises
    var hypoSet = Set[CogitativeHypothesis]()
    for (val hypoIndivid <- hypoIndivids) {
      hypoSet = hypoSet + (new CogitativeHypothesis(hypoIndivid))
      log debug "individ " + hypoIndivid
      log debug "hypoSet " + hypoSet
    }

    log debug "hypoSet " + hypoSet

    val res = hypoSet.filter(hypo => {
      val res = hypo(statement)
      log debug "res hypo " + res
      res match {
        case Some(r) => true
        case None => false
      }
    })
    log debug "res " + res
    res
  }

  /*
  * Returns Set of CogitativeHiypothesis of the specified hypothesis OWLClass.
  * @param hypothesisClas OWLClass of hypothesises
  * @returns Set of the CogitativeHypothesis-es
  */
  def findHypothesisOfAClass(hypothesisClass: OWLClass): Set[CogitativeHypothesis] = {
    val hypoIndivids = scala.collection.jcl.Set(hypothesisClass.getIndividuals(Constant.ontology))

    log debug ("hypoIndividuals " + hypoIndivids)

    // initiate hypothesises
    var hypoSet = Set[CogitativeHypothesis]()
    for (val hypoIndivid <- hypoIndivids) {
      hypoSet = hypoSet + (new CogitativeHypothesis(hypoIndivid))
      log debug "individ " + hypoIndivid
      log debug "hypoSet " + hypoSet
    }
    hypoSet
  }
}
