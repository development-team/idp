package idp.sandBox.server.impl;

import java.io.{File,IOException,OutputStream,PrintStream,DataInputStream,DataOutputStream,BufferedWriter, 
  FileWriter, FileInputStream,FileOutputStream,InputStreamReader,BufferedReader}
import java.net.InetAddress
import java.util.{Date,Properties,ArrayList}
import java.rmi.RemoteException
import java.rmi.registry.LocateRegistry
import java.rmi.registry.Registry
import java.rmi.MarshalledObject
import java.rmi.server.UnicastRemoteObject
import java.rmi.server.RemoteStub
import javax.swing.tree.DefaultMutableTreeNode

import org.apache.log4j.Logger

import scala.io.Source
import scala.concurrent.ops._

import idp.sandBox.server.constants.Constants
import idp.sandBox.models.TextFileInfo
import idp.sandBox.server.stream.RemoteStream
import idp.sandBox.server.managers.SettingsManager
import idp.sandBox.server.managers.ProjectSettingsManager

import ubique.idp.batchprocessor.{Applier,Trainer}

import ubique.idp.processing.state.State
import ubique.idp.processing.serverconsole.Queue
import ubique.idp.processing.serverconsole.IDPOutputStream

object SandBoxServer
  extends java.rmi.server.UnicastRemoteObject 
  with idp.sandBox.server.MessagingInterface {
  
  var thisAddress: String = ""
  //var thisPort: int = 0
  var registry: Registry = null // rmi registry for lookup the remote objects.
  var state = new State(8, "Collecting tag names")
  private val log = Logger.getLogger(this.getClass())
  
  val DEBUG = false
  
  var collectTagNamesComplete = false
  var projectDirectoryFile:File = null
  
  private var propertiesPathSeparator = Constants.propertiesPathSeparator
  private var svnDirectory = ""
  private var namespace = ""
  var annotatorsExtension = ""
  var normalisedResExtension = ""
  var treeFilename = ""
  var namespaceBindingFilename = ""
  var seqFilename = ""
  var predictedTagPrefix = ""
  var hideDirectory = ""
    
  var substAttributeName = ""
  var substRulesFilename = ""
  var rootElementName = ""
    
  private val settingsManager = SettingsManager
  private val projectSettingsManager = ProjectSettingsManager
    
  ////////////////////////////////////////////////////////////////////////
  // console strings
  //val consoleQueue : ArrayList = null
  var drainedQueue : ArrayList[String] = null
  var queue : Queue = null 
  ////////////////////////////////////////////////////////////////////////
    
  // constructor 
  //TODO Should Be moved to project properties
  val props = new Properties()
  var propsInStream: FileInputStream = null
  try {
    propsInStream = new FileInputStream(Constants.propertiesFileName)
    props.load(propsInStream)
  } catch { 
    case (e: IOException) => if (DEBUG) println(" Could not read file " + Constants.propertiesFileName) 
  } finally {
    propertiesPathSeparator = props.getProperty("propertiesPathSeparator", Constants.propertiesPathSeparator).trim()
    svnDirectory = props.getProperty("svnDirectory", Constants.svnDirectory).trim()
    namespace = props.getProperty("namespace", Constants.namespace).trim()
    annotatorsExtension = props.getProperty("annotatorsExtension", Constants.annotatorsExtension).trim()
    normalisedResExtension = props.getProperty("normalisedResExtension", Constants.normalisedResExtension).trim()
    treeFilename = props.getProperty("treeFilename", Constants.treeFilename).trim()
    namespaceBindingFilename = props.getProperty("namespaceBindingFilename", Constants.namespaceBindingFilename).trim()
    seqFilename = props.getProperty("seqFileName", Constants.seqFilename).trim()
    predictedTagPrefix = props.getProperty("predictedTagPrefix", Constants.predictedTagPrefix).trim()
    substAttributeName = props.getProperty("substAttributeName", Constants.substAttributeName).trim()
    substRulesFilename = props.getProperty("substRulesFilename", Constants.substRulesFilename).trim()
    rootElementName = props.getProperty("rootElementName", Constants.rootElementName).trim()
    if (propsInStream != null) propsInStream.close 
  }
  
  def getPropertiesPathSeparator(): String = propertiesPathSeparator
  
  def isTrainCompleted(): Boolean = {
    isTrainCompleted(settingsManager.getProjectDirectory())
  }
  
  def isTrainCompleted(project: String): Boolean = {
    val properties = projectSettingsManager.get(project, settingsManager.getProjectProperties)
    java.lang.Boolean.parseBoolean(properties.getProperty("trained", "false"))
  }

  override def isApplied(): Boolean = {
    isApplied(settingsManager.getProjectDirectory())
  }

  def isApplied(project: String): Boolean = {
    val properties = projectSettingsManager.get(project, settingsManager.getProjectProperties)
    java.lang.Boolean.parseBoolean(properties.getProperty("applied", "false"))
  }
  
  def getState: State = {
    this.state
  }
  
  def receiveMessage(in: String) {
    println(in)
  }
  
  /** Method to provide access to training facilities of the trainer. */
  override def train() {
    if (! (new File(settingsManager.getTrainDirectory)).isDirectory()) { 
      throw new IllegalArgumentException(" Can not find the directory: " + settingsManager.getTrainDirectory)
    }
    
    // move corrected files to train Directory
    println("move corrected")
    moveCorrected();
    spawn{ trainRun }
  }
  
  override def getConsoleStrings(): ArrayList[String] = {
    queue = Queue.getQueue()
    drainedQueue = queue.poll(5)
    return drainedQueue
  }
  
  /** Main method to train */
  private def trainRun() {
    // TODO this should be removed as soon as we will move to multiproject structure
    val project = settingsManager.getProjectDirectory
    val d1:Date = new Date;
    this.state = new State(3, "Collecting tag names")
    //  training and applying
    log info (" * idp.Trainer started ")
    log info (settingsManager.getTrainDirectory + " " + settingsManager.getAnnotatorsDirectory)
    try {
      val trainer = new Trainer(settingsManager.getTrainDirectory, namespace, settingsManager.getAnnotatorsDirectory, 
          annotatorsExtension, treeFilename, namespaceBindingFilename, seqFilename, predictedTagPrefix,
          substAttributeName, substRulesFilename)
       trainer.setState(this.state)
       trainer.run();
       log info ((new Date).getTime - d1.getTime)
       log info (" * Trainer is done")
       this.state.setComplete(true)
       projectSettingsManager.setProperty(project, settingsManager.getProjectProperties, "trained", "true")
    } catch {
      case e: Exception => {
        log info (" * Trainer produced an error: " + e.getMessage())
        this.state.setComplete(false)
        this.state.setStageName("Exception")
        projectSettingsManager.setProperty(project, settingsManager.getProjectProperties, "trained", "false")
        throw new RemoteException(e.getMessage())
      }
    }
  }
  
  /** Method to provide access to annotation facilities of the trainer. */
  override def apply() {
    val project = settingsManager.getProjectDirectory
    val trainComplete = java.lang.Boolean.parseBoolean(
        projectSettingsManager.getProperty(project, settingsManager.getProjectProperties, "trained", "false"))
    if (!trainComplete) {
      throw new RemoteException(" Can not start applying before training is done, please Train first")
    }
    
    if (!(new File(settingsManager.getAnnotatorsDirectory)).isDirectory() ) {
      throw new IllegalArgumentException(" Can not find the directory: " + settingsManager.getAnnotatorsDirectory 
      + " possibly you have not run the idp.Trainer ")
    }
    if (! (new File(settingsManager.getApplyDirectory)).isDirectory()) { 
      throw new IllegalArgumentException(" Can not find the directory: " + settingsManager.getApplyDirectory)
    }
    spawn{ applyRun }
  }
  
  /** 
  *  Main method to apply annotatators. 
  */
  private def applyRun() {
    //  TODO this should be removed as soon as we will move to multiproject structure
    val project = settingsManager.getProjectDirectory
    val d1:Date = new Date;
    this.state = new State(5, "Annotating texts")
    
    log info ("idp.Applier started ") 
    log info (settingsManager.getApplyDirectory + " " + settingsManager.getAnnotatorsDirectory)
    
    var destinationDirectory : String = settingsManager.getDestinationDirectory()
    val a = new Applier(settingsManager.getApplyDirectory, settingsManager.getAnnotatorsDirectory, 
        settingsManager.getApplyResDirectory, destinationDirectory,
        annotatorsExtension, normalisedResExtension, 
        namespaceBindingFilename, seqFilename, substRulesFilename, rootElementName)
    a.setState(this.state)
    a.load()
    a.run()
    log info ((new Date).getTime - d1.getTime + " miliseconds spent.")
    log info ("idp.Applier is done");
    this.state.setComplete(true)
    projectSettingsManager.setProperty(project, settingsManager.getProjectProperties, "applied", "true")
  }
  
  
  //TODO move to project base
  override def trainApply() {
    
    if (! (new File(settingsManager.getTrainDirectory)).isDirectory()) { 
      throw new IllegalArgumentException(" Can not find the directory: " + settingsManager.getTrainDirectory)
    }
    if (! (new File(settingsManager.getApplyDirectory)).isDirectory()) { 
      throw new IllegalArgumentException(" Can not find the directory: " + settingsManager.getApplyDirectory)
    }
    spawn { trainApplyRun }
  }
  
 
  private def moveCorrected() {
    val dir = new File(settingsManager.getCorrectedDirectory)
    if (!(dir).isDirectory()) {
      dir.mkdirs()
      //throw new IllegalArgumentException(" Can not find the directory: " + settingsManager.getCorrectedDirectory)
    }
   
    val directory = new File(dir.getCanonicalPath());
    val files : Array[String] = directory.list;
       
    for (i <- 0.to(files.length-1)) {
        val filename = settingsManager.getCorrectedDirectory+files(i);
        val new_filename = settingsManager.getTrainDirectory+files(i);
        println(filename)
        println(new_filename)
	val f = new File(filename);
        val old = new File(new_filename)
        if (old.exists()) old.delete()
        f.renameTo(old);
        f.delete()
    }
  }
                
  //TODO move to project base
  override def reTrainApply() {

    // check
    if (! (new File(settingsManager.getTrainDirectory)).isDirectory()) { 
      throw new IllegalArgumentException(" Can not find the directory: " + settingsManager.getTrainDirectory)
    }
    if (! (new File(settingsManager.getApplyDirectory)).isDirectory()) { 
      throw new IllegalArgumentException(" Can not find the directory: " + settingsManager.getApplyDirectory)
    }
 
    // move corrected files to train Directory
    println("move corrected")
    moveCorrected();
    
    // spawn (create new thread for trainApplyRun method)
    spawn { trainApplyRun }
  }
  
  /** Main method to train and apply */
  private def trainApplyRun() {
    //TODO should be removed as soon as we move to multiproject structure
    val project = this.settingsManager.getProjectDirectory
    val d1:Date = new Date;
    this.state = new State(8, "Collecting tag names")
    //  training and applying
    println (" * idp.Trainer started ")
    println (settingsManager.getTrainDirectory + " " + settingsManager.getAnnotatorsDirectory)
    val trainer = new Trainer(settingsManager.getTrainDirectory, namespace, settingsManager.getAnnotatorsDirectory, 
        annotatorsExtension, treeFilename, namespaceBindingFilename, seqFilename, predictedTagPrefix,
        substAttributeName, substRulesFilename)
    trainer.setState(this.state)
    trainer.run();
    Console println ((new Date).getTime - d1.getTime)
    Console println " * Trainer is done"
    this.projectSettingsManager.setProperty(project,  settingsManager.getProjectProperties, "trained", "true")
    
    if (!(new File(settingsManager.getAnnotatorsDirectory)).isDirectory() ) {
      throw new IllegalArgumentException(" Can not find the directory: " + settingsManager.getAnnotatorsDirectory 
      + " possibly you have not run the idp.Trainer ")
    }
    println ("idp.Applier started ") 
    println (settingsManager.getApplyDirectory + " " + settingsManager.getAnnotatorsDirectory)
    
    var destinationDirectory : String = settingsManager.getDestinationDirectory()
    val a = new Applier(settingsManager.getApplyDirectory, settingsManager.getAnnotatorsDirectory, 
        settingsManager.getApplyResDirectory, destinationDirectory, 
        annotatorsExtension, normalisedResExtension, namespaceBindingFilename, seqFilename, substRulesFilename, rootElementName)
    a.setState(this.state)
    a.load()
    a.run()
    Console println ((new Date).getTime - d1.getTime + " miliseconds spent.")
    Console println "* idp.Applier is done";
    this.state.setComplete(true)
    projectSettingsManager.setProperty(project, settingsManager.getProjectProperties, "applied", "true")  
  }
  
  override def getFileContentByAbsolutePath(path: String): String = {
    
    val aFile = new File(path)
    println("Started: transferring contents of the file - " + path)
    if (!aFile.exists()) throw new RemoteException (" Could not find file " + path)
    val buffer: StringBuilder = new StringBuilder()

    // if it's a folder then we return empty string
    if (aFile.isDirectory() == true) {
      return buffer.toString()
    }
    
    val fileStream = new FileInputStream(aFile)
    val inputStreamReader = new InputStreamReader(fileStream)
    val bufReader = new BufferedReader(inputStreamReader)
    while (bufReader.ready()) {
      var aLine : String = bufReader.readLine() + "\n"
      buffer.append(aLine)
    }
    
    bufReader.close()
    
    if (DEBUG) println (" buffer " + buffer)
    println (" Contents transferr complete")
    buffer.toString()
  }
  
  override def getFileContentByRelativePath(path: String): String = {
   
    val aFile = new File(settingsManager.getProjectDirectory + path)
    
    log.info("Started: transferring contents of the file" + path)
    
    if (!aFile.exists()) throw new RemoteException (" Could not find file " + path)
    
    val buffer: StringBuilder = new StringBuilder()
    
    val fileStream = new FileInputStream(aFile)
    val inputStreamReader = new InputStreamReader(fileStream)
    val bufReader = new BufferedReader(inputStreamReader)
    while (bufReader.ready()) {
      var aLine : String = bufReader.readLine() + "\n"
      buffer.append(aLine)
    }
    
    bufReader.close()
    
    if (DEBUG) println (" buffer " + buffer)
    println (" Contents transferr complete")
    buffer.toString()
  }
  
  def saveFileContentByRelativePath(path: String, content: String) = {
    val aFile = new File(settingsManager.getProjectDirectory + path)
    println("Started: transferring contents of the file" + path)
    if (!aFile.exists()) println(" Overwriting file " + path)
    val out: BufferedWriter = new BufferedWriter(new FileWriter(aFile))
    try {
      out.write(content);
      out.close();
    } catch {
      case (e:IOException) => throw new RemoteException(e.getMessage())
    } finally out.close
  }
  
  def sendCorrected(filename: String, content: String) = {
    val aFile = new File(settingsManager.getCorrectedDirectory)
    if (!aFile.exists()) aFile.mkdirs()
    val changedFile = new File(settingsManager.getCorrectedDirectory + filename)
    println("Started: saving changes of the file " + settingsManager.getCorrectedDirectory + filename)
    val out: BufferedWriter = new BufferedWriter(new FileWriter(changedFile))
    try {
      out.write(content);
      out.close();
    } catch {
      case (e:IOException) => throw new RemoteException(e.getMessage())
    } finally out.close
  }
  
  def sendXMLTreeFile(content: String) =
  {
    val aFile = new File(settingsManager.getAnnotatorsDirectory)
    if (!aFile.exists()) aFile.mkdirs()
    val treeFile = new File(settingsManager.getAnnotatorsDirectory + "/tree.xml")
    println("Started: saving changes of the file " + settingsManager.getAnnotatorsDirectory + "/tree.xml")
    val out: BufferedWriter = new BufferedWriter(new FileWriter(treeFile))
    try {
      out.write(content);
      out.close();
    } catch {
      case (e:IOException) => throw new RemoteException(e.getMessage())
    } finally out.close
  }
  /**
  *  Collects directory structure in DefaultMutableTreeNode. 
  */
  def getProjectTreeNodes(): DefaultMutableTreeNode = {
    log.info("Started tree nodes collection")
    this.projectDirectoryFile = new File(settingsManager.getProjectDirectory)
    val top: DefaultMutableTreeNode = new DefaultMutableTreeNode(projectDirectoryFile.getName())
    addNodesOnDir(this.projectDirectoryFile, top)
    log.info("Tree nodes collection complete")
    //load project properties
    //TODO this could be not best place to load project properties, but I could not find better moment.
    this.loadProject
    return top
  }
  
  def uploadFile(relativePath: String, filename: String, content: Array[byte])
  {
    var path : String = relativePath
    
    var file = new File(settingsManager.getProjectDirectory + path + "/" + filename);
    var dir = new File(settingsManager.getProjectDirectory + path);
    
    println("Started: uploading file to " + settingsManager.getProjectDirectory + path + "/" + filename);
    
    var outputStream : FileOutputStream = null
    
    if (!dir.isDirectory())
    {
      val index : int = path.lastIndexOf("/")
      if (index != -1)
      {
        path = path.substring(0, index)
        println("new path: " + path)
        println("Is not directory, trying to upload to " + settingsManager.getProjectDirectory + path + "/" + filename)
        file = new File(settingsManager.getProjectDirectory + path + "/" + filename)
      }
    }
    
    outputStream = new FileOutputStream(file)
    
    try
    {
    	outputStream.write(content);
        outputStream.close
    } catch {
      case (e:IOException) => throw new RemoteException(e.getMessage())  
    } finally outputStream.close
        
    println("Uploading completed succesfully." );
  }
  
  def deleteFile(relativePath: String) : Integer =
  {
    val file = new File(settingsManager.getProjectDirectory + relativePath)
    
    println("Trying to delete file: " + settingsManager.getProjectDirectory + relativePath);
    
    if (file.exists() == false) 
    {
      println("Error: doesn't exist");
      return 1
    }
      
    if (file.isDirectory()) {
      // delete recursively  
      deleteFileRec(file)
    }
    else file.delete()
    
    return 0
  }
  
  /** Delete file directory recursively **/
  private def deleteFileRec(dir: File)
  {
    dir.listFiles().map[Unit]((aFile) => {
      if (aFile.isDirectory())
      {
        deleteFileRec(aFile)
      }
      aFile.delete()
    })
    dir.delete    
  }
 
  def createFolder(relativePath:String, foldername:String) : Boolean =
  {
    var path : String = relativePath
    
    var file = new File(settingsManager.getProjectDirectory + path + "/" + foldername);
    var dir = new File(settingsManager.getProjectDirectory + path);
    
    println("Started: creating file in " + settingsManager.getProjectDirectory + path + "/" + foldername);
    
    if (!dir.isDirectory())
    {
      val index : int = path.lastIndexOf("/")
      if (index != -1)
      {
        path = path.substring(0, index)
        println("new path: " + path)
        println("Is not directory, trying to upload to " + settingsManager.getProjectDirectory + path + "/" + foldername)
        file = new File(settingsManager.getProjectDirectory + path + "/" + foldername)
      }
    }

    System.out.println("Creating folder: " + path)
    return file.mkdir()
  }
  
  def setServerProperties(props:Properties)
  {
    settingsManager.setProperties(props)
  }
    
  def getServerProperties() : Properties =
  {
    return settingsManager.getProperties()
  }
  

  def setProjectProperties(props:Properties)
  {
    setProjectProperties(settingsManager.getProjectDirectory, props)
  }
  
  def setProjectProperties(project: String, props:Properties)
  {
    projectSettingsManager.setProperties(project, settingsManager.getProjectProperties, props)
  }
  
  
  private def addNodesOnDir(dirFile:File, branch:DefaultMutableTreeNode) {
    if (dirFile.isDirectory()) {
      dirFile.listFiles().map[Unit]((aFile) => {
        if (aFile.getName().indexOf(this.svnDirectory) < 0) {
            if (aFile.isDirectory()) {
              val subBranch: DefaultMutableTreeNode = new DefaultMutableTreeNode(
                  new TextFileInfo(aFile, this.projectDirectoryFile));
              branch.add(subBranch);
              addNodesOnDir(aFile, subBranch);
            } else {
              branch.add(new DefaultMutableTreeNode(new TextFileInfo(
                  aFile, this.projectDirectoryFile)))
            }
        }
      })
    }
  }
  
  /**
    * Start server method. 
    */
  private def startServer() {
    try {
      // get the address of this host.
      thisAddress = (InetAddress.getLocalHost()).toString();
    } catch {
      case (e: Exception) => throw new RemoteException("Can't get internet address.")
    }
    log.info("this address=" + thisAddress + ",port=" + settingsManager.getServerPort());
    try {
      // create the registry and bind the name and object.
      registry = LocateRegistry.createRegistry(settingsManager.getServerPort());
      registry.rebind(Constants.defaultServerName, this);
    } catch {
      case (e: RemoteException) => throw e
    }
    log.info("Server is " + this)
  }
  
  /** 
  *   Loads default project properties.
  */
  def loadProject(): Properties = {
    loadProject(settingsManager.getProjectDirectory())
  }
  
  /** 
  *   Loads project properties.
  * @param project - project name
  */
  def loadProject(project: String): Properties = {
    projectSettingsManager.get(project, settingsManager.getProjectProperties)
  }
  
  def shutDownServer(key: long)
  {
    if (key == 1637171910328672563L)
    {
      log.info("Server started storing projects settings")
      this.projectSettingsManager storeChanged settingsManager.getProjectProperties
      log.info("Server done storing projects settings")
      registry.unbind(Constants.defaultServerName)
      spawn(System.exit(0))
    }
  }
  
  def collectTagNames() {
    if (! (new File(settingsManager.getTrainDirectory)).isDirectory()) { 
      throw new IllegalArgumentException(" Can not find the directory: " + settingsManager.getTrainDirectory)
    }
    // This should be useful in multiuser environment
    // spawn { collectTagNamesRun }
    collectTagNamesRun
  }
  
  /** Main method to collect tag names */
  private def collectTagNamesRun() {
    val d1:Date = new Date;
    this.state = new State(1, "Collecting tag names")
    //  collecting tag names
    log.info("idp.Trainer started collecting tag names \n")
    log.info(settingsManager.getTrainDirectory + " " + settingsManager.getAnnotatorsDirectory + "\n")
    try {
      val trainer = new Trainer(settingsManager.getTrainDirectory, namespace, settingsManager.getAnnotatorsDirectory, 
          annotatorsExtension, treeFilename, namespaceBindingFilename, seqFilename, predictedTagPrefix,
          substAttributeName, substRulesFilename)
       trainer.setState(this.state)
       trainer.runCreateTree
       log.info((new Date).getTime - d1.getTime)
       log.info(" Trainer collected tag names in a tree")
       this.state.setComplete(true)
       this.collectTagNamesComplete = true
    } catch {
      case e: Exception => {
        log.error (" * Trainer produced an error: " + e.getMessage())
        this.state.setComplete(false)
        this.state.setStageName("Exception")
        this.collectTagNamesComplete = false
        throw new RemoteException(e.getMessage())
      }
    }
  }
  
  /**
  *  Runs Normalizer.
  */
  override def normalize() {
    //TODO must be removed when we move to multiproject
    val project = settingsManager.getProjectDirectory
    val applyResDir = settingsManager.getApplyResDirectory
    val applyComplete = java.lang.Boolean.parseBoolean(
        projectSettingsManager.getProperty(project, settingsManager.getProjectProperties, "applied", "false"))
    if (!applyComplete) { 
      throw new RemoteException(" Can not start normalizing before application is done, please Apply first")
    }
    if (!(new File(applyResDir)).isDirectory()) { 
      throw new IllegalArgumentException(" Can not find the directory: " + applyResDir)
    } 
    spawn{ normalizeRun }
  }
  
  /** 
   * Actually runs normalizer.
   */
   private def normalizeRun() {
    val d1:Date = new Date;
    this.state = new State(1, "Normalizing xml files")
    val applyResDir = settingsManager.getApplyResDirectory
    log.info("idp.Normalizer started at: ") 
    log.info(applyResDir)
  
    val destinationDirectory : String = settingsManager.getDestinationDirectory()
    
    val a = new Applier(settingsManager.getApplyDirectory, settingsManager.getAnnotatorsDirectory, 
        settingsManager.getApplyResDirectory, destinationDirectory,
        annotatorsExtension, normalisedResExtension, namespaceBindingFilename, seqFilename, substRulesFilename, rootElementName)
    a.setState(this.state)
    a.load
    a.normalize
    log info ((new Date).getTime - d1.getTime + " miliseconds spent.")
    log info ("idp.Normalizer is done");
    this.state.setComplete(true)
  }
  
  
  /**
   * Method to run class.
   */
  def main(args: Array[String]) {
   
    //////////////////////////////////////////////////////////////////////
    // change standart output
    val idp_output : IDPOutputStream = new IDPOutputStream(System.out)
    System.setOut(new PrintStream(idp_output))
    //////////////////////////////////////////////////////////////////////
    
    try {
      startServer()
    } catch {
    case (e:Exception) => {
      e.printStackTrace()
      System.exit(1)
    }
    }
  }
}