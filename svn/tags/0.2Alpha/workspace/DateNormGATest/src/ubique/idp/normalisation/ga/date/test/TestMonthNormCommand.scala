package ubique.idp.normalisation.ga.date.test;

import org.scalacheck.Test
import org.scalacheck.Gen
import org.scalacheck.Prop
import java.util.Calendar
import ubique.idp.normalisation.ga.date.DateModel
import org.jgap.gp.impl.ProgramChromosome
import org.jgap.gp.impl.GPProgram
import org.jgap.gp.impl.GPConfiguration

object TestMonthNormCommand {
  val conf = new GPConfiguration
  val monthsEng = Gen.elements("Jan", "February", "mar", "apr", "May", "june", "Jul", "august", "September", "Oct", "nov", "December")
  val monthList = List("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec")
  val monthsInt = Gen.choose(-12, 14)
  val prog = new GPProgram(conf, 3)
  val aProgram = new ProgramChromosome(conf, 1, prog);
  val command = new MonthNormCommand(conf);
    
  val propMonthEng = Prop.forAll(monthsEng)( m => {
    val dm = new DateModel(m);
    prog.setApplicationData(dm)
    command.execute_void(aProgram, 0, null)
    val res = aProgram.getIndividual().getApplicationData().asInstanceOf[DateModel].getMonth()
    m.toLowerCase.contains(monthList.apply(res.intValue()).toLowerCase)
  })
  val propMonthInt = Prop.forAll(monthsInt) ( m => {
    val dm = new DateModel("" + m);
    prog.setApplicationData(dm)
    command.execute_void(aProgram, 0, null)
    val res = aProgram.getIndividual().getApplicationData().asInstanceOf[DateModel].getMonth()
    if (m > 0 && m < 13) {
      m == res
    } else {
      res == null
    }
  })
  def main(args : Array[String]) : Unit = {
    Test.check(propMonthEng)
    Test.check(propMonthInt)
  }
}