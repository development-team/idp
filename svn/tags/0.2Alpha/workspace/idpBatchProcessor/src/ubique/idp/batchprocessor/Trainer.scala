package ubique.idp.batchprocessor;

import java.util.Date;
import java.io.{File,IOException,Serializable, FileWriter, BufferedWriter}

import scala.xml.parsing.ConstructingParser;
import scala.xml.{Elem,Node,Document,NamespaceBinding}
import scala.collection.jcl.LinkedHashSet;
import scala.collection.mutable.{HashSet,ArrayBuffer,HashMap,Stack}
import scala.StringBuilder

import edu.cmu.minorthird.util.IOUtil;
import edu.cmu.minorthird.classify.{BatchClassifierLearner,StackedLearner}
import edu.cmu.minorthird.classify.experiments.CrossValSplitter;
import edu.cmu.minorthird.classify.sequential.CMMLearner;
import edu.cmu.minorthird.text.{Annotator,FancyLoader,LabeledDirectory,MonotonicTextLabels,TextLabels,EncapsulatingAnnotatorLoader}
import edu.cmu.minorthird.text.learn.{AnnotatorTeacher,SequenceAnnotatorLearner,TextLabelsAnnotatorTeacher,SpanFeatureExtractor,MixupCompatible}
import edu.cmu.minorthird.ui.Recommended;
import edu.cmu.minorthird.ui.Recommended.{SVMCMMLearner,VotedPerceptronLearner} 

import jp.ac.kobe_u.cs.prolog.lang.{Predicate, PrologControl, VariableTerm, Term, SymbolTerm, ListTerm}

import ubique.idp.batchprocessor.constants.Constants
import ubique.idp.batchprocessor.xml.XMLRunner
import ubique.idp.processing.state.State
import ubique.idp.batchprocessor.prolog.C45RulesFactory

