package ubique.idp.batchprocessor.xml;

import scala.collection.mutable.{HashMap,HashSet}
import scala.collection.immutable.Stack
import scala.xml.Node

/**
  *   Object collects element labes in scala.collection.mutable.Stack
  */
object ElementsPathCollector {
  
  type ResMapping = HashMap[Path,String]
  type Res = HashSet[Stack[String]]
  type Path = Stack[String]
  private val res: Res = new HashSet
  private val mapping: ResMapping = new HashMap
  private var attributeName = "";
  private var attributePath = ""
  
  /**
    * Collects paths to elements that contains specified attrubutes and their values in HashMap
    * @param attributeName attribute name that will be collected 
    * @param path tree Node tree to be processed
    * @return HashMap of collected paths and attribute values
    */
  def collectPathMapAttributes(attributeName: String, tree: Node): ResMapping = {
    this.attributeName = attributeName
    this.attributePath = "@" + this.attributeName
    collectPathAttributeMappingsRec(tree: Node, (new Stack).push(tree.label))
    this.mapping
  }
  
  /** 
    *  Collects paths to elements that contains specified attributes in HashSet
    * @param attributeName attribute name that will be collected 
    * @param path tree Node tree to be processed
    * @return HashSet of collected paths and attribute values
    */
  def collectPathAttributes(attributeName: String, tree: Node): Res = {
    this.attributeName = attributeName
    this.attributePath = "@" + this.attributeName
    collectPathAttributesRec(tree: Node, (new Stack).push(tree.label))
    this.res
  }
  
  private def collectPathAttributesRec(node: Node, path: Path): Unit = {
    val kids = node.child
    var currentPath = new Stack[String]
    if (kids.size > 0){
      if (kids.size > 0){
        for (val aKid <- kids) yield aKid match {
          case attrs @ aKid if attrs.\(this.attributePath) != "" => { 
            currentPath = path + aKid.label
            //println (" + "  + aKid + " " + currentPath)
            res += (currentPath)
            collectPathAttributesRec(aKid, currentPath)
          }
          case _ => {
            currentPath = path + aKid.label
            // println (" - " + aKid + " " + currentPath)
            collectPathAttributesRec(aKid, currentPath)
          }
        }
      }
    }
  }
  
  /**
    *  Recursively collects paths to elements that contains specified attrubutes and their values in Stack
    * @param node Tree to be processed
    * @path path Stack that collects nodes names
    */
  private def collectPathAttributeMappingsRec(node: Node, path: Path): Unit = {
    val kids = node.child
    var currentPath = new Stack[String]
    if (kids.size > 0){
      if (kids.size > 0){
        for (val aKid <- kids) yield aKid match {
          case attrs @ aKid if attrs.\(this.attributePath) != "" => { 
            currentPath = path + aKid.label
            // println (" + "  + aKid + " " + currentPath + " " + attrs.\(this.attributePath))
            mapping += (currentPath -> attrs.\(this.attributePath).toString)
            collectPathAttributeMappingsRec(aKid, currentPath)
          }
          case _ => {
            currentPath = path + aKid.label
            //println (" - " + aKid + " " + currentPath)
            collectPathAttributeMappingsRec(aKid, currentPath)
          }
        }
      }
    }        
  }
}
