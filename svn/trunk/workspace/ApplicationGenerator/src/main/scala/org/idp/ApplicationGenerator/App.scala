package org.idp.ApplicationGenerator
import java.util.Collections
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.io.OWLXMLOntologyFormat;
import java.net.URI;
import java.io.File;
import org.semanticweb.owl.model.OWLClass;
//import org.semanticweb.owl.util.OWLOntologyWalker;
//import org.semanticweb.owl.util.OWLOntologyWalkerVisitor;


/*import org.semanticweb.owl.model.*;*/

/**
 * Hello world!
 *
 */
object App extends Application {

  val manager = OWLManager.createOWLOntologyManager();
  val physicalURI = new File ("TargetAppRDF.owl").toURI;
  val ont = manager.loadOntologyFromPhysicalURI(physicalURI);

  for ( val cls <-  scala.collection.jcl.Set(ont.getReferencedClasses)) {
    
	 Console println cls
  }
  // setup walker
  //val  walker = new OWLOntologyWalker(Collections.singleton(ont));
  


}
