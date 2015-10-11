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

import scala.io.Source
import scala.concurrent.ops._

import idp.sandBox.server.constants.Constants
import idp.sandBox.models.TextFileInfo
import idp.sandBox.server.stream.RemoteStream
import idp.sandBox.server.managers.SettingsManager

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
  
  val DEBUG = false
  
  var svnDirectory = ""
  var trainComplete = false
  var projectDirectoryFile:File = null
  
  var namespace = ""
  var annotatorsExtension = ""
  var normalisedResExtension = ""
  var treeFilename = ""
  var namespaceBindingFilename = ""
  var seqFilename = ""
  var predictedTagPrefix = ""

  //var trainDirectory = ""
  //var annotatorsDirectory = ""
  //var correctedDirectory = ""
  //var applyDirectory = ""
  //var destinationDirectory = ""
  //var projectDirectory = ""
  var hideDirectory = ""
    
  var substAttributeName = ""
  var substRulesFilename = ""
  var rootElementName = ""
    
  val settingsManager = new SettingsManager()
    
  ////////////////////////////////////////////////////////////////////////
  // console strings
  //val consoleQueue : ArrayList = null
  var drainedQueue : ArrayList[String] = null
  var queue : Queue = null 
  ////////////////////////////////////////////////////////////////////////
    
  // constructor 
  // reading project properties
  val props = new Properties()
  var propsInStream: FileInputStream = null
  try {
    propsInStream = new FileInputStream(Constants.propertiesFileName)
    props.load(propsInStream)
  } catch { 
    case (e: IOException) => if (DEBUG) println(" Could not read file " + Constants.propertiesFileName) 
  } finally {
    svnDirectory = props.getProperty("svnDirectory", Constants.svnDirectory).trim()
    namespace = props.getProperty("namespace", Constants.namespace).trim()
    annotatorsExtension = props.getProperty("annotatorsExtension", Constants.annotatorsExtension).trim()
    normalisedResExtension = props.getProperty("normalisedResExtension", Constants.normalisedResExtension).trim()
    treeFilename = props.getProperty("treeFilename", Constants.treeFilename).trim()
    namespaceBindingFilename = props.getProperty("namespaceBindingFilename", Constants.namespaceBindingFilename).trim()
    seqFilename = props.getProperty("seqFileName", Constants.seqFilename).trim()
    predictedTagPrefix = props.getProperty("predictedTagPrefix", Constants.predictedTagPrefix).trim()
    //projectDirectory = props.getProperty("projectDirectory", Constants.projectDirectory).trim()
    //trainDirectory = projectDirectory + props.getProperty("trainDirectory", Constants.trainDirectory).trim()
    //annotatorsDirectory = projectDirectory + props.getProperty("annotatorsDirectory", Constants.annotatorsDirectory).trim()
    //applyDirectory = projectDirectory + props.getProperty("applyDirectory", Constants.applyDirectory).trim()
    //correctedDirectory = projectDirectory + props.getProperty("correctedDirectory", Constants.correctedDirectory).trim();
    //destinationDirectory = projectDirectory + props.getProperty("destinationDirectory", Constants.destinationDirectory).trim()
    substAttributeName = props.getProperty("substAttributeName", Constants.substAttributeName).trim()
    substRulesFilename = props.getProperty("substRulesFilename", Constants.substRulesFilename).trim()
    rootElementName = props.getProperty("rootElementName", Constants.rootElementName).trim()
    //thisPort = Integer.parseInt(props.getProperty("port", Constants.defaultPort).trim())
    if (propsInStream != null) propsInStream.close 
  }
  
  def isTrainCompleted () = this.trainComplete
  
  def getState: State = {
    this.state
  }
  
  def receiveMessage(in: String) {
    println(in)
  }
  
  /** Method to provide access to training facilities of the trainer */
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
    val d1:Date = new Date;
    this.state = new State(3, "Collecting tag names")
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
    Decorate (" * Trainer is done")
    this.state.setComplete(true)
    this.trainComplete = true

    // TODO: create "train" file
  }
  
  def Decorate(line: String) {
    Console println line
  }
  
  /** Method to provide access to training facilities of the trainer */
  override def apply() {
    if (! this.trainComplete) 
      throw new RemoteException(" Can not start applying before training is done, please Train first")
    
    if (! (new File(settingsManager.getTrainDirectory)).isDirectory()) { 
      throw new IllegalArgumentException(" Can not find the directory: " + settingsManager.getTrainDirectory)
    }
    if (! (new File(settingsManager.getApplyDirectory)).isDirectory()) { 
      throw new IllegalArgumentException(" Can not find the directory: " + settingsManager.getApplyDirectory)
    }
    spawn{ applyRun }
  }
  
  /** Main method to apply annotatators */
  private def applyRun() {
    val d1:Date = new Date;
    this.state = new State(5, "Collecting tag names")
    if (!(new File(settingsManager.getAnnotatorsDirectory)).isDirectory() ) {
      throw new IllegalArgumentException(" Can not find the directory: " + settingsManager.getAnnotatorsDirectory 
      + " possibly you have not run the idp.Trainer ")
    }
    println ("idp.Applier started ") 
    println (settingsManager.getApplyDirectory + " " + settingsManager.getAnnotatorsDirectory)
    
    var destinationDirectory : String = settingsManager.getDestinationDirectory()
    val a = new Applier(settingsManager.getApplyDirectory, settingsManager.getAnnotatorsDirectory , destinationDirectory,
        annotatorsExtension, normalisedResExtension, namespaceBindingFilename, seqFilename, substRulesFilename, rootElementName)
    a.setState(this.state)
    a.load()
    a.run()
    Console println ((new Date).getTime - d1.getTime + " miliseconds spent.")
    Console println "* idp.Applier is done";
    this.state.setComplete(true)
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
    this.trainComplete = true
    
    if (!(new File(settingsManager.getAnnotatorsDirectory)).isDirectory() ) {
      throw new IllegalArgumentException(" Can not find the directory: " + settingsManager.getAnnotatorsDirectory 
      + " possibly you have not run the idp.Trainer ")
    }
    println ("idp.Applier started ") 
    println (settingsManager.getApplyDirectory + " " + settingsManager.getAnnotatorsDirectory)
    
    var destinationDirectory : String = settingsManager.getDestinationDirectory()
    val a = new Applier(settingsManager.getApplyDirectory, settingsManager.getAnnotatorsDirectory , destinationDirectory, 
        annotatorsExtension, normalisedResExtension, namespaceBindingFilename, seqFilename, substRulesFilename, rootElementName)
    a.setState(this.state)
    a.load()
    a.run()
    Console println ((new Date).getTime - d1.getTime + " miliseconds spent.")
    Console println "* idp.Applier is done";
    this.state.setComplete(true)
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
    
    println("Started: transferring contents of the file" + path)
    
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
  
  def getProjectTreeNodes(): DefaultMutableTreeNode = {
    println("Started tree nodes collection")
    this.projectDirectoryFile = new File(settingsManager.getProjectDirectory)
    val top: DefaultMutableTreeNode = new DefaultMutableTreeNode(
                        projectDirectoryFile.getName());
    addNodesOnDir(this.projectDirectoryFile, top);
    println("Tree nodes collection complete")
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
    }
    catch
    {
        case (e:IOException) => throw new RemoteException(e.getMessage())  
    }
    finally
        outputStream.close
        
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
  
  private def addNodesOnDir(dirFile:File, branch:DefaultMutableTreeNode) {
    if (dirFile.isDirectory()) {
      dirFile.listFiles().map[Unit]((aFile) => {
        if (aFile.getName().indexOf(this.svnDirectory) < 0) {
            if (aFile.isDirectory()) {
              val subBranch: DefaultMutableTreeNode = new DefaultMutableTreeNode(
                  new TextFileInfo(aFile));
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
    case (e: Exception) => throw new RemoteException("can't get inet address.")
    }
    println("this address=" + thisAddress + ",port=" + settingsManager.getServerPort());
    try {
      // create the registry and bind the name and object.
      registry = LocateRegistry.createRegistry(settingsManager.getServerPort());
      registry.rebind(Constants.defaultServerName, this);
    } catch {
    case (e: RemoteException) => throw e
    }
    println("Server is " + this)
  }
  
  def shutDownServer(key: long)
  {
    if (key == 1637171910328672563L)
    {
      registry.unbind(Constants.defaultServerName)
      spawn(System.exit(0))
    }
  }
  
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