package idp.sandbox.server.command.test;

import scala.io.{Source, BufferedSource}
import scala.xml.{Elem, Document}
import scala.xml.parsing.ConstructingParser
import org.scalatest.FunSuite

import java.rmi.registry.{LocateRegistry, Registry} 

import idp.sandBox.server.constants.Constants
import idp.sandBox.server.managers.SettingsManager
import idp.sandBox.server.MessagingInterface

class SendServerCommandTest extends FunSuite {

  private var connection: MessagingInterface = null
  private var settingsManager = SettingsManager
  
  test("check connection") {
    getConnection
    assert(connection != null)
  }
  
  test("send server command collect tag names") {
    if (connection == null) getConnection
    connection collectTagNames
    val res = connection getFileContentByRelativePath "/annotators/tree.xml"
    try {
      val p = ConstructingParser.fromSource(Source.fromString(res), true)
      val root = p.document.docElem
      assert(true)
    } catch {
      case e: Exception => assert(false, e.getMessage())
    }
  }
  
  private def getConnection: MessagingInterface =  {
    if (this.connection == null)
    { 
      // get the registry
      val registry : Registry = LocateRegistry.getRegistry(
            "127.0.0.1",
            settingsManager.getServerPort())
      connection = registry.lookup(Constants.defaultServerName).asInstanceOf[MessagingInterface]
    }
    connection 
  }
}