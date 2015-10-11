package idp.sandBox.server.constants;

object Constants {
    
  val defaultPort = "3232"
  val defaultProjectsDirName = "prj"
  val defaultServerName = "SandBoxRMIServer"
  val defaultEncoding = "UTF-8"
  val propertiesFileName = "sandBoxServer.properties"
    
  //default parameters
  val svnDirectory = ".svn"
  var namespace = "foo"
  var annotatorsExtension = ".ann.sjobj"
  val normalisedResExtension = ".norm.xml"
  var treeFilename = "tree.xml"
  var namespaceBindingFilename = "nb.sjobj"
  var seqFilename = "seq.sjobj"
  var predictedTagPrefix = ""

  var trainDirectory = "/train"
  var annotatorsDirectory = "/annotators"
  var applyDirectory = "/apply"
  var correctedDirectory = "/corrected"
  var destinationDirectory = "/res"
  var projectDirectory = "/example_1"
  var hideDirectory = "annotators"
    
  var substAttributeName = "sRule"
  var substRulesFilename = "c45rule.sjobj"
  var rootElementName = "doc"

}
