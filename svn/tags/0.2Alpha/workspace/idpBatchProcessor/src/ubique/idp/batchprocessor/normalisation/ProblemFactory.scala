package ubique.idp.batchprocessor.normalisation;

import org.jgap.gp.GPProblem
import ubique.idp.normalisation.ProblemRunner
import ubique.idp.normalisation.ga.date.DateNormProblemRunner

object ProblemFactory {
  def apply(problem: String): Option[ProblemRunner]  = problem match{
    case "dateNorm" => Some(new DateNormProblemRunner())
    case _ => None
  }
}
