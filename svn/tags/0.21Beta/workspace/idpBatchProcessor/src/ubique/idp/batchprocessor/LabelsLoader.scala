package ubique.idp.batchprocessor;

import edu.cmu.minorthird.text.FancyLoader
import edu.cmu.minorthird.text.MonotonicTextLabels

/** Loading labels class */
abstract class LabelsLoader (labelsDir: String){

  protected var labels: MonotonicTextLabels = null
  protected def loadLabels () = {
    this.labels = FancyLoader.loadTextLabels(this.labelsDir).asInstanceOf[MonotonicTextLabels]
  }
  def run(): Unit
  
}
