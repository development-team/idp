package org.idp.kb

import org.specs._
import org.specs.runner.{ConsoleRunner, JUnit4}

import java.net.URI
import org.apache.log4j.Logger

import org.semanticweb.owl.apibinding.OWLManager

import org.idp.kb.model.cogitative.Comprehension
import org.idp.kb.model.cogitative.decision.{Value}
import org.idp.kb.model.cogitative.decision.{Operator, AnyExpression}
import org.idp.kb.model.cogitative.decision.CogitativeHypothesis

class ExpressionSpecTest extends JUnit4(ExpressionSpec)
//class MySpecSuite extends ScalaTestSuite(MySpec)
object ExpressionSpecRunner extends ConsoleRunner(ExpressionSpec)

// Testing knowledge base operations
object ExpressionSpec extends Specification {
  val log = Logger.getLogger(this getClass)

  "idp" should {

    "check and operator" in {
      log.debug("test: " + "and operator")

      // create operator with operands
      val baseURI = Constant.base
      val owlManager = OWLManager.createOWLOntologyManager()
      val ontology = owlManager createOntology (URI create baseURI)
      val dataFactory = owlManager getOWLDataFactory

      val operatorURIString = Constant.localNamesPrefix + Constant.andURIString
      val operatorIndivid = dataFactory getOWLIndividual (URI create (baseURI + operatorURIString))
      val op = Operator(operatorIndivid)

      val trueIndividual = Constant.trueIndividual;
      val value = new Value(trueIndividual)

      val res = op.interpret(null, value, value)

      assert(res.isTrue)
    }

    "check or operator" in {
      log.debug("test: " + "or operator")

      // create operator with operands
      val baseURI = Constant.base
      val owlManager = OWLManager.createOWLOntologyManager()
      val ontology = owlManager createOntology (URI create baseURI)
      val dataFactory = owlManager getOWLDataFactory

      val operatorURIString = Constant.localNamesPrefix + Constant.orURIString
      val operatorIndivid = dataFactory getOWLIndividual (URI create (baseURI + operatorURIString))
      val op = Operator(operatorIndivid)

      val trueIndividual = Constant.trueIndividual;
      val value = new Value(trueIndividual)

      val falseIndividual = Constant.falseIndividual;
      val valueFalse = new Value(falseIndividual)

      val res = op.interpret(null, value, valueFalse)

      assert(res.isTrue)
    }

    "check the hasObject operator" in {
      val statementString = "Menta place Login field on Customer page."
      val statement = Statement(statementString)
      log debug "statement: " + statement

      // create operator with operands
      def baseURI = Constant.base
      val owlManager = OWLManager.createOWLOntologyManager()
      def ontology = owlManager createOntology (URI create baseURI)
      def dataFactory = owlManager getOWLDataFactory

      val operatorURIString = Constant.localNamesPrefix + Constant.hasObjectURIString
      val operatorIndivid = dataFactory getOWLIndividual (URI create (baseURI + operatorURIString))
      val op = Operator(operatorIndivid)

      val objIndividual = dataFactory getOWLIndividual (URI create (baseURI + "#Object.Phrase.Word.field_Word.Login"))
      val value = new Value(objIndividual)

      val res = op.interpret(statement.toList(0), null, value)

      assert(res.isTrue)

    }

    "check hasSubject operator " in {
      val statementString = "Menta place Login field on Customer page."
      val statement = Statement(statementString)
      log debug "test: " + "hasSubject"

      // create operator with operands
      def baseURI = Constant.base
      val owlManager = OWLManager.createOWLOntologyManager()
      def ontology = owlManager createOntology (URI create baseURI)
      def dataFactory = owlManager getOWLDataFactory

      val operatorURIString = Constant.localNamesPrefix + Constant.hasSubjectURIString
      val operatorIndivid = dataFactory getOWLIndividual (URI create (baseURI + operatorURIString))
      val op = Operator(operatorIndivid)

      val objIndividual = dataFactory getOWLIndividual (URI create (baseURI + "#Subject.Phrase.Word.place_Word.Menta"))
      val value = new Value(objIndividual)

      val res = op.interpret(statement.toList(0), null, value)

      assert(res.isTrue)
    }

    "check hasObjectType operator " in {
      val statementString = "Menta place Login field on Customer page."
      val statement = Statement(statementString)
      log debug "test: " + "hasObjectType"

      // create operator with operands
      val baseURI = Constant.base
      val owlManager = OWLManager.createOWLOntologyManager()
      val ontology = owlManager createOntology (URI create baseURI)
      val dataFactory = owlManager getOWLDataFactory

      val operatorURIString = Constant.localNamesPrefix + Constant.hasObjectTypeURIString
      val operatorIndivid = dataFactory getOWLIndividual (URI create (baseURI + operatorURIString))
      val op = Operator(operatorIndivid)

      val objIndividual = dataFactory getOWLIndividual (URI create (baseURI + "#ObjectType.dobj"))
      val value = new Value(objIndividual)

      val res = op.interpret(statement.toList(0), null, value)

      assert(res.isTrue)

    }


    "check not operator" in {
      log.debug("test: " + "not operator")

      val statementString = "Menta place Login field on Customer page."
      val statement = Statement(statementString)
      log debug "test: " + "not operator"

      // create operator with operands
      val baseURI = Constant.base
      val owlManager = OWLManager.createOWLOntologyManager()
      val ontology = owlManager createOntology (URI create baseURI)
      val dataFactory = owlManager getOWLDataFactory

      val operatorURIString = Constant.localNamesPrefix + Constant.notURIString
      val operatorIndivid = dataFactory getOWLIndividual (URI create (baseURI + operatorURIString))
      val op = Operator(operatorIndivid)

      val falseIndividual = Constant.falseIndividual;
      val valueFalse = new Value(falseIndividual)

      val res = op.interpret(statement.toList(0), valueFalse, valueFalse)

      assert(res isTrue)
    }

    "check simple expression " in {
      log.info("test: " + "exression test")
      val statementString = "Menta place Login field on Customer page."
      val statement = Statement(statementString)

      val baseURI = Constant.base
      val owlManager = OWLManager.createOWLOntologyManager()
      val ontology = owlManager loadOntology (Constant.physicalOutputURI)
      val dataFactory = owlManager.getOWLDataFactory

      val exprIndivid = dataFactory.getOWLIndividual(URI create (Constant.baseURI + Constant.localNamesPrefix + "expr_prep_to"))

      val expr = AnyExpression(exprIndivid)

      val res = expr(statement.toList(0))

      log debug (res)

      assert(res isTrue)
    }

    "check primitive hypothesis " in {
      log.info("test: " + "hypothesis test")

      val statementString = "Menta place Login field on Customer page."
      val statement = Statement(statementString)

      val baseURI = Constant.base
      val owlManager = OWLManager.createOWLOntologyManager()
      val ontology = owlManager loadOntology (Constant.physicalOutputURI)
      val dataFactory = owlManager.getOWLDataFactory

      val hypoIndivid = dataFactory.getOWLIndividual(URI create (Constant.baseURI + Constant.localNamesPrefix + "destinationHipothesis"))

      val hypo = new CogitativeHypothesis(hypoIndivid)

      val res = hypo(statement.toList(0))

      log debug (res)

      for (val r <- res) {
        assert(r._1 != None)
      }
    }

    "check hypothesis filetering" in {
      log.info("test: " + "hypothesis filtering test")

      val statementString = "Menta place Login field on Customer page."
      val statement = Statement(statementString)

      val baseURI = Constant.base
      val owlManager = OWLManager.createOWLOntologyManager()
      val ontology = owlManager loadOntology (Constant.physicalOutputURI)
      val dataFactory = owlManager.getOWLDataFactory

      val hypoClass = dataFactory.getOWLClass(URI create (Constant.baseURI + Constant.localNamesPrefix + "CogitativeHipothesis"))

      val res = HypothesisUtil.filterHypothesis(hypoClass, statement.toList(0))

      log debug (res)
      res mustNot beNull
    }

    "check comprehension creation of AnyDomain AnyDestination" in {
      log.info("test: " + "comprehension creating test")

      val statementString = "Menta place Login field on Customer page."
      val statement = Statement(statementString)

      val baseURI = Constant.base
      val owlManager = OWLManager.createOWLOntologyManager()
      val ontology = owlManager loadOntology (Constant.physicalOutputURI)
      val dataFactory = owlManager.getOWLDataFactory

      val comprehensionIndivid = dataFactory.getOWLIndividual(URI create (Constant.baseURI + Constant.localNamesPrefix + "destinationComprehension"))

      val c = new Comprehension(comprehensionIndivid, statement.toList(0))

      val res = c()
      log debug (res)
      res mustNotBe None
    }

    "check comprehension creation of AnyDomain anyActionObject" in {
      log.info("test: " + "comprehension creating test")

      val statementString = "Menta place Login field on Customer page."
      val statement = Statement(statementString)

      val baseURI = Constant.base
      val owlManager = OWLManager.createOWLOntologyManager()
      val ontology = owlManager loadOntology (Constant.physicalOutputURI)
      val dataFactory = owlManager.getOWLDataFactory

      val comprehensionIndivid = dataFactory.getOWLIndividual(URI create (Constant.baseURI + Constant.localNamesPrefix + "actionObjectComprehension"))

      val c = new Comprehension(comprehensionIndivid, statement.toList(0))

      val res = c()
      log debug (res)
      res mustNotBe None
    }
  }

  "check comprehension creation of AnyDomain addFieldAction" in {
    log.info("test: " + "comprehension creating test")

    val statementString = "Menta place Login field on Customer page."
    val statement = Statement(statementString)

    val baseURI = Constant.base
    val owlManager = OWLManager.createOWLOntologyManager()
    val ontology = owlManager loadOntology (Constant.physicalOutputURI)
    val dataFactory = owlManager.getOWLDataFactory

    val comprehensionIndivid = dataFactory.getOWLIndividual(URI create (Constant.baseURI + Constant.localNamesPrefix + "actionComprehension"))

    val c = new Comprehension(comprehensionIndivid, statement.toList(0))

    val res = c()
    log debug (res)
    res mustNotBe None
  }
}
