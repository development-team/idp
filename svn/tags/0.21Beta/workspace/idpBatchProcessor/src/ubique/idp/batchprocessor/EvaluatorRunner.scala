package ubique.idp.batchprocessor;

object EvaluatorRunner {
  import java.util.Date
  
  def main (args: Array[String]) :Unit = {
    val d1:Date = new Date;
    val labelsDirectory: String = "./jobs_10.xml/"
    val annotatorsDirectory: String = "./annotators";
    val annotatorsExtension = ".ann"
    val nb = "nb.sjobj"
    val seq = "seq.sjobj" 
    val rules = "c45.sjobj"
    val evaluator = new Evaluator(labelsDirectory, annotatorsDirectory, annotatorsExtension, nb, seq, rules)
    evaluator.load()
    evaluator.run() 
    Console println ((new Date).getTime - d1.getTime)
    Console println "* We are done"; 
  }

}
