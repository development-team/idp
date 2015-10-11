package ubique.idp.normalisation.ga.date.test;

import org.scalacheck.Test
import org.scalacheck.Gen
import org.scalacheck.Prop
import java.util.Calendar
import ubique.idp.normalisation.ga.date.DateModelMatcher
import org.jgap.gp.impl.ProgramChromosome
import org.jgap.gp.impl.GPProgram
import org.jgap.gp.impl.GPConfiguration

object TestMonthNormMatcherCommand {
  val conf = new GPConfiguration
  val monthsEn = Gen.elements("Jan 1968", " of February ", " march ", "-apr-", "May56", " june ", "Jul.1988", " august ", " From September ", " Oct 1998", " of nov", " December of 2004")
  val monthListEn = List("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec")
  val monthsRu = Gen.elements("январь 1968", "в Феврале" , " март ", " -апр- ", "Май67", " июнь ", " Июль.1998", " Август ", " С Сентября", " Окт 1998 ", " начиная с ноября", " Декабрь 2004")
  val monthListRu = List("янв", "фев", "мар", "апр", "май", "июн", "июл", "авг", "сен", "окт", "ноя", "дек")
  val monthsLat = Gen.elements("I 1968", "в II" , " III ", " -IV- ", "V67", " VI ", " VII.1998", " VIII ", " С IX", " X 1998 ", " начиная с XI", " XII 2004")
  val monthListLat = List("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII")
  val monthsInt = Gen.choose(-12, 14)
  val prog = new GPProgram(conf, 3)
  val aProgram = new ProgramChromosome(conf, 1, prog);
  val command = new MonthNormMatcherCommand(conf);
    
  val propMonthEng = Prop.forAll(monthsEn)( m => {
    val dm = new DateModelMatcher(m);
    prog.setApplicationData(dm)
    command.execute_void(aProgram, 0, null)
    val res = aProgram.getIndividual().getApplicationData().asInstanceOf[DateModelMatcher].getMonth()
    // println("\n test: " + m + ", " + res) 
    m.toLowerCase.contains(monthListEn.apply(res.intValue()-1))
  })
  
  val propMonthRu = Prop.forAll(monthsRu)( m => {
    val dm = new DateModelMatcher(m);
    prog.setApplicationData(dm)
    command.execute_void(aProgram, 0, null)
    val res = aProgram.getIndividual().getApplicationData().asInstanceOf[DateModelMatcher].getMonth()
    // println("\n test: " + m + ", " + res) 
    m.toLowerCase.contains(monthListRu.apply(res.intValue()-1))
  })
  
  val propMonthLat = Prop.forAll(monthsLat)( m => {
    val dm = new DateModelMatcher(m);
    prog.setApplicationData(dm)
    command.execute_void(aProgram, 0, null)
    val res = aProgram.getIndividual().getApplicationData().asInstanceOf[DateModelMatcher].getMonth()
    println("\n test: " + m + ", " + res) 
    m.contains(monthListLat.apply(res.intValue()-1))
  })
  
  
  val propMonthInt = Prop.forAll(monthsInt) ( m => {
    val dm = new DateModelMatcher("" + m);
    prog.setApplicationData(dm)
    command.execute_void(aProgram, 0, null)
    val res = aProgram.getIndividual().getApplicationData().asInstanceOf[DateModelMatcher].getMonth()
    // println("\n test: " + m + ", " + res)
    if (m > 0 && m < 13) {
      m == res
    } else if (m < 0){
      res == -m
    } else {
      res == null
    }
  })
  def main(args : Array[String]) : Unit = {
    // Test.check(propMonthEng)
    // Test.check(propMonthInt)
    // Test.check(propMonthRu)
    Test.check(propMonthLat)
  }
}