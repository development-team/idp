package org.idp.kb.applicationgenerator

import org.idp.kb.Constant
import org.idp.kb.model.domain.{Field, Module}
import org.apache.log4j.Logger

/**
 * @author talanovm
 * @date 14.12.2009
 * Time: 10:08:44
 */

class JavaClassGenerator extends ModuleGenerator {
  val log = Logger.getLogger(this.getClass)

  override def apply(module: Module): String = {
    val classDecl: String = {
      val moduleInterfaces = module.getInterfaces
      val moduleClasses = module.getSuperClasses
      var res = ""
      var superClassesDecl = ""
      var interfacesDecl = ""
      // the extends declaration
      if (moduleInterfaces.size > 0) {
        val interfaces = moduleInterfaces.filter(sC => {
          val t = sC.getType
          if (t.size > 0) {
            t.toList(0).OWLURIString == Constant.interfaceURIString
          } else {
            false
          }
        })
        // implements declaration
        if (interfaces.size > 0) {
          interfacesDecl = " implements "
          for (val i <- interfaces) {
            interfacesDecl += i.OWLURIString
          }
        }
      }
      if (moduleClasses.size > 0) {
        val superClasses = moduleClasses.filter(sC => {
          val t = sC.getType
          if (t.size > 0) {
            t.toList(0).OWLURIString != Constant.interfaceURIString
          } else {
            true
          }
        })
        if (superClasses.size > 0) {
          superClassesDecl = " extends " + superClasses.toList(0).OWLURIString
        }
      }
      res = superClassesDecl + interfacesDecl

      // the class declaration
      var moduleName = module.getBody
      if (moduleName.trim.length < 1) {
        moduleName = module.OWLURIString
      }
      "public class " + moduleName + res
    }
    val context = getContext(module)
    getPackage(module) + "\n\n" +
            getImports(module) + "\n\n" +
            classDecl + " { \n" +
            getConstructors(module, context) + "\n" +
            getFields(module) + "\n" +
            getMethods(module, context) + "\n }"
  }

  override protected def getMethods(module: Module, context: Map[String, List[Field]]): String = {
    var res = ""
    module.getMethods.map(method => {
      val mG = new JavaMethodGenerator
      res = res + mG(method, context)
    })
    res
  }

  override protected def getConstructors(module: Module, context: Map[String, List[Field]]): String = {
    var res = ""
    module.getConstructors.map(method => {
      val mG = new JavaMethodGenerator
      res = res + mG(method, context)
    })
    res
  }

  override protected def getSuperClass(module: Module): String = {
    var res = ""
    module.getSuperClasses.map(superClass => {
      val mG = new JavaClassGenerator
      res = res + mG(superClass)
    })
    res
  }

  override protected def getFields(module: Module): String = {
    var res = ""
    module.getFields.map(field => {
      val fG = new JavaFieldGenerator
      val gen = fG(field)
      res = res + gen
      // log debug gen
    })
    res
  }

  def getImports(module: Module): String = {
    if (module.getImports.size > 0) {
      val res = module.getImports.foldLeft("")((x1, x2) => {
        if (x1.length > 0) {
          x1 + "import " + x2 + ";\n"
        } else {
          "import " + x2 + ";\n"
        }
      })
      res
    } else {
      ""
    }

  }

  def getPackage(module: Module): String = {
    if (module.getPackage.length > 0) {
      "package " + module.getPackage + ";"
    } else {
      ""
    }

  }

}