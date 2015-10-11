package ubique.idp.normalisation.ga.date.test;

import org.scalacheck.Test
import org.scalacheck.Gen
import org.scalacheck.Prop
import java.util.Calendar
import ubique.idp.normalisation.ga.date.command.YearNormCommand
import ubique.idp.normalisation.ga.date.command.DayNormCommand
import ubique.idp.normalisation.ga.date.model.DateModel
import org.jgap.gp.impl.ProgramChromosome
import org.jgap.gp.impl.GPProgram
import org.jgap.gp.impl.GPConfiguration

object TestDayNormCommand {

  val conf = new GPConfiguration
  val minDay = 1
  val maxDay = 31
  val day = Gen.choose(-30,32)
  val currentYear = (Calendar.getInstance()).get(Calendar.YEAR) - 2000
  val prog = new GPProgram(conf, 3)
  val aProgram = new ProgramChromosome(conf, 1, prog);
  val command = new DayNormCommand(conf);
  
  val propDay = Prop.forAll(day) ( d => {
    val dm = new DateModel(d +" jun 1969");
    prog.setApplicationData(dm)
    command.execute_void(aProgram, 0, null)
    val res = aProgram.getIndividual().getApplicationData().asInstanceOf[DateModel].getDay()
    if (d < minDay) res == null
    else if (d >= minDay && d <= maxDay) res == d
    else res == null
  })
  
  def main(args: Array[String]) {
    Test.check(propDay)
  }
}