package ubique.idp.batchprocessor;

import java.util.Properties
import java.io.{FileInputStream, IOException}
import ubique.idp.batchprocessor.constants.Constants

class PropertiesReader {
  // xml document setup
  protected var namespace = ""
  protected var predictedTagPrefix = ""
  protected var substAttributeName = ""
  protected var rootElementName = ""
  
  // directories
  protected var projectDirectory = ""
  protected var annotatorsDirectory = ""
  
  // extensions and file names
  protected var annotatorsExtension = ""
  protected var normalisedResExtension = ""
  protected var substRulesFilename = ""
  protected var namespaceBindingFilename = ""
  protected var seqFilename = ""
  protected var treeFilename = ""
  
  /** Load and read properties */
  protected def loadReadProperties() {
    //  reading project properties
    val props = new Properties()
    val propsInStream = new FileInputStream(Constants.propertiesFileName);
    try {
      props.load(propsInStream)
      this readProperties(props)
      return
    } catch {
      case (e: IOException) => if (Constants.DEBUG) println(" Could not read file " + Constants.propertiesFileName) 
    } finally {
      propsInStream close
    }
  }
  
  /** Reads properties from Properties into variables */
  protected def readProperties(props: Properties) {
    this.namespace = props.getProperty("namespace", Constants.namespace).trim()
    this.predictedTagPrefix = props.getProperty("predictedTagPrefix", Constants.predictedTagPrefix).trim()
    this.substAttributeName = props.getProperty("substAttributeName", Constants.substAttributeName).trim()
    this.rootElementName = props.getProperty("rootElementName", Constants.rootElementName).trim()
    
    this.projectDirectory = props.getProperty("projectDirectory", Constants.projectDirectory).trim()
    this.annotatorsDirectory = this.projectDirectory + props.getProperty("annotatorsDirectory", Constants.annotatorsDirectory).trim()
    
    this.annotatorsExtension = props.getProperty("annotatorsExtension", Constants.annotatorsExtension).trim()
    this.normalisedResExtension = props.getProperty("normalisedResExtension", Constants.normalisedResExtension.trim())
    this.treeFilename = props.getProperty("treeFilename", Constants.treeFilename).trim()
    this.namespaceBindingFilename = props.getProperty("namespaceBindingFilename", Constants.namespaceBindingFilename).trim()
    this.seqFilename = props.getProperty("seqFileName", Constants.seqFilename).trim()
    this.substRulesFilename = props.getProperty("substRulesFilename", Constants.substRulesFilename).trim()

    return
  }
}
