package ubique.idp.normalisation;

class RulesRunner(splitter: Splitter, 
    rules: Array[(String,Array[String=>Option[String]]) => boolean ]) {
  type ModelFunc = String => Option[String]
  type Rule = (String, Array[ModelFunc]) => boolean
  
  def apply(model: Array[ModelFunc]): boolean = {
    var counter = 0
    val res = model.forall((aFunc) => {
      aFunc(splitter.split(counter)).getOrElse(false).asInstanceOf[String].length() > 0
    })
    if (res) {
      rules.forall((aRule) => {
        aRule(splitter.getInputString, model)
      })
    } else res
  }
}
