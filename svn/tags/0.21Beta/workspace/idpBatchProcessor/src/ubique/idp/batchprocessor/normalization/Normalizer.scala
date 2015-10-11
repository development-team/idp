package ubique.idp.batchprocessor.normalization;

import java.io.File
import java.io.OutputStream
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.IOException

import scala.xml.Node
import scala.xml.XML
import scala.xml.PrettyPrinter
import scala.collection.immutable.Stack
import scala.collection.mutable.Buffer
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.ArrayBuffer

import jp.ac.kobe_u.cs.prolog.lang.{Predicate, PrologControl, VariableTerm, Term}

import org.apache.log4j.Logger

import ubique.idp.batchprocessor.prolog.adapter.Mapping2PredicatesAdapter
import ubique.idp.batchprocessor.xml.XMLRunner
import ubique.idp.batchprocessor.constants.Constants
import ubique.idp.processing.state.State

/** 
  *  Runs normalisation according to Predicates rules.
  */
object Normalizer {
  type updateFunction = State => Unit
  val normEnding = Constants.normalisedResExtension
  var state: State = new State(1, "Normalizing  result xml files")
  val updaters: Buffer[updateFunction] = new ListBuffer
  val log = Logger.getLogger(this.getClass())
  /** 
  *  Sets initial state.
  * @param state - initial State
  */ 
  def setState(state: State) = this.state = state
  /**
  *  Getter for current state
  * @return - current State
  */
  def getState = this.state
  
  /**
  *  Adds new update function to Observer update functions list.
  * @param updater - State => Unit 
  */
  def attachObserverUpdateFunction(updater: updateFunction) = updaters += updater
  
  /**
  *  Removes update function from update functions list.
  * @param updater - function Unit to remove
  */
  def detachObserverUpdateFunction(updater: updateFunction) = updaters -= updater
  
  /**
  *  Implementation of Observer Subject Notify method.
  */
  private def notifyObservers {
    if (this.updaters != null && !this.updaters.isEmpty) {
      for (val updater <- this.updaters) {
        updater(this.state)
      }
    }
  }
  
  /**  
  *  Runs normlisation over all XML files in directory 
  * @param rules - Predicate that contains root predicate of normalisation rules 
  * @param sourceDirname - String name of directory that contains source XML files to normalize
  * @param destnationDirname - String name of destination directory
  * @normalizedResExtension - file extension of result of normalization
  */
  def apply (rules: Predicate, sourceDirname: String, destinationDirname: String, normalizedResExtension: String) {
    val destDirFile = new File(destinationDirname)
    val srcDirFile = new File(sourceDirname)
    var counter = 0.0
    var out: OutputStreamWriter = null;
    if (srcDirFile.isDirectory()) {
      val files = srcDirFile.listFiles.filter(aFile => !aFile.isDirectory && aFile.getName.endsWith(".xml"))
      log info "files to process are " + files
      val total = files.size
      if (!files.isEmpty) {
        files.filter(aFile => {!aFile.getName.endsWith(normalizedResExtension) }).map[Unit](aFile => {
          var filename = aFile.getName
          if (Constants.DEBUG) log info (filename + " start processing ")
          //TODO: Correct to object
          val root = XMLRunner.forAllNodes(nodeNormalize(rules), sourceDirname, filename)
          // saving files
          if (Constants.DEBUG) { 
            filename = destinationDirname + Constants.slash + aFile.getName + normalizedResExtension
            // println("** " + root)
            log info (filename + " done  processing")
          }
          XML.saveFull(filename, root, Constants.defaultEncoding, true, null)
          counter = counter + 1
          this.state.setPercent(counter / total * 100.0)
          notifyObservers
        })
      }
    } else {
      throw new IllegalArgumentException(" Specified directory name " + srcDirFile + " does not point to directory")
    }
  }
  
  /**  Function to pass to XMLRunner.forAllNodes to perform learned normalisation 
  * @param rules Predicate root predicate that contans rules
  * @param aNode Node to normalise if there is rule to normalise it 
  * @param pathAccumulator Stack[String] that represents the XPath to the current node to check the rule 
  * @return Node recreated node if normalisation have been usedn
  */
  def nodeNormalize(rules: Predicate)(aNode: Node, pathAccumulator: Stack[String]): Node = {
    var res: Node = aNode
    // create Terms list from Stack[String]
    val resBuffer = new ArrayBuffer[String]();
    val pathTermList = Mapping2PredicatesAdapter.stack2TermList(pathAccumulator)
    /* if(Constants.DEBUG) {
      println("* " + aNode + " " + pathTermList + " " + pathAccumulator)
    } */
    var rule4Node = new VariableTerm()
    val p = new PrologControl()
    val argsP: Array[Term] = new Array[Term](2)
    argsP(0) = rule4Node
    argsP(1) = pathTermList
    p.setPredicate(rules, argsP)

    var r = p.call() 
    while (r) {
        println(rule4Node);
        resBuffer += rule4Node.toQuotedString()
        r = p.redo()
    }
    
    if (resBuffer.size > 0) {
      val rule4NodeString = resBuffer(0)
      val runner = ProblemFactory(rule4NodeString).
        getOrElse(throw new IllegalArgumentException("Could not find proper problem solver mapping to " + rule4NodeString))
      // TODO deal with parallel and save of value
      val text = runner.run(aNode.text)
      //TODO: I have to change this to object when XMLRunner will be an object.
      res = XMLRunner.recreateElemWithText(aNode, text)
    }
    res
  }

}
