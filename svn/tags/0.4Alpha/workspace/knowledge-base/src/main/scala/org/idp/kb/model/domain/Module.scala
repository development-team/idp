package org.idp.kb.model.domain

import java.net.URI
import org.semanticweb.owl.model.{OWLIndividual, AddAxiom}
import java.io.File
import org.idp.kb.{ModulesUtil, OWLSaveHelper, Util, Constant}
import org.apache.log4j.Logger

class Module(uriString: String) extends AnyDomain {
  val log = Logger.getLogger(this.getClass)

  def this(d: AnyDomain) = this (d.OWLURIString)

  def OWLURIString = uriString

  def getIndivid = individ

  def classURIString = Constant.moduleClassURIString

  var addedFields = Set[Field]()

  var removedFields = Set[Field]()

  private val methods: Set[Method] = {
    Util.findPropertyValue(individ,
      URI create (Constant.domainBase
              + Constant.localNamesPrefix
              + Constant.hasMethodURIString))
    match {
      case Some(indSet) => indSet.map(i => new Method(i.getURI.toString.substring(Constant.domainBase.length + Constant.localNamesPrefix.length)))
      case None => Set[Method]()
    }
  }

  def getMethods: Set[Method] = methods

  private val body: String = {
    val bodyTypedConstantSet = Util.findDataPropertyValueSet(individ,
      URI create (Constant.domainBase
              + Constant.localNamesPrefix
              + Constant.value))
    if (bodyTypedConstantSet.size > 0) {
      bodyTypedConstantSet.toList(0).getLiteral
    } else {
      ""
    }
  }

  def getBody: String = body

  private var fields: Set[Field] = {
    Util.findPropertyValue(individ,
      URI create (Constant.domainBase
              + Constant.localNamesPrefix
              + Constant.hasFieldURIString))
    match {
      case Some(indSet) => indSet.map(i => new Field(i.getURI.toString.substring(Constant.domainBase.length + Constant.localNamesPrefix.length)))
      case None => Set[Field]()
    }
  }

  def getFields = fields


  private var imports: Set[String] = {
    Util.findPropertyValue(individ,
      URI create (Constant.domainBase
              + Constant.localNamesPrefix
              + Constant.hasImportURIString))
    match {
      case Some(indSet) => indSet.map(i => {
        i.getURI.toString.substring(Constant.domainBase.length + Constant.localNamesPrefix.length)
      })
      case None => Set[String]()
    }
  }

  def getImports = imports

  private var modulePackages: Set[String] = {
    Util.findPropertyValue(individ,
      URI create (Constant.domainBase
              + Constant.localNamesPrefix
              + Constant.hasPackageURIString))
    match {
      case Some(indSet) => indSet.map(i => i.getURI.toString.substring(Constant.domainBase.length + Constant.localNamesPrefix.length))
      case None => Set[String]()
    }
  }

  def getPackage: String = {
    if (modulePackages.size > 0) {
      modulePackages.toList(0)
    } else {
      ""
    }
  }

  private var file: Option[File] = {
    Util.findPropertyValue(individ,
      URI create (Constant.domainBase
              + Constant.localNamesPrefix
              + Constant.hasFileURIString))
    match {
      case Some(indSet) => {
        if (indSet.size > 0) {
          val i = indSet.toList(0)
          val fileAddressSet = Util.findDataPropertyValueSet(i,
            URI create (Constant.domainBase
                    + Constant.localNamesPrefix
                    + Constant.addressURIString))
          if (fileAddressSet.size > 0) {
            Some(new File(fileAddressSet.toList(0).getLiteral))
          } else {
            None
          }
        } else {
          None
        }
      }
      case None => None
    }
  }

  def getFile = file

