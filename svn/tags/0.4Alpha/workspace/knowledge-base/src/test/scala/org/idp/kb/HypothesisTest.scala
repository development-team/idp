package org.idp.kb

import model.domain.AddFieldAction
import org.junit._
import java.net.URI
import org.apache.log4j.Logger
import Assert._

/**
 * @author Max Talanov
 * @date 07.12.2009
 * Time 22:52:35
 */
@Test
class HypothesisTest {
  val log = Logger.getLogger(this getClass)

  //  @Test
  def testHypothesis {
    log.info("test: " + "hypothesis production test")
    val statementString = "Menta place Login field on Customer page."
    val statement = Statement(statementString)

    val baseURI = Constant.domainBase
    val dataFactory = Constant.dataFactory

    val hypoClass = dataFactory.getOWLClass(URI create (Constant.baseURI + Constant.localNamesPrefix + "CogitativeHipothesis"))

    val hypoRes = HypothesisUtil.findHypothesisOfAClass(hypoClass)

    log debug "hypoRes = " + hypoRes
    val res = HypothesisUtil.findMostProbableHypothesis(hypoRes, statement.toList(0))

    log debug ("Search result = " + res)
    for (val r <- res) {
      log debug r(statement.toList(0))
    }
    assertNotSame(None, res)
  }

  @Test
  def testHypothesisAddAction {
    log debug ("test:" + "hypothesis production and action extraction")
    val statementString = "Menta place Patronim field on CreateCustomer page."
    val statement = Statement(statementString)

    val baseURI = Constant.domainBase
    val dataFactory = Constant.dataFactory

    val hypoClass = dataFactory.getOWLClass(URI create (Constant.baseURI + Constant.localNamesPrefix + "CogitativeHipothesis"))

    val hypo = HypothesisUtil.findHypothesisOfAClass(hypoClass)
    log debug "hypo = " + hypo
    val hypoRes = HypothesisUtil.findMostProbableHypothesis(hypo, statement.toList(0))

    for (val hR <- hypoRes) {
      log debug hR(statement.toList(0))
      val action = hR(statement.toList(0))
      log debug action
      for (val a <- action) {
        val module = a._1.asInstanceOf[AddFieldAction]()
        log debug module
      }
    }

    assertNotSame(None, hypoRes)
  }
}