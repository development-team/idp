package idp.sandBox.server.managers;

import java.io.{FileInputStream, FileOutputStream, IOException}
import java.util.Properties
import java.util.Enumeration

import idp.sandBox.server.constants.Constants

object SettingsManager {

  private var properties : Properties = null 
  
  private var projectDirectory : String = null
  private var trainDirectory : String = null
  private var annotatorsDirectory : String = null
  private var applyDirectory : String = null
  private var applyResDirectory: String = null
  private var correctedDirectory : String = null
  private var destinationDirectory : String = null
  private var projectProperties: String = null
  
  private var serverPort: int = 0
  
  properties = new Properties
  var propsInStream: FileInputStream = null
  
  try 
  {
    propsInStream = new FileInputStream(Constants.propertiesFileName)
    properties.load(propsInStream)
  }
  catch 
  { 
    case (e: IOException) => println(" Could not read file " + Constants.propertiesFileName)
  } 
  finally
  {
    updateValues()
    if (propsInStream != null) propsInStream.close 
  }    
  
  def updateValues()
  {
    projectDirectory = Constants.defaultProjectsDirName + properties.getProperty("projectDirectory", Constants.projectDirectory).trim()
    trainDirectory = properties.getProperty("trainDirectory", Constants.trainDirectory).trim()
    annotatorsDirectory = properties.getProperty("annotatorsDirectory", Constants.annotatorsDirectory).trim()
    applyDirectory = properties.getProperty("applyDirectory", Constants.applyDirectory).trim()
    applyResDirectory = properties.getProperty("applyResDirectory", Constants.applyResDirectory).trim()
    correctedDirectory = properties.getProperty("correctedDirectory", Constants.correctedDirectory).trim();
    destinationDirectory = properties.getProperty("destinationDirectory", Constants.destinationDirectory).trim()

    projectProperties = properties.getProperty("projectProperties", Constants.defaultProjectProperties).trim
    
    addSlashes()
    
    serverPort = Integer.parseInt(properties.getProperty("port", Constants.defaultPort).trim())
  }
  
  private def addSlashes() =
  {
    if (trainDirectory.charAt(trainDirectory.length-1) != '/') trainDirectory += '/';
    if (applyDirectory.charAt(applyDirectory.length-1) != '/') applyDirectory += '/';
    if (applyResDirectory.charAt(applyResDirectory.length-1) != '/') applyResDirectory += '/';
    if (annotatorsDirectory.charAt(annotatorsDirectory.length-1) != '/') annotatorsDirectory += '/';
    if (correctedDirectory.charAt(correctedDirectory.length-1) != '/') correctedDirectory += '/';
    if (destinationDirectory.charAt(destinationDirectory.length-1) != '/') destinationDirectory += '/';
  }
  
  def getProperties() : Properties =
  {
    return properties
  }
  
  def setProperties(prop : Properties)
  {
    if (prop == null) return
    
    val names : Enumeration[String] = prop.propertyNames().asInstanceOf[Enumeration[String]]
    
    while(names.hasMoreElements())
    {
	val name = names.nextElement()
        properties.setProperty(name, prop.getProperty(name))
    }
    
    properties.store(new FileOutputStream(Constants.propertiesFileName), "")
      
    updateValues()
  }
  
  def getProjectDirectory() : String =
  {
    return projectDirectory
  }

  def getTrainDirectory() : String =
  {
    return projectDirectory + trainDirectory
  }

  def getAnnotatorsDirectory() : String =
  {
    return projectDirectory + annotatorsDirectory
  }
  
  def getApplyDirectory() : String =
  {
    return projectDirectory + applyDirectory
  }
 
  def getApplyResDirectory = projectDirectory + applyResDirectory
  
  def getCorrectedDirectory() : String =
  {
    return projectDirectory + correctedDirectory
  }
  
  def getDestinationDirectory() : String =
  {
    return projectDirectory + destinationDirectory
  }
  
  def getServerPort() : int = 
  {
    return serverPort
  }
  
  def getProjectProperties = projectProperties
  
  def store()
  {
    val p : Properties = new Properties
    p.load(new FileInputStream(Constants.propertiesFileName))
    
    p.setProperty("projectDirectory", projectDirectory)
    p.setProperty("trainDirectory", trainDirectory)
    p.setProperty("annotatorsDirectory", annotatorsDirectory)
    p.setProperty("applyDirectory", applyDirectory)
    p.setProperty("applyResDirectory", applyResDirectory)
    p.setProperty("correctedDirectory", correctedDirectory)
    p.setProperty("destinationDirectory", destinationDirectory)
    
    p.setProperty("projectProperties", projectProperties)
    
    properties.store(new FileOutputStream(Constants.propertiesFileName), "")
  }
  
  
}
