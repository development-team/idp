package ubique.idp.batchprocessor;

import java.io.File
import java.io.IOException

import scala.collection.mutable.ArrayBuffer

import edu.cmu.minorthird.text.TextLabels
import edu.cmu.minorthird.text.TextLabelsLoader
import edu.cmu.minorthird.text.learn.ExtractorAnnotator

import ubique.idp.processing.state.State
import ubique.idp.batchprocessor.normalisation.Normalizer

/**
* Applies saved annotators to plain text.
*/
class Applier(labelsDir:String, annotatorsDir: String, destinationDir:String, 
    annotatorsExtension: String, normalisedResExtension: String, 
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
  /** Save the result of annotation */
  private def save(annLabels: TextLabels) {
    val destinationFile = new File(this.destinationDir)
    if (!(destinationFile exists) || !(destinationFile isDirectory)) destinationFile mkdirs()
    new TextLabelsLoader().saveDocsWithEmbeddedTypes( annLabels, destinationFile, 
        this.namespaceBinding.uri, this.namespaceBinding.prefix, rootElementName )
  }
  
  /** Runs annotators from annotators directory on specified apply directory texts */
  def run() {
    val prefix = this.namespaceBinding.prefix + ":"
    this.state.incrementStage("Annotating texts")
    var counter = 0.0
    val total = this.anns.size
    this.anns.map[Unit] ((annTuple) => {
      println("** Annotating "+prefix+annTuple._1)
      counter = counter + 1
      this.state.setPercent(counter / total * 100.0)
      annLabels = this.annotate(annTuple._2)
    })
    this.state.incrementStage("Saving annotated texts")
    save(annLabels)
    this.state.setPercent(100.0)
    this.state.incrementStage("Normalising  result xml files")
    if (this.substRules != null) runSubst
    this.state.setPercent(100.0)
    println("* We are done")
  }
  
  def runSubst {
    // run through list of documents in res
    // form request to substRules
    // cache result in some storage (path, normClass, probability)
    Normalizer setState this.state
    Normalizer(this.substRules, this.destinationDir, this.normalisedResExtension)
  }
}
