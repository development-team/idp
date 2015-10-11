package org.idp.kb

import model.domain._
import org.junit._
import Assert._
import org.apache.log4j.Logger
import org.semanticweb.owl.io.RDFXMLOntologyFormat

/**
 * User: Max
 * Date: 22.11.2009
 * Time: 0:49:36
 */

@Test
class AddFieldActionTest {
  val log = Logger.getLogger(this.getClass)

  @Test
  def addFieldActionTest() {
    val name = "TestAddFieldAction"
    val destination = new Destination("TestDestination", "Destination module to test")
    val field = new ActionObject("TestField", "Field to test")
    var domainObjects = List[AnyDomain](destination, field);
    val addField = new AddFieldAction(name, domainObjects);
    val res = addField()
    log debug res
    assertNotNull(res)
  }

  @Test
  def addFieldAndSaveActionTest() {
    val name = "TestAddFieldAction"
    val destination = new Destination("TestModule", "Destination module to test")
    val field = new ActionObject("TestField", "Field to test")
    var domainObjects = List[AnyDomain](destination, field);
    val addField = new AddFieldAction(name, domainObjects);
    val res = addField.save
    Constant.owlManager saveOntology (Constant.ontology, new RDFXMLOntologyFormat(), Constant.physicalOutputURI)
    log debug res
    assertNotNull(res)
  }
}