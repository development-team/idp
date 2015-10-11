package ubique.idp.batchprocessor.normalisation;

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

import ubique.idp.batchprocessor.prolog.adapter.Mapping2PredicatesAdapter
import ubique.idp.batchprocessor.xml.XMLRunner
import ubique.idp.batchprocessor.constants.Constants
import ubique.idp.processing.state.State

/** 
  *  Runs normalisation according to Predicates rules.
  */
object Normalizer {
  type updateFunction = State => Unit
  var normEnding = Constants.normalisedResExtension
  var state: State = new State(1, "Normalizing  result xml files")
  var updaters: Buffer[updateFunction] = new ListBuffer
  
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
  * @param dirname - String name of directory that contains XML files to normalise
  */
  def apply (rules: Predicate, dirname: String, normalisedResExtension: String) {
    val dirFile = new File(dirname)
    var counter = 0.0
    var out: OutputStreamWriter = null;
    if (dirFile.isDirectory()) {
      val files = dirFile.listFiles.filter(aFile => !aFile.isDirectory && aFile.getName.endsWith(".xml"))
      val total = files.size
      if (!files.isEmpty) {
        files.filter(aFile => { !aFile.getName.endsWith(normalisedResExtension) }).map[Unit](aFile => {
          var filename = aFile.getName
          if (Constants.DEBUG) println (" ** " + filename + " start processing ")
          val root = (new XMLRunner(dirname, "", "")).forAllNodes(nodeNormalize(rules), dirname, filename)
          // saving files
          if (Constants.DEBUG) { 
            filename = aFile + normalisedResExtension
            // println("** " + root)
            println (" ** " + filename + " done  processing")
          }
          XML.saveFull(filename, root, Constants.defaultEncoding, true, null)
          counter = counter + 1
          this.state.setPercent(counter / total * 100.0)
          notifyObservers
        })
      }
    } else {
      throw new IllegalArgumentException(" Specified directory name " + dirname + " does not point to directory")
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
      //TODO I have to change this to object when XMLRunner will be an object.
      res = (new XMLRunner("", "", "")).recreateElemWithText(aNode, text)
    }
    res
  }

}
