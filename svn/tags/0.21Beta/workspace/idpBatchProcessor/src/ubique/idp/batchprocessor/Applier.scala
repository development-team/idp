package ubique.idp.batchprocessor;

import java.io.File
import java.io.IOException

import scala.collection.mutable.ArrayBuffer

import edu.cmu.minorthird.text.TextLabels
import edu.cmu.minorthird.text.TextLabelsLoader
import edu.cmu.minorthird.text.learn.ExtractorAnnotator

import ubique.idp.processing.state.State
import ubique.idp.batchprocessor.normalization.Normalizer

/**
* Applies saved annotators to plain text.
*/
class Applier(labelsDir:String, annotatorsDir: String, applyResDir: String, destinationDir:String, 
    annotatorsExtension: String, normalizedResExtension: String, 
    namespaceBindingFilename: String, seqFilename: String, rulesFilename: String, rootElementName: String)
  extends AnnotatorsLoder(labelsDir, annotatorsDir, annotatorsExtension, 
      namespaceBindingFilename, seqFilename, rulesFilename) {
  
  private var annLabels: TextLabels = null
  private var annLabelsFiles: ArrayBuffer[File] = new ArrayBuffer()
  this.state = new State(2, "Loading annotators")
  
  /** Annotate the labels set */
  private def annotate(ann: ExtractorAnnotator): TextLabels = {
    if (annLabels == null) ann.annotatedCopy(this.labels)
    else ann.annotatedCopy(this.annLabels)
    // ann.annotatedCopy(this.labels)
  }
  /** 
  *  Save the result of annotation in destinationDirectory and in applyResDirectory.
  * @param annLabels - labels(annotated xml files) to save
  */
  private def save(annLabels: TextLabels) {
    val destinationFile = new File(this.destinationDir)
    val applyResFile = new File(this.applyResDir)
    // put files in applyRes directory (applyRes)
    if (!(applyResFile exists) || !(applyResFile isDirectory)) applyResFile mkdirs()
    new TextLabelsLoader().saveDocsWithEmbeddedTypes( annLabels, applyResFile, 
        this.namespaceBinding.uri, this.namespaceBinding.prefix, rootElementName )
    // put files in res directory (destination)
    if (!(destinationFile exists) || !(destinationFile isDirectory)) destinationFile mkdirs()
    new TextLabelsLoader().saveDocsWithEmbeddedTypes( annLabels, destinationFile, 
        this.namespaceBinding.uri, this.namespaceBinding.prefix, rootElementName )
  }
  
  /**  
  *   Runs annotators from annotators directory on specified apply directory texts. 
  */
  def run() {
    val prefix = this.namespaceBinding.prefix + ":"
    this.state.incrementStage("Annotating texts")
    var counter = 0.0
    val total = this.anns.size
    this.anns.map[Unit] ((annTuple) => {
      log info ("Annotating "+prefix+annTuple._1)
      counter = counter + 1
      this.state.setPercent(counter / total * 100.0)
      annLabels = this.annotate(annTuple._2)
    })
    this.state.incrementStage("Saving annotated texts")
    save(annLabels)
    this.state.setPercent(100.0)
  }
  
  /**
    * Run Normalisation on learned rules.
    */
  def normalize {
    log info ("Normalizer started")
    log info ("Rules are " + substRules)
    this.state.incrementStage("Normalizing  result xml files")
    if (this.substRules != null) {
      Normalizer setState this.state
      Normalizer(this.substRules, this.applyResDir, this.destinationDir, this.normalizedResExtension)      
    }
    this.state.setPercent(100.0)
    log.info("Normalizer is done")
  }
}
