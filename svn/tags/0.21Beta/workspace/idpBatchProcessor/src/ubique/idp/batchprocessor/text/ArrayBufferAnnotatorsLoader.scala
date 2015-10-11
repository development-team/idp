package ubique.idp.batchprocessor.text;

import java.io.InputStream
import java.io.ByteArrayInputStream
import java.util.HashMap

import scala.collection.mutable.ArrayBuffer

import edu.cmu.minorthird.text.AnnotatorLoader
import edu.cmu.minorthird.text.Annotator

import org.apache.log4j.Logger

/** Class to load annotators for enclosing annotators case */
class ArrayBufferAnnotatorLoader(fileNameToAnnotatorBuffer:ArrayBuffer[Tuple2[String,Annotator]]) 
extends AnnotatorLoader {
  
  private val log:Logger = Logger.getLogger(this.getClass())
  private val myClassLoader = this.getClass().getClassLoader();
  private var fileNameToContentsMap: HashMap[String, String] = new HashMap
  
  override def findAnnotator(annotationType:String, source: String): Annotator = {
    (fileNameToAnnotatorBuffer.filter((aTuple) => aTuple._1 == source))(0)._2
  }
  
  /** Find the named resource file - usually a dictionary or trie for mixup. */
  override def findFileResource(fileName: String): InputStream = {
    log.info("looking for file resource "+fileName+" with encapsulated loader");
    val contents:Array[byte] = fileNameToContentsMap.get(fileName).asInstanceOf[Array[byte]];
    if (contents!=null) {
      log.info("encapsulated resource found containing "+contents.length+" bytes");
      return new ByteArrayInputStream(contents)
    } else {
      log.info("calling default class loader to find resource");
      return this.getClass().getClassLoader().getResourceAsStream(fileName);
    }
  }

  /** Find the named resource class - usually an annotator. */
  override def findClassResource(className: String): Class[Any] = {
    try {
      return myClassLoader.loadClass(className).asInstanceOf[Class[Any]]
    } catch {
      case e:ClassNotFoundException => return null
    }
  }
}
