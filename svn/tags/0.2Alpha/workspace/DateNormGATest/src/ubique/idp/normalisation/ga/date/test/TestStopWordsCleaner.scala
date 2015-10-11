package ubique.idp.normalisation.ga.date.test;

import java.io.FileNotFoundException

import org.scalacheck.Test
import org.scalacheck.Gen
import org.scalacheck.Prop

import ubique.idp.normalisation.ga.date.StopWordsCleaner

object TestStopWordsCleaner {
  val stopWordsFileAddress = "./nlp/stop_words_russian.txt"
  val encoding = "UTF-8"
    
  val dates = Gen.elements(
    ("15 ноября 1978 г.", "15 ноября 1978 "),
    ("01.11.1961", "01.11.1961"),
    ("12.03.2007", "12.03.2007"),
    ("1987", "1987"),
    ("1990", "1990"),
    ("1992", "1992"),
    ("13.03.1986", "13.03.1986"),
    ("27.10.2007", "27.10.2007"),
    ("2007", "2007"),
    ("2000", "2000"),
    ("2006", "2006"),
    ("В 2003 г.", "2003 "),
    ("В 2004 г.", "2004 "),
    ("В 2000/2001", "2000/2001"),
    ("08/01/1966", "08/01/1966"),
    ("26.06.84", "26.06.84"),
    ("23 августа 1979г. ", "23 августа 1979"),
    ("19 июня 1982 года", "19 июня 1982 "),
    ("1973 г.", "1973 "),
    ("20 июня 1982", "20 июня 1982"),
    ("27 сентября 1967", "27 сентября 1967"),
    ("1982 г", "1982 "),
    ("«03» января 2007 г.", "03 января 2007 "),
    ("18.09.1972 г.", "18.09.1972 "),
    ("1971 г. р.", "1971 ")
  )
    
  val propStopWordsCleaner = Prop.forAll(dates) (date => {
    val temp = StopWordsCleaner.apply(date._1, stopWordsFileAddress, encoding)
    println("\n res "  + temp)
    date._2 equals temp
  })
    
  def main(args : Array[String]) : Unit = {
    Test.check(propStopWordsCleaner)
  }
}
