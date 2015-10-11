package ubique.idp.batchprocessor;

import java.io.File
import java.io.IOException
import java.util.Date

import scala.Seq
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashSet
import scala.collection.mutable.Set
import scala.xml.NamespaceBinding

import edu.cmu.minorthird.util.IOUtil

import edu.cmu.minorthird.text.FancyLoader
import edu.cmu.minorthird.text.TextLabels
import edu.cmu.minorthird.text.MonotonicTextLabels
import edu.cmu.minorthird.text.SpanDifference
import edu.cmu.minorthird.text.learn.ExtractorAnnotator
import edu.cmu.minorthird.text.learn.experiments.SubTextBase
import edu.cmu.minorthird.text.learn.experiments.ExtractionEvaluation
import edu.cmu.minorthird.text.learn.experiments.MonotonicSubTextLabels
import edu.cmu.minorthird.classify.experiments.CrossValSplitter

import org.apache.log4j.Logger

class Evaluator (labelsDir:String, dir: String, annotatorsExtension: String, 
    namespaceBindingFileName: String, seq: String, rulesFilename: String) 
  extends ubique.idp.batchprocessor.AnnotatorsLoder(labelsDir, dir, annotatorsExtension, namespaceBindingFileName, seq, rulesFilename) {
  
  private val num_partitions = 5

  /** This duplicates evaluation part of edu.cmu.minorthird.ui.TestExtractor doMain method with adoption to batch tags processing*/
  private def evaluate (ann: ExtractorAnnotator, spanType: String) =  {
    
    var extractionEval = new ExtractionEvaluation()
    var i = 0
    var doSplit: Boolean = false
    var splitter = new CrossValSplitter(num_partitions)
    var subLabels: Array[MonotonicTextLabels] = null
    var testBase: SubTextBase = null
    var annFullLabels: TextLabels = null

    println ("labels " + this.labels)
    annFullLabels = ann.annotatedCopy(this.labels)
    log.info("Evaluating test partitions...");
    // split to partitions and evaluate
    if (num_partitions<2) doSplit=false
    if (doSplit){
      log.info("Creating test partition...");
      splitter.split(annFullLabels.getTextBase().documentSpanIterator());
      subLabels = new Array[MonotonicTextLabels](splitter.getNumPartitions());
      i = 0
      while (i<splitter.getNumPartitions()) {
        try {
          testBase = new SubTextBase(annFullLabels.getTextBase(), splitter.getTest(i) );
          subLabels(i) = new MonotonicSubTextLabels(testBase, annFullLabels.asInstanceOf[MonotonicTextLabels] );
        } catch {
          case ex: SubTextBase.UnknownDocumentException => // do nothing since testLabels[i] is already set
        }
        measurePrecisionRecall("TestPartition"+(i+1), subLabels(i), false, spanType, ann, extractionEval)
        i += 1
      }
    }
    measurePrecisionRecall("OverallTest", annFullLabels, true, spanType, ann, extractionEval)
    // sample statistics
    if (doSplit) extractionEval.printAccStats();
  }
  
  /** This is just a copy of measurePrecisionRecall from edu.cmu.minorthird.ui.TestExtractor */
  private def measurePrecisionRecall(tag: String, labels: TextLabels, isOverallMeasure: boolean, 
      spanType: String, ann: ExtractorAnnotator, extractionEval: ExtractionEvaluation) = {
    if (spanType!=null) {
      // only need one span difference here
      val sd:SpanDifference =
        new SpanDifference(
            labels.instanceIterator(ann.getSpanType()),
            labels.instanceIterator(spanType),
            labels.closureIterator(spanType) );
      System.out.println("\n" + tag+":");
      System.out.println(sd.toSummary());
      extractionEval.extend(tag,sd,isOverallMeasure);
    }
    else{
      // there is no option to work with properties at the moment
      Console.println ("There is no option to work with properties at the moment")
    }
  }
  
  /** Constructs prefix and runs through tags that was annotated */
  override def run() = {
    val prefix = this.namespaceBinding.prefix + ":" 
    this.anns.map[Unit] ((annTuple) => {
      println(prefix+annTuple._1)
      evaluate (annTuple._2, prefix+annTuple._1)	      
    })
  }
}
