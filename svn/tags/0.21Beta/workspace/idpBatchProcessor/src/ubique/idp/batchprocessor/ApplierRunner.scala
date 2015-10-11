package ubique.idp.batchprocessor;

import java.util.{Date, Properties}
import java.io.{File, FileInputStream, IOException}
import org.apache.log4j.Logger
import ubique.idp.batchprocessor.constants.Constants

object ApplierRunner extends PropertiesReader {
  
  private var applyDirectory = ""
  private var applyResDirectory = ""
  private var destinationDirectory = ""
    
  private val log = Logger.getLogger(this.getClass())
  
  def main(args: Array[String]) {
    ApplierRunner.run(args)
  }
  
  /** Actually runs the an annotators
  * @param main method arguments 
  */
  def run(args : Array[String]) : Unit = {
    val d1:Date = new Date;
    
    this.loadReadProperties()
    if (args.size == 3) {
      this.applyDirectory = args.apply(0)
      this.annotatorsDirectory = args.apply(1)
      this.destinationDirectory = args.apply(2)
      if (! (new File(this.applyDirectory)).isDirectory()) { 
        throw new IllegalArgumentException(" Can not find the directory: " + args(0))
      }
      if (!(new File(annotatorsDirectory)).isDirectory() ) {
        throw new IllegalArgumentException(" Can not find the directory: " + args(1) + " possibly you have not run the Trainer ")
      }
    } else if (args.size > 0) {
      throw new IllegalArgumentException(" usage: applyDirectory annotatorsDirectory destinationDirectory ")
    }
    log info ("idp.Applier started ") 
    val a = new Applier(applyDirectory, annotatorsDirectory , applyResDirectory, destinationDirectory, 
        annotatorsExtension, normalisedResExtension ,
        namespaceBindingFilename, seqFilename, substRulesFilename, rootElementName)
    a.load
    log info "idp.Applier logged"
    a.run
    log info "idp.Applier applied"
    a.normalize 
    log info "idp.Applier normalized"
    log info ((new Date).getTime - d1.getTime + " miliseconds spent.")
    log info "* We are done"
    
  }
  
  /** Reads properties specific to apply */
  protected override def readProperties(props: Properties) {
    super.readProperties(props)
    this.applyDirectory = this.projectDirectory + props.getProperty("applyDirectory", Constants.applyDirectory).trim()
    this.applyResDirectory = this.projectDirectory + props.getProperty("applyResDirectory", Constants.applyResDirectory).trim()
    this.destinationDirectory = this.projectDirectory + props.getProperty("destinationDirectory", Constants.destinationDirectory).trim()
  }
}
