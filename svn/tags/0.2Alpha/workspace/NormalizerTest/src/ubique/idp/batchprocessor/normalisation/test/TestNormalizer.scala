package ubique.idp.batchprocessor.normalisation.test;

import java.io.File

import org.scalacheck.Test
import org.scalacheck.Gen
import org.scalacheck.Prop
import org.scalacheck.Prop.property

import edu.cmu.minorthird.util.IOUtil

import jp.ac.kobe_u.cs.prolog.lang.Predicate

import ubique.idp.batchprocessor.normalisation.Normalizer

object TestNormalizer {
  
  val rulesFile = new File("./prj/annotators/c45rule.sjobj")
  val substRules = IOUtil.loadSerialized(rulesFile).asInstanceOf[Predicate]
  val normDirname = "./prj/res/"
  
  // val propNormTest = property( (in: Unit) => { in == in })
  
   def main(args: Array[String]) {
    Normalizer(substRules, normDirname, ".norm.xml")
  }
  
}
