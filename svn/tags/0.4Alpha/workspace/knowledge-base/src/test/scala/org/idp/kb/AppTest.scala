package org.idp.kb

import org.junit._
import Assert._

@Test
class AppTest {

    @Test
    def testOK() = assertTrue(true)

//    @Test
//    def testKO() = assertTrue(false)

	@Test
	def testCase() = {
	  val id = Fun("x", Var("x"))
	  val t = Fun("x", Fun("y", App(Var("x"), Var("y"))))
	  TermTest.printTerm(t)
	  println
	  println(TermTest.isIdentityFun(id))
	  println(TermTest.isIdentityFun(t))
	}

}

abstract class Term
case class Var(name: String) extends Term
case class Fun(arg: String, body: Term) extends Term
case class App(f: Term, v: Term) extends Term

object TermTest extends Application {
  def printTerm(term: Term) {
    term match {
      case Var(n) =>
        print(n)
      case Fun(x, b) =>
        print("^" + x + ".")
        printTerm(b)
      case App(f, v) =>
        Console.print("(")
        printTerm(f)
        print(" ")
        printTerm(v)
        print(")")
    }
  }
  def isIdentityFun(term: Term): Boolean = term match {
    case Fun(x, Var(y)) if x == y => true
    case _ => false
  }
  
  
}
