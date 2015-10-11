package ubique.idp.backtracking;

import scala.collection.mutable.ArrayBuffer
import ubique.idp.normalisation.RulesRunner

class Backtracker(rulesRunner: RulesRunner) {
  
  type ModelFunc = String => Option[String]
  
  /** Getter for rulesRunner */
  def getRulesRunner = this.rulesRunner
  
  /** Tries to calculate right combination of parameters according to BackTracking algorithm.
    * @param aModelFuncs - the Array of possible Model functions for each model position
    * @returns the combinaton of ModelFunc that succeeded in RulesRunner
    */
  def apply(aModelFuncs: Array[Array[ModelFunc]]): Option[ArrayBuffer[ModelFunc]] = {
    
    applyRec(new ArrayBuffer[ModelFunc](), aModelFuncs)
  }

  private def applyRec(variableStates: ArrayBuffer[ModelFunc], aModelFuncs: Array[Array[ModelFunc]]): 
  Option[ArrayBuffer[ModelFunc]] = {
    val length = aModelFuncs.length
    if (length > 1) {
      val variableVariants = aModelFuncs(0)
      if (variableVariants.length >1) {
        var i = 0
        var resVariableStates = new ArrayBuffer[ModelFunc]()
        resVariableStates ++= variableStates
        while (i< variableVariants.length) {
          if (i>0) resVariableStates.remove(resVariableStates.length-1)
          resVariableStates += variableVariants.apply(i)
          val subRes = applyRec(resVariableStates, aModelFuncs.slice(1, aModelFuncs.length+1))
          subRes match {
            case None => i += 1
            case _ => subRes
          }
        }
        None
      } else if (length == 1){
        //the leaf of the recursion
        var i = 0
        var rulesRunnerRes = false
        var resVariableStates = new ArrayBuffer[ModelFunc]()
        resVariableStates ++= variableStates
        var arrayVariableStates: Array[ModelFunc] = new Array[ModelFunc](variableStates.length + 1)
        while(!rulesRunnerRes && i < variableVariants.length){
          if (i>0) resVariableStates.remove(resVariableStates.length-1)
          resVariableStates += variableVariants.apply(i)
          variableStates.copyToArray(arrayVariableStates, 0)
          rulesRunnerRes = rulesRunner.apply(arrayVariableStates)
          i += 1
        }
        if (rulesRunnerRes) Some(variableStates)
        else None
      } else throw new IllegalArgumentException("incoming Array is 0 size")
    } else throw new IllegalArgumentException("incoming Array is 0 size")
  }
}
