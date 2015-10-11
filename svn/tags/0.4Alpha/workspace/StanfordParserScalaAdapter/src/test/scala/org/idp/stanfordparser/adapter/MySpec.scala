package org.idp.stanfordparser.adapter

import org.specs._
import org.specs.runner.{ConsoleRunner, JUnit4}

class MySpecTest extends JUnit4(MySpec)
//class MySpecSuite extends ScalaTestSuite(MySpec)
object MySpecRunner extends ConsoleRunner(MySpec)

object MySpec extends Specification {
  "This Menta system" should {
    "save the world" in {
      val list = Nil
      list must beEmpty
    }
    "run the primitive adapter to stanford parser" in {
      val sent = "Menta place three fields on Customer page"
      val sa = new ScalaAdapter()
      val res = sa.run(sent)
      Console.println("type dependencies ")
      // Console.println(res)
      val it = res.iterator()
      while (it.hasNext){
        Console.println(it.next)
       }
      res mustNotBe Nil
    }
  }
}
