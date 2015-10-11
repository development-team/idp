package org.idp.stanfordparser.adapter

import java.util.Collection
import edu.stanford.nlp.parser.lexparser.LexicalizedParser
import edu.stanford.nlp.trees.{TreebankLanguagePack, GrammaticalStructureFactory, 
                               GrammaticalStructure, PennTreebankLanguagePack, 
                               TreePrint}

class ScalaAdapter {
  def run(sentence: String):java.util.Collection[edu.stanford.nlp.trees.TypedDependency] = {
	val lp = new LexicalizedParser("englishPCFG.ser.gz")
	
	lp.setOptionFlags("-maxLength", "80", "-retainTmpSubcategories")
    var parse = lp.apply(sentence);
    // parse.pennPrint();
    // Console.println();

    val tlp = new PennTreebankLanguagePack();
    val gsf = tlp.grammaticalStructureFactory();
    val gs = gsf.newGrammaticalStructure(parse);
    val tdl = gs.typedDependenciesCollapsed();
    // Console.println(tdl);
    // Console.println();

    val tp = new TreePrint("penn, typedDependenciesCollapsed");
    // tp.printTree(parse)
    tdl
  }
}
