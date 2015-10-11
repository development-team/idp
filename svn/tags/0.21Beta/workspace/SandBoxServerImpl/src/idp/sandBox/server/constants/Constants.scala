package idp.sandBox.server.constants;

object Constants {
  /** Default server parameters */
  val defaultPort = "3232"
  val defaultServerName = "SandBoxRMIServer"
  val defaultEncoding = "UTF-8"
  val propertiesFileName = "sandBoxServer.properties"
  val propertiesPathSeparator = "/"
  
  /** Default xml file structure */
  val predictedTagPrefix = ""
  val rootElementName = "doc"
  val substAttributeName = "sRule"
  val namespace = "foo"
  
  /** Default directory sructure */
  val defaultProjectsDirName = "prj"
  val defaultProjectProperties = ".properties"
  val trainDirectory = "/train"
  val annotatorsDirectory = "/annotators"
  val applyDirectory = "/apply"
  val applyResDirectory = "/applyRes"
  val correctedDirectory = "/corrected"
  val destinationDirectory = "/res"
  val projectDirectory = "/example_1"
  val hideDirectory = "annotators"
  val svnDirectory = ".svn"

  /** Default filenames and extensions */
  val annotatorsExtension = ".ann.sjobj"
  val treeFilename = "tree.xml"
  val namespaceBindingFilename = "nb.sjobj"
  val seqFilename = "seq.sjobj"
  val normalisedResExtension = ".norm.xml"
  val substRulesFilename = "c45rule.sjobj"
}