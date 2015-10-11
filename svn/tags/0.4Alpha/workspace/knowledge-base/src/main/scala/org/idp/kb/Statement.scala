package org.idp.kb

import java.net.URI

import org.apache.log4j.Logger

import edu.stanford.nlp.trees.TreeGraphNode
import edu.stanford.nlp.trees.TypedDependency

import org.semanticweb.owl.model.{OWLIndividual, AddAxiom}

import org.idp.stanfordparser.adapter.ScalaAdapter
import org.idp.kb.model.Subject
import org.idp.kb.model.Object
import org.idp.kb.model.ObjectType
import org.idp.kb.model.linguistic.Word
import org.idp.kb.model.linguistic.Phrase

/*
 * Factory for the Statements production
*/
object Statement {
  val log = Logger.getLogger(this.getClass)

  // supplemental indexed words map 
  var words = Map[Int, Word]()

  def apply(text: String): Set[Statement] = {
    log info (" apply (" + text + ")")

    var statements = Set[Statement]()
    // gov -> statement
    var selfSubjects = Map[String, Statement]()
    var subjects = Map[String, Statement]()
    // dep -> statement
    var depSubjects = Map[String, Statement]()
    // gov -> statement
    var objects = Map[String, Statement]()
    // the collection of numbers
    var nubers = Map[String, Statement]()

    // td = TypedDependency
    val tdCollection = (new ScalaAdapter()).run(text)
    log info tdCollection

    val phrases = createPhrases(tdCollection)

    // list of subjects
    val tdSubjects = findTypedDependencyByRel(Constant.subject, tdCollection)
    for (val td <- tdSubjects) {
      val statement = new Statement(getWord(td.gov),
        new Subject(phrases(td)))
      subjects += td.gov.label.value -> statement
      depSubjects += td.dep.label.value -> statement
      statements += statement
    }

    // run through refences (dobj, prep_on ...)
    // TODO change to recursion
    for (val subjectTuple <- subjects) {
      val tdObjects = findTypedDependencyByGov(subjectTuple._1, tdCollection)
      // find leaves objecs
      for (val td <- tdObjects) {
        val objType = new ObjectType(td.reln.getShortName, td.reln.getLongName)
        val tdDepObjects = findTypedDependencyByGov(td.dep.label.value, tdCollection)
        for (val tdDep <- tdDepObjects) {
          val obj = new org.idp.kb.model.Object(phrases(tdDep), Some(objType))
          var statementObjects = subjectTuple._2.getObjects
          statementObjects = statementObjects ::: List(obj)
          subjectTuple._2.setObjects(statementObjects)
        }
      }
    }
    statements
  }

  private def checkSubject(subj: TreeGraphNode): Boolean = {
    subj.label.value == Constant.self
  }

  private def findTypedDependencyByRel(needle: String, tdCollection: java.util.Collection[TypedDependency]):
  List[TypedDependency] = {
    var res = scala.List[TypedDependency]()
    val it = tdCollection.iterator
    while (it hasNext) {
      val td = it.next
      if (td.reln.getShortName == needle) {
        res = res ::: List(td)
      }
    }
    res
  }

  private def findTypedDependencyByGov(needle: String, tdCollection: java.util.Collection[TypedDependency]):
  List[TypedDependency] = {
    var res = scala.List[TypedDependency]()
    val it = tdCollection.iterator
    while (it hasNext) {
      val td = it.next
      if (td.gov.label.value == needle) {
        res = res ::: List(td)
      }
    }
    res
  }

  private def createPhrases(tdCollection: java.util.Collection[TypedDependency]):
  Map[TypedDependency, Phrase] = {
    var res = Map[TypedDependency, Phrase]()
    val it = tdCollection.iterator
    while (it hasNext) {
      val td = it.next
      res += td -> new Phrase(getWord(td.gov), getWord(td.dep))
    }
    res
  }

  private def getWord(tgn: TreeGraphNode): Word = {
    words.getOrElse(tgn.label.index, {new Word(tgn.label.value)})
  }
}

/*
 * Statement(predicate) implementation.
 */
class Statement(verb: Word, subject: Subject) extends OWLSavable {
  val log = Logger.getLogger(this.getClass)

  def getVerb = verb

  def getSubject = subject

  var objects = scala.List[Object]()

  def getObjects = objects

  def setObjects(objs: List[Object]) = {this.objects = objs}

  override def toString = {
    var acc = this.verb + " ( " + this.subject + ", "
    this.objects.map(i => {acc += i + ", "})
    acc + " )"
  }

  private def objectOWLNames: String = {
    var acc = ""
    this.objects.map(i => acc += i.OWLName + Constant.delimiter)
    acc
  }

  override def save: OWLIndividual = {
    val vrb = verb.save;
    val sbj = subject.save;
    val statement = OWLSaveHelper.saveIndividual(OWLName, Constant.baseURI + Constant.localNamesPrefix + Constant.statementURIString)
    log info statement

    val hasSubject = Constant.dataFactory.getOWLObjectProperty(URI create (Constant.localNamesPrefix + Constant.hasSubjectURIString))
    val hasSubjectPropAssertion = Constant.dataFactory.getOWLObjectPropertyAssertionAxiom(statement, hasSubject, sbj)
    val addHasSubjectPropAddAxiom = new AddAxiom(Constant.ontology, hasSubjectPropAssertion)
    Constant.owlManager applyChange addHasSubjectPropAddAxiom
    log debug hasSubject

    val hasVerb = Constant.dataFactory.getOWLObjectProperty(URI create (Constant.localNamesPrefix + Constant.hasVerbURIString))
    val hasVerbPropAssertion = Constant.dataFactory.getOWLObjectPropertyAssertionAxiom(statement, hasVerb, vrb)
    val addHasVerbPropAddAxiom = new AddAxiom(Constant.ontology, hasVerbPropAssertion)
    Constant.owlManager applyChange addHasVerbPropAddAxiom
    log debug hasVerb

    // objects
    this.objects.map(i => {
      val obj = i save;
      val hasObject = Constant.dataFactory.getOWLObjectProperty(URI create Constant.localNamesPrefix + Constant.hasObjectURIString)
      val hasObjectPropAssertion = Constant.dataFactory.getOWLObjectPropertyAssertionAxiom(statement, hasObject, obj)
      val addHasObjectPropAddAxiom = new AddAxiom(Constant.ontology, hasObjectPropAssertion)
      Constant.owlManager applyChange addHasObjectPropAddAxiom
      log debug obj
    })

    statement
  }

  override def OWLName = Constant.statementURIString + Constant.classDelimiter +
          this.verb.OWLName + Constant.delimiter + this.subject.OWLName + Constant.delimiter + this.objectOWLNames
}