  /**
   * Modules referenced from current via dependsOn object property.
   */
  private val modulesDepending: Set[Module] = {
    Util.findPropertyValue(individ,
      URI create (Constant.domainBase
              + Constant.localNamesPrefix
              + Constant.hasModuleURIString)) match {
      case Some(indSet) => indSet.map(i => {
        ModulesUtil.findModule(i.getURI) match {
          case Some(m) => m.asInstanceOf[Module]
          case None => {
            ModulesUtil.addModule(this)
            new Module(i.getURI.toString.substring(Constant.domainBase.length + Constant.localNamesPrefix.length))
          }
        }
      })
      case None => Set[Module]()
    }
  }

  /**
   * Modules that refenece current module via dpendsOn object property.
   */
  private val modulesIDepend: Set[Module] = {
    Util.findPropertyValueSet(individ,
      URI create (Constant.domainBase
              + Constant.localNamesPrefix
              + Constant.linkedToModuleURIString)).map(i => {
      ModulesUtil.findModule(i.getURI) match {
        case Some(m) => m.asInstanceOf[Module]
        case None => {
          ModulesUtil.addModule(this)
          new Module(i.getURI.toString.substring(Constant.domainBase.length + Constant.localNamesPrefix.length))
        }
      }
    })
  }

  def getModules: Set[Module] = modulesDepending ++ modulesIDepend

  private val constructors: Set[Method] = {
    Util.findPropertyValue(individ,
      URI create (Constant.domainBase
              + Constant.localNamesPrefix
              + Constant.hasConstructorURIString)) match {
      case Some(indSet) => indSet.map(i => new Method(i.getURI.toString.substring(Constant.domainBase.length + Constant.localNamesPrefix.length)))
      case None => Set[Method]()
    }
  }

  def getConstructors = constructors

  private val superClasses: Set[Module] = {
    Util.findPropertyValue(individ,
      URI create (Constant.domainBase
              + Constant.localNamesPrefix
              + Constant.extendsURIString)) match {
      case Some(indSet) => indSet.map(i => new Module(i.getURI.toString.substring(Constant.domainBase.length + Constant.localNamesPrefix.length)))
      case None => Set[Module]()
    }
  }

  def getSuperClasses = superClasses

  private val interfaces: Set[Module] = {
    Util.findPropertyValue(individ,
      URI create (Constant.domainBase
              + Constant.localNamesPrefix
              + Constant.implementsURIString)) match {
      case Some(indSet) => indSet.map(i => new Module(i.getURI.toString.substring(Constant.domainBase.length + Constant.localNamesPrefix.length)))
      case None => Set[Module]()
    }
  }

  def getInterfaces = interfaces

  private val isType: Set[Module] = {
    Util.findPropertyValue(individ,
      URI create (Constant.domainBase
              + Constant.localNamesPrefix
              + Constant.isTypeURIString)) match {
      case Some(indSet) => indSet.map(i => new Module(i.getURI.toString.substring(Constant.domainBase.length + Constant.localNamesPrefix.length)))
      case None => Set[Module]()
    }
  }

  def getType = isType

  /*
  * Adds the field to self and to all dependant modules, recursively.
  * @param Field to add
  * @return this.
  */
  def addField(field: Field): Module = {
    // adding to self
    log debug "adding to module: " + this.OWLURIString + " field: " + field
    addFieldRec(field, Set[Module]())
    this
  }

  protected def addFieldRec(field: Field, modulesAlreadyProcessed: Set[Module]): Option[Module] = {
    if (modulesAlreadyProcessed contains this) {
      None
    } else {
      if (!fieldsContains(field)) {
        this.fields = this.fields + field
        val modulesAlreadyProcessedAndCurrent: Set[Module] = modulesAlreadyProcessed ++ Set(this)
        for (val mod <- getModules) {
          mod.addFieldRec(field, modulesAlreadyProcessedAndCurrent)
        }
        this.addedFields = addedFields + field
      }
      Some(this)
    }
  }

  private def fieldsContains(field: Field): Boolean = {
    val res = this.fields.filter(f => f.OWLURIString == field.OWLURIString)
    res.size > 0
  }

