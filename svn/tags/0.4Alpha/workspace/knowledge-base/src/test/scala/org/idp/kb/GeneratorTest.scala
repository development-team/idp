package org.idp.kb

import applicationgenerator._
import model.domain.{Module, Method, Field}
import org.junit._
import Assert._
import org.apache.log4j.Logger
import java.io.File
import org.semanticweb.owl.apibinding.OWLManager

/**
 * Test class for FieldGenerator, MethodGenerator, ModuleGenerator
 * @author Max Talanov
 * @date: 30.11.2009
 * Time: 20:18:03
 */

@Test
class GeneratorTest {
  val log = Logger.getLogger(this.getClass)

  // @Test
  def testTargetApplication {
    val owlManager = OWLManager createOWLOntologyManager
    val targetApplicationPhysicalURI = new File("TargetAppRDF.owl").toURI
    val physicalURI = new File("LinguisticCogitativeDomainModelShort.owl").toURI
    owlManager loadOntologyFromPhysicalURI targetApplicationPhysicalURI
    val ontology = owlManager loadOntologyFromPhysicalURI physicalURI
    assertNotNull(ontology)
  }

  @Test
  def testFieldGenerator {
    val field = new Field("newField")
    val fG = new JavaFieldGenerator
    val res = fG.apply(field)
    log.debug(res)
    assertEquals("\n\tprivate String newField;\n \tpublic String getNewField() { return this.newField; }\n \tpublic  void  setNewField( String in) { this.newField = in ; }", res)
  }

  @Test
  def testMethodGenerator {
    val method = new Method("CreateCustomerMethod")
    val mG = new JavaMethodGenerator
    var context: Map[String, List[Field]] = Map[String, List[Field]]()
    var customerArray = List[Field](new Field("address_field"))
    context += "Customer" -> customerArray
    val res = mG.apply(method, context)
    log.debug(res)
    val toCompare = "public Object executeDatabaseOperation(c) {\nint i=-1;\nPreparedStatement sta = conn.prepareStatement(\"INSERT INTO CUSTOMER (\naddress_field\n) VALUES (\n ?, );\n\n i=i+1;\n sta.setString(i,c.getAddress());\n\nint rows_updated = sta.executeUpdate();\nsta.close();\nreturn new Integer(rows_updated);\n}";
    assertEquals(toCompare, res);
  }

  @Test
  def testModule {
    val module = new Module("CreateCustomerServlet")
    val mG = new JavaClassGenerator
    val res = mG.apply(module)
    log.debug(res)
    assertEquals("\tprivate String address_field;\n \tpublic String getAddress() { return address_field; }\n \tpublic  void  setAddress( String in) { address_field = in ; }", res)
  }

  // @Test
  def testJSPGeneration {
    val module = new Module("index")
    val field = new Field("FF")
    module.addField(field)
    val mG = ModuleGenerator(module)
    val res = mG.apply(module)
    log.debug(res)
    assertEquals("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"> \n<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\n<title>Welcome Page</title>\n\n<script language=\"Javascript\">\n<body>\n\n<a href=\"/Orders/ListCustomers\">List Registered Customer</a><p>\n\n<hr>\n<form method=\"get\" action=\"/Orders/CreateCustomer\">\n<b>Customer Registration</b><p>\nFirst Name: <input type=\"text\" name=\"first_name\" size=\"30\"><br>\nLast Name:  <input type=\"text\" name=\"last_name\" size=\"30\"><br>\nAddress:   <input type=\"text\" name=\"address\" size=\"60\"><br>\n<input type=\"submit\" value=\"Create Customer\" onClick=\"return testCreateCustomer();\">\n", res)
  }

  // @Test
  def testSQLGeneration {
    val module = new Module("Customer")
    val field = new Field("FF")
    module.addField(field)
    val mG = ModuleGenerator(module)
    val res = mG.apply(module)
    log.debug(res)
    assertEquals("ALTER TABLE  Customer ADD FF VARCHAR(50)\n ", res)
  }

  // @Test
  def writerTest {
    log debug "Writer Test"
    val module = new Module("CustomerObject")
    val field = new Field("FF")
    module.addField(field)
    val res = ModuleWriter(new File("TargetApp"), module)
    assertNotSame(None, res)
  }

  @Test
  def writeChangedModulesTest {
    log debug "Writer Test"
    val module = new Module("CreateCustomer")
    val field = new Field("newField")
    module.addField(field)
    module.collectModules(Set[Module]()) match {
      case Some(changedModules) => {
        for (val chM: Module <- changedModules) {
          log debug "Module changed " + chM + " in the file " + chM.getFile
          val res = ModuleWriter(new File("TargetApp"), chM)
          assertNotSame(None, res)
        }
      }
      case None => log debug "No modules collected"
    }
  }
}