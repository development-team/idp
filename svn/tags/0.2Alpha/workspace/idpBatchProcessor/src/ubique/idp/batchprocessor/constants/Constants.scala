package ubique.idp.batchprocessor.constants;

/** Constants singleton */
object Constants {
  val propertiesFileName = "batchProcessor.properties"
  val defaultEncoding = "UTF-8"
  val DEBUG = true
  // evaluation mode changes predictedTagPrefix_prefix_label instead of just label
  //set the label used for annotations produced by the learner
  val EVALUATION_MODE = false
  // default properties
  // xml training document settings
  val namespace = "foo" 
  val rootElementName = "doc"
  val predictedTagPrefix = "_predicted"
  val substAttributeName = "sRule"
  //"gdc.icl.kazan.ru/hurricane"
 
  // directories
  val projectDirectory = "./prj" 
  val trainDirectory = "/train"
  val annotatorsDirectory = "/annotators"
  val applyDirectory = "/apply"
  val destinationDirectory = "/res"
    
  // extensions and filenames
  val annotatorsExtension = ".ann.sjobj"
  val treeFilename = "tree.xml"
  val namespaceBindingFilename = "nb.sjobj"
  val seqFilename = "seq.sjobj"
  val substRulesFilename = "c45rule.sjobj"
  val normalisedResExtension = ".norm.xml"
}