  def resetAddedFields = this.addedFields = Set[Field]()

  /*
  * Removes the field from self and from all dependant modules, recursively.
  * @param Field to remove
  * @return this.
  */
  def removeField(field: Field): Module = {
    // removing from self
    if (this.fields contains field) {
      this.fields = this.fields - field
      for (val mod <- getModules) {
        mod.removeField(field)
      }
      this.removedFields = removedFields ++ Set(field)

    } else {
      log warn "Remove of the field that does not exists silently ignored"
    }
    this
  }

  def resetRemovedFields = {
    this.removedFields = Set[Field]()
  }

  override def save(): OWLIndividual = {
    val moduleIndividual = saveRec(Set[Module]())
    moduleIndividual
  }

  private def saveRec(modulesAlready: Set[Module]): OWLIndividual = {
    val moduleIndividual = OWLSaveHelper.saveIndividual(individ, Constant.domainBase + Constant.localNamesPrefix + Constant.moduleClassURIString)
    if (!modulesAlready.contains(this)) {
      for (val m <- methods) {
        val methodInd = m.save
        val hasMethod = Constant.dataFactory.getOWLObjectProperty(URI create Constant.domainBase + Constant.localNamesPrefix + Constant.hasMethodURIString)
        val propAssertion = Constant.dataFactory.getOWLObjectPropertyAssertionAxiom(moduleIndividual, hasMethod, methodInd)
        val addPropAddAxiom = new AddAxiom(Constant.ontology, propAssertion)
        Constant.owlManager applyChange addPropAddAxiom
      }
      for (val f <- fields) {
        val fieldInd = f.save
        val hasField = Constant.dataFactory.getOWLObjectProperty(URI create Constant.domainBase + Constant.localNamesPrefix + Constant.hasFieldURIString)
        val propAssertion = Constant.dataFactory.getOWLObjectPropertyAssertionAxiom(moduleIndividual, hasField, fieldInd)
        val addPropAddAxiom = new AddAxiom(Constant.ontology, propAssertion)
        Constant.owlManager applyChange addPropAddAxiom
        addPropAddAxiom
      }
      for (val m <- getModules) {
        val modulesAlreadyWithCurrent = modulesAlready ++ Set(this)
        val moduleInd = m.saveRec(modulesAlreadyWithCurrent)

        val hasModule = Constant.dataFactory.getOWLObjectProperty(URI create Constant.domainBase + Constant.localNamesPrefix + Constant.hasModuleURIString)
        val propAssertion = Constant.dataFactory.getOWLObjectPropertyAssertionAxiom(moduleIndividual, hasModule, moduleInd)
        val addPropAddAxiom = new AddAxiom(Constant.ontology, propAssertion)
        Constant.owlManager applyChange addPropAddAxiom
      }
    }
    moduleIndividual
  }

  override def toString: String = {
    val modulesStringSet: Set[String] = (for (val m <- getModules) yield m.OWLURIString + " ,")
    var moduleString: String = modulesStringSet.foldLeft[String]("")((one, two) => one + two)
    if (moduleString.length > 0) {
      moduleString = moduleString.substring(0, moduleString.length - 1)
    }
    uriString + " fields: " + getFields + " methods: " + getMethods + " modules: [" + moduleString + "]"
  }

  /**
   * Runs through modules recursively and returns the set of them.
   * @param modulesDownToThis the Set of Modules that have been collected till this module.
   * @return Some of the Set of the modules and if this module is in the modulesDownToThis set.
   */
  def collectModules(modulesDownToThis: Set[Module]): Option[Set[Module]] = {
    if (modulesDownToThis.contains(this)) {
      None
    } else {
      var res: Set[Module] = modulesDownToThis ++ Set(this)
      for (val m <- getModules) {
        val mRes = m.collectModules(res)
        mRes match {
          case Some(x) => res = res ++ x
          case None => // no option to add
        }
      }
      Some(res)
    }
  }
}