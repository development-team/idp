package ubique.idp.normalisation.ga.date.test;

import org.scalacheck.Test
import org.scalacheck.Gen
import org.scalacheck.Prop
import java.util.Calendar
import ubique.idp.normalisation.ga.date.command.YearNormCommand
import ubique.idp.normalisation.ga.date.model.DateModel
import org.jgap.gp.impl.ProgramChromosome
import org.jgap.gp.impl.GPProgram
import org.jgap.gp.impl.GPConfiguration

object TestYearNormCommand {
  val conf = new GPConfiguration
  val year2 = Gen.choose(0,99)
  val year4 = Gen.choose(1000,9999)
  val year3 = Gen.choose(100,999)
  val yearM = Gen.choose(-1000, 0)
  val year5 = Gen.choose(10000, 20000)
  val currentYear = (Calendar.getInstance()).get(Calendar.YEAR) - 2000
    val prog = new GPProgram(conf, 3)
    val aProgram = new ProgramChromosome(conf, 1, prog);
    val yearCommand = new YearNormCommand(conf);
    
  val propYear2 = Prop.forAll(year2)( y => {
    val dm = new DateModel("20 jun " + y);
    dm.incCursor
    dm.incCursor
    prog.setApplicationData(dm)
    yearCommand.execute_void(aProgram, 0, null)
    val res = aProgram.getIndividual().getApplicationData().asInstanceOf[DateModel].getYear()
    if (y > currentYear) {
      print("\n 1900 " + y + " " + res)
      (1900 + y) == res
    } else {
      print("\n 2000 " + y + " " + res)
      (2000 + y) == res
    }
  })
  
  val propYear4 = Prop.forAll(year4)( y => {
    val dm = new DateModel("20 jun " + y);
    dm.incCursor
    dm.incCursor
    prog.setApplicationData(dm)
    yearCommand.execute_void(aProgram, 0, null)
    val res = aProgram.getIndividual().getApplicationData().asInstanceOf[DateModel].getYear()
    y == res
  })
  
  val propYear3 = Prop.forAll(year3) ( y => {
    val dm = new DateModel("20 jun " + y);
    dm.incCursor
    dm.incCursor
    prog.setApplicationData(dm)
    yearCommand.execute_void(aProgram, 0, null)
    val res = aProgram.getIndividual().getApplicationData().asInstanceOf[DateModel].getYear()
    res == null
  })
  
  val propYearM = Prop.forAll(yearM) ( y => {
    val dm = new DateModel("20 jun " + y);
    dm.incCursor
    dm.incCursor
    prog.setApplicationData(dm)
    yearCommand.execute_void(aProgram, 0, null)
    val res = aProgram.getIndividual().getApplicationData().asInstanceOf[DateModel].getYear()
    if(y < 0) res == null
    else res == 2000 + y
  })
  
  val propYear5 = Prop.forAll(year5) ( y => {
    val dm = new DateModel("20 jun " + y);
    dm.incCursor
    dm.incCursor
    prog.setApplicationData(dm)
    yearCommand.execute_void(aProgram, 0, null)
    val res = aProgram.getIndividual().getApplicationData().asInstanceOf[DateModel].getYear()
    res == null
  })
  
  def main(args : Array[String]) : Unit = {
    Test.check(propYear2)
    Test.check(propYear4)
    Test.check(propYear3)
    Test.check(propYearM)
    Test.check(propYear5)
  }
}