/** Trains Annotators via SVMCMM learner */
class Trainer (labelsDir: String, namespace:String, destination:String, 
    annotatorsExtension:String, treeFilename:String, namespaceBindingFilename:String, seqFilename: String, 
    predictedTagPrefix: String, substAttributeName: String, substRulesFilename: String)
  extends LabelsLoader(labelsDir){
  
  /** The array buffer of tuples organized as LinkedHashMap but is serializable */
  type Annotators = ArrayBuffer[Tuple2[String,Annotator]]
  /** HashSet of Tuple of element prefix, name, qname, predicted tag prefix + element prefix + element name  */
  type ElementNames = LinkedHashSet[Tuple4[String,String,String,String]]
  
  private var nB:NamespaceBinding = null
  private var elementNames: ElementNames = null
  private val annotators: Annotators = new ArrayBuffer
  private var completeTree: Node = null
  private var rootPredicate: Option[Predicate] = null
  private var state = new State(3, "Collecting tag names")
  
  def getRootPredicate() = this.rootPredicate
  
  def setState(state: State) {
    this.state = state
  }
  
  def getState: State = state
  
  /** Collects tag names with specified namespace, <br/>
  * creates complete tree of XML elements that have been parsed, <br/>
  * creates predicates from XML elements of the tree that contains specified attribute.
  */ 
  def collectTagNames () = {
    val xmlR = new XMLRunner(labelsDir, namespace, predictedTagPrefix)
    this.elementNames = xmlR.collectTagNames()
    this.nB = xmlR.getNB
    this.completeTree = xmlR.getCompleteTree
    if (Constants.DEBUG) {println ("Complete tree " + this.completeTree) }
    // process complete tree in prediaces
    // store normalisation C 4.5 rules that determines normalisation methods
    // Predicate C4.5(null, null, {transformCalendar(null, null) {[doc,header,post_date], monthFull}}  -> null)
    this.rootPredicate = C45RulesFactory.createC45Predicate(substAttributeName, this.completeTree)
    
    // for test purposes
    if (Constants.DEBUG && this.rootPredicate.isDefined) {
      val path = new Stack[String]();
      path.push("doc");
      path.push("header");
      path.push("post_date");

      var lt: ListTerm = null;
      while (!path.isEmpty) {
        val symb = SymbolTerm.makeSymbol(path.pop());
        if (lt == null) {
          lt = new ListTerm(symb, SymbolTerm.makeSymbol("[]"));
        } else {
          lt = new ListTerm(symb, lt);
        }
      }
      var rule4Node = new VariableTerm()
      val p = new PrologControl()
      val argsP: Array[Term] = new Array[Term](2)
      argsP(0) = rule4Node
      argsP(1) = lt
      p.setPredicate(this.rootPredicate.get, argsP)
      
      var r = p.call() 
      println ("res " + r)
      while (r) {
        println(rule4Node);
        r = p.redo()
      }
      println("Rules predicate " + this.rootPredicate)
    }
  }
  
  /** Train the annotators and save them one by one */
  def train () = {
    val repositoryKey = this.labels
    var teacher: AnnotatorTeacher = null
    var fe:Recommended.TokenFE = null
    var annFilenames: ArrayBuffer[String] = new ArrayBuffer
    // Stacked learner stuff not actual at the moment
    /*val bCL: Array[BatchClassifierLearner] = new Array(2)
    bCL(0) = new SVMLearner
    bCL(1) = new VotedPerceptronLearner
    // need to be adjusted
    val splitter: CrossValSplitter = new CrossValSplitter(2)
    val stackedLearner: SequenceAnnotatorLearner = new SequenceAnnotatorLearner(
        new CMMLearner(
            new StackedLearner(bCL, new VotedPerceptronLearner(), splitter),1),
            new Recommended.TokenFE())*/
    /* val simpleLearnerSVM: SequenceAnnotatorLearner = new SequenceAnnotatorLearner(
        new CMMLearner(new SVMLearner,1),new Recommended.TokenFE())*/
        
    val simpleLearnerSVMCMM = new Recommended.SVMCMMLearner()
    val simpleLearnerVP: SequenceAnnotatorLearner = new SequenceAnnotatorLearner(
        new CMMLearner(new VotedPerceptronLearner,1),new Recommended.TokenFE())
    val learner = simpleLearnerSVMCMM
    val compoundString: StringBuilder = new StringBuilder()
    var counter = 0.0
    val total = elementNames.size
    loadLabels()
    //actualy train   
    elementNames.map[Unit] ((tag) => {
      // state processing
      counter = counter + 1
      state.setPercent(counter/total*100.0)
      // this is done for evaluation use predictedTagPrefix_prefix_label instead of just label
      // set the label used for annotations produced by the learner
      if (Constants.EVALUATION_MODE) learner.setAnnotationType(tag._4)
      else learner.setAnnotationType(tag._2)
      fe = new Recommended.TokenFE()
      //TODO Tried to implement other annotators influence
      /*if (annFilenames.size > 0) {
        annFilenames.map[Unit]((aFilename)=> {
          compoundString.append(aFilename + ";")
        })
        // println (" Compound string " + compoundString)
        fe.asInstanceOf[MixupCompatible].setAnnotatorLoader(new EncapsulatingAnnotatorLoader(compoundString.toString))
      }*/
      fe.setFeatureWindowSize(5)
      learner.setSpanFeatureExtractor(fe);
      
      teacher = new TextLabelsAnnotatorTeacher(this.labels, tag._3, "")
      // println ("learner " + learner)
      // println ("Teacher " + teacher)
      println ("Tag name " + tag._2)
      val ann = teacher.train(learner)
      this.annotators += (tag._2, ann)
      println(" Saving " + tag._2)
      annFilenames += saveOne(tag._2,ann)
      // println (" ann File names " + annFilenames)
    })
  }
  
  /** Saving one annotator */
  def saveOne(annTuple: Tuple2[String, Annotator]): String = {
    val dDFile = new File(destination)
    var fileName: String = ""
    var annFile: File = null
    // this should be moved to config
    
    if (!(dDFile exists) || !(dDFile isDirectory)) dDFile mkdirs
    
    try {
      fileName = annTuple._1+annotatorsExtension
      annFile = new File(dDFile,fileName) 
      IOUtil.saveSerialized(annTuple._2.asInstanceOf[Serializable],annFile);
      // save prefix to namespace mapping
    } catch {
    case e:IOException => new IllegalArgumentException("can't save to " + 
      destination + "/" + fileName +": "+e);
    }
    annFile.getPath()
  }
  
  /** Save complete tree, C4.5 predicates, namespace binding and sequence file */
  def saveRest() = {
    val dDFile = new File(destination)
    var fileName: String = ""
    var aFile: File = null
    if (!(dDFile exists) || !(dDFile isDirectory)) dDFile mkdirs
    
    try {
      //    save complete tree learned
      aFile = new File(dDFile, treeFilename)
      // IOUtil.saveSerialized(this.completeTree.toString(),aFile);
      val out = new BufferedWriter(new FileWriter(aFile))
      out.write(this.completeTree.toString)
      out.close
      
      // save C4.5 rules
      if (this.rootPredicate.isDefined) {
        aFile = new File(dDFile, substRulesFilename)
        IOUtil.saveSerialized(this.rootPredicate.get().asInstanceOf[Serializable], aFile)
      }

      // save prefix to namespace binding
      aFile = new File(dDFile, namespaceBindingFilename)
      IOUtil.saveSerialized(this.nB.asInstanceOf[Serializable],aFile)
      
      if (this.nB != null) {
        aFile = new File(dDFile, namespaceBindingFilename)
        IOUtil.saveSerialized(this.nB.asInstanceOf[Serializable],aFile)
      }
      
      // save sequence of annotation 
      aFile = new File(dDFile, seqFilename)
      IOUtil.saveSerialized(this.elementNames.underlying.asInstanceOf[Serializable], aFile)
      
    } catch {
      case e:IOException => new IllegalArgumentException("can't save to " + 
        destination + "/" + fileName +": "+e);
    }
  }
  
  def run(): Unit = {
    println(" Started collecting tag names for namespace " + this.namespace)
    this.state.incrementStage("Collecting tag names")
    collectTagNames()
    state.setPercent(100.0)
    
    println(" Started training ")
    this.state.incrementStage("Training annotators")
    train()
    println(" Started saving ")
    this.state.incrementStage("Saving annotators")
    saveRest()
    state.setPercent(100.0)
    
  }
}
