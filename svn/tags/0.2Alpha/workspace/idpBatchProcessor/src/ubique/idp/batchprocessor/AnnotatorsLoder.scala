package ubique.idp.batchprocessor;

import java.io.File
import java.io.IOException
import java.util.Date

import scala.Seq
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Set
import scala.collection.jcl.LinkedHashSet
import scala.xml.NamespaceBinding

import edu.cmu.minorthird.util.IOUtil
import edu.cmu.minorthird.text.{FancyLoader, TextLabels,MonotonicTextLabels,SpanDifference}
import edu.cmu.minorthird.text.learn.ExtractorAnnotator
import edu.cmu.minorthird.text.learn.experiments.SubTextBase
import edu.cmu.minorthird.text.learn.experiments.ExtractionEvaluation
import edu.cmu.minorthird.text.learn.experiments.MonotonicSubTextLabels
import edu.cmu.minorthird.classify.experiments.CrossValSplitter

import jp.ac.kobe_u.cs.prolog.lang.Predicate

import org.apache.log4j.Logger

import ubique.idp.processing.state.State

/** 
*  Loads annotators and labels from specified directory
* @param labelsDir - directory of labeled examples
* @param dir - directory of annotators
* @param annotatorsExtension - annotators filename extension
* @param namespaceBindingFileName - file name of file of namespaceBinding
*/
abstract class AnnotatorsLoder (labelsDir:String, annotatorsDir: String, 
    annotatorsExtension: String, namespaceBindingFilename: String, seqFilename: String, rulesFilename: String)
  extends LabelsLoader (labelsDir){

  type Element = Tuple4[String,String,String,String]
  type ElementNames = LinkedHashSet[Tuple4[String,String,String,String]]
  
  protected val log: Logger = Logger.getLogger(this.getClass())
  /** anns contanis Tuple of filename without extension plus annotator */
  protected var anns: ArrayBuffer[Tuple2[String,ExtractorAnnotator]] = new ArrayBuffer
  protected var namespaceBinding: NamespaceBinding = null
  protected var sequence: ElementNames = null
  protected var substRules: Predicate = null
  protected var state: State = new State(3, "Loading sequence")
  
  private val DEBUG: boolean = true
  
  def setState(state: State) = this.state = state
  def getState = this.state
  
  /** Loads annotators and returns the ArrayBuffer of annotators*/
    def load () = {
      val dirFile: File = new File(this.annotatorsDir)
      var ann: ExtractorAnnotator = null
      var fileName: String = ""
      val seqFile = new File(dirFile, seqFilename)
      val nbFile = new File(dirFile, namespaceBindingFilename)
      val rulesFile = new File(dirFile, rulesFilename)
      
      println (" Loading sequence ")
      this.state.incrementStage("Loading sequence")
      if ((new File(this.labelsDir)).isDirectory()) loadLabels()
      else throw new IllegalArgumentException( this.labelsDir + " is not a directory ");
      
      if (!dirFile.isDirectory) throw new IllegalArgumentException( this.annotatorsDir + " is not a directory ");
      
      if (seqFile.exists()) {
        this.sequence = new LinkedHashSet(IOUtil.loadSerialized(seqFile).asInstanceOf[java.util.LinkedHashSet[Element]])
      } else {
        throw new IllegalArgumentException(" could not find file " + seqFilename + " in " + annotatorsDir );
      }
      this.state.setPercent(100.0)
      
      println (" Loading namespace ")
      this.state.incrementStage("Loading namespace")
      if (nbFile.exists()) {
        this.namespaceBinding = IOUtil.loadSerialized(nbFile).asInstanceOf[NamespaceBinding]
      } else {
        throw new IllegalArgumentException(" could not find file " + namespaceBindingFilename + " in " + annotatorsDir );
      }
      this.state.setPercent(100.0)
      
      println (" Loading subst rules ")
      this.state.incrementStage("Loading subst rules(C 4.5)")
      if (rulesFile.exists()) {
        if (DEBUG) Console println (rulesFile)
        this.substRules = IOUtil.loadSerialized(rulesFile).asInstanceOf[Predicate]
      } else {
        //throw new IllegalArgumentException(" could not find file " + rulesFilename + " in " + annotatorsDir );
        this.substRules = null
      }
      this.state.setPercent(100.0)
      
      println(" Loading annotators ")
      this.state.incrementStage("Loading annotators")
      var counter = 0.0
      val total = sequence.size
      sequence.map[Unit]((aElement) => {
        //println ("* " + aElement)
        counter = counter + 1
        this.state.setPercent(counter/total*100.0)
        val aFile = new File(dirFile, aElement._2+this.annotatorsExtension)
        fileName = aFile.getName()
        if (aFile.exists()) {
          try {
            // fileName.substring(0, fileName.indexOf(this.annotatorsExtension))
            this.anns += new Tuple2(aElement._2,
                IOUtil.loadSerialized(aFile).asInstanceOf[ExtractorAnnotator])
                // println ("loded annotators " + this.anns)
          } catch {
          case ex: IOException => 
          throw new IllegalArgumentException("can't load annotator from " + aFile + ": "+ex);
          }
        } else throw new IllegalArgumentException("can't load annotator from " + aFile );
      })
    }

    protected def loadLabels(dir: String) = {
      this.labels = FancyLoader.loadTextLabels(dir).asInstanceOf[MonotonicTextLabels]
    }
    
    def run(): Unit
}
