package idp.sandBox.server.command

import java.rmi.registry.{LocateRegistry, Registry} 

import idp.sandBox.server.constants.Constants
import idp.sandBox.server.managers.SettingsManager

object SendServerCommand {

  private var connection : MessagingInterface = null
  private var settingsManager = SettingsManager

  private def getConnection() : MessagingInterface =
  {
    if (connection == null)
    { 
      // get the registry
      val registry : Registry = LocateRegistry.getRegistry(
            "127.0.0.1",
            settingsManager.getServerPort())
      connection = registry.lookup(Constants.defaultServerName).asInstanceOf[MessagingInterface]
    }
    connection 
  }
  
  /**
  *   Shuts down server.
  */
  private def shutDownServer()
  {
    val con : MessagingInterface = getConnection()
    con.shutDownServer(1637171910328672563L);
  }
  
  /**
  *   Creates tree on base of training set.
  */ 
  private def createTree = {
    val con = getConnection 
    con.collectTagNames
  }
  
  /**
  *   Runs Applier.
  */
  private def apply {
    val con = getConnection
    con apply
  }
  
  /**
  *   Runs Applier.normalize.
  */
  private def normalize {
    val con = getConnection
    con normalize
  }
     
  
  def main(args: Array[String])
  {
    if (args.length > 0) {
      val arg = args(0).trim()
      arg match {
        case "-s" => shutDownServer        
        case "--tree" => createTree
        case "--apply" => apply
        case "--normalize" => normalize
        case _ => usagePrint
      }
    } else {
      usagePrint
    }
    return
  }
  
  /**
  *   Prints usage message.
  */
  private def usagePrint = {
    println("""Parameters: 
         -s             Shutdown server
         --tree         Create xml tree from train-set
         --apply        Runs Applier
         --normalize    Runs Applier.nomalize
         """)
  }
}
