package ubique.idp.normalisation.ga.date.test;

import org.scalacheck.Test
import org.scalacheck.Gen
import org.scalacheck.Prop
import java.util.Calendar
import ubique.idp.normalisation.ga.date.model.DateModel
import ubique.idp.normalisation.ga.date.command.DelimiterChangeCommand
import org.jgap.gp.impl.ProgramChromosome
import org.jgap.gp.impl.GPProgram
import org.jgap.gp.impl.GPConfiguration

object TestDelimiterChangeCommand {
  val conf = new GPConfiguration
  // used Appolo program dates on the Moon
  val dates = List("20.07.1969", "1969-11-19", "05 February 1971", "30/07/1971", "21 04 1972", "April 11, 1972")
  val delimiters = Gen.elements("\\s+", "\\s*\\.\\s*", "\\s*\\-\\s*", "\\s*/\\s*", "\\s*\\,?\\s+")
  val prog = new GPProgram(conf, 3)
  val aProgram = new ProgramChromosome(conf, 1, prog);

    
  val propDelimiter = Prop.forAll(delimiters)(delimiter => {
    val command = new DelimiterChangeCommand(conf, delimiter);
    dates.forall(d => {
      val dm = new DateModel(d);
      prog.setApplicationData(dm)
      command.execute_void(aProgram, 0, null)
      val res = aProgram.getIndividual().getApplicationData().asInstanceOf[DateModel].getDestination()
      d.split(delimiter).toString.equals(res.toString)
    })
  })

  def main(args : Array[String]) : Unit = {
    Test.check(propDelimiter)
  }
}
