package ubique.idp.batchprocessor;

import java.util.{Date, Properties}
import java.io.{File, FileInputStream, IOException}
import ubique.idp.batchprocessor.constants.Constants

object TrainerRunner extends PropertiesReader {
  
  private var trainDirectory = ""
      
  def main(args: Array[String]) {
    TrainerRunner.run(args)
  }
  
  /** Actually runs training process 
  * @param main method arguments
  */
  def run(args: Array[String]){
    // this should be moved ot config file or set via input paramenters
    val d1:Date = new Date;
    
    super.loadReadProperties()
    if (args.size == 3) {
      this.trainDirectory = args.apply(0)
      this.annotatorsDirectory = args.apply(1)
      this.namespace = args.apply(2)
      if (! (new File(trainDirectory)).isDirectory()) { 
        throw new IllegalArgumentException(" Can not find the directory: " + args(0))
      }
    } else if (args.size > 0) {
      throw new IllegalArgumentException(" usage: labelsDirectory annotatorsDirectory namespace  ")
    }
    println ("idp.Trainer started ") 
    val trainer = new Trainer(trainDirectory, namespace, annotatorsDirectory, annotatorsExtension, 
        treeFilename, namespaceBindingFilename, seqFilename, predictedTagPrefix,
        substAttributeName, substRulesFilename) 
    val annotators = trainer.run();
    Console println ((new Date).getTime - d1.getTime)
    Console println "* We are done"
  }
  
  /** Reads properties of the project */
  protected override def readProperties(props: Properties) {
    super.readProperties(props)
    this.trainDirectory = this.projectDirectory + props.getProperty("trainDirectory", Constants.trainDirectory).trim()
  }
}
