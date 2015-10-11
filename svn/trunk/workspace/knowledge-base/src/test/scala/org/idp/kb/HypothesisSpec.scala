package org.idp.kb

import org.specs._
import org.specs.runner.{ConsoleRunner, JUnit4}

import java.net.URI
import org.apache.log4j.Logger

import org.semanticweb.owl.apibinding.OWLManager

import org.idp.kb.model.cogitative.decision.{Operator, AnyExpression}
class HypothesisSpecTest extends JUnit4(HypothesisSpec)
//class MySpecSuite extends ScalaTestSuite(HypothesisSpec) 
object HypothesisSpecRunner extends ConsoleRunner(HypothesisSpec)

// Testing knowledge base operations
object HypothesisSpec extends Specification {

  val log = Logger.getLogger(this getClass)
  
  "idp prototype" should {
    "check most probable hypothesis production" in {
      
      log.info("test: " + "hypothesis production test")
      val statementString = "Menta place Login field on Customer page."
      val statement = Statement(statementString)
      
      val baseURI = Constant.base
      val owlManager = OWLManager.createOWLOntologyManager()
      val ontology = owlManager loadOntology (Constant.physicalOutputURI)
      val dataFactory = owlManager.getOWLDataFactory
      
      val hypoClass = dataFactory.getOWLClass(URI create (Constant.baseURI + Constant.localNamesPrefix + "CogitativeHipothesis"))
      
      val hypoRes = HypothesisUtil.filterHypothesis(hypoClass, statement.toList(0))
      
      log debug hypoRes
      
      val res = HypothesisUtil.findMostProbableHypothesis(hypoRes, statement.toList(0))
      
      log debug ("Search result = " + res)
      for (val r <- res) {
        log debug r(statement.toList(0))
      }
      res mustNotBe None
    }
  }
}
