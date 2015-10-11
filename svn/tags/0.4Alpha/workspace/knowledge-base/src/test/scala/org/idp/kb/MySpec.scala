package org.idp.kb

import org.specs._
import org.specs.runner.{ConsoleRunner, JUnit4}

import org.idp.kb.model.ObjectType

import java.net.URI

import org.semanticweb.owl.model.{AddAxiom, OWLIndividual}
import org.semanticweb.owl.apibinding.OWLManager
import org.semanticweb.owl.io.RDFXMLOntologyFormat

class MySpecTest extends JUnit4(MySpec)
//class MySpecSuite extends ScalaTestSuite(MySpec)
object MySpecRunner extends ConsoleRunner(MySpec)

// Testing knowledge base operations
object MySpec extends Specification {
  "kb subsystem" should {
    
    /* "run Statement factory" in {
      val statement = "Menta place Login field on Customer page."
      val r = Statement(statement)
      Console println "statement: " + statement
      Console println " res "
      Console println r
      r mustNotBe Nil
      assert(!(r isEmpty ))
    } */
    
    "run statement factory and save owl file" in {
      val statement = "Menta place Login field on Customer page."
      val r = Statement(statement)
      Console println "statement: " + statement
      Console println "res: " + r
      r mustNotBe Nil
      assert(!(r isEmpty ))
      
      for (i <- r) {
        Console println i
        i.save
      }
      Constant.owlManager saveOntology (Constant.ontology, new RDFXMLOntologyFormat(), Constant.physicalOutputURI)
    }
    
    "save objectType" in {
      val oT = new ObjectType("dobject", "direct object")
      oT.save
    }
    
    "save some owl" in {
      def physicalURI =  URI create "file:///C:/idpWorkspace/knowledge-base/test1.owl"
      def baseURI = Constant.base
      val owlManager = OWLManager.createOWLOntologyManager()
      val ontology = owlManager createOntology (URI create baseURI)
      // val ontology = owlManager loadOntologyFromPhysicalURI Constant.physicalURI
      def dataFactory = owlManager getOWLDataFactory
      
      val classURI = baseURI + "#ParentClass"
      val individualName = "#Individ"
      val individ = dataFactory getOWLIndividual(URI create (baseURI + individualName))
      val assertion = dataFactory getOWLClassAssertionAxiom(
    		  individ, dataFactory getOWLClass (URI create classURI))
      val axiomChange = new AddAxiom(ontology, assertion)
      owlManager applyChange axiomChange
      owlManager saveOntology (ontology, new RDFXMLOntologyFormat(), physicalURI)
    }
    
  }
}