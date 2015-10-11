package idp.sandBox.server.command

import java.rmi.registry.{LocateRegistry, Registry} 

import idp.sandBox.server.constants.Constants
import idp.sandBox.server.managers.SettingsManager

object SendServerCommand {

  var connection : MessagingInterface = null
  
  var settingsManager : SettingsManager = null

  def getConnection() : MessagingInterface =
  {
    if (connection == null)
    {
      if (settingsManager == null)
        settingsManager = new SettingsManager()
      
      // get the registry
      val registry : Registry = LocateRegistry.getRegistry(
            "127.0.0.1",
            settingsManager.getServerPort())
            
      connection = registry.lookup(Constants.defaultServerName).asInstanceOf[MessagingInterface]
    }
    
    connection 
  }
  
  def shutDownServer()
  {
    val con : MessagingInterface = getConnection()
    con.shutDownServer(1637171910328672563L);
  }
  
  def main(args: Array[String])
  {
    if (args.length == 0 || args(0).compareTo("-s") != 0)
    {
      println("Parameters:\n\t -s \tShutdown server")
      return
    }
        
    shutDownServer()
  }
}
