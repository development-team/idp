package ubique.idp.batchprocessor.xml;

import scala.xml.parsing.ConstructingParser
import scala.xml.{Elem, Null ,Node, Document, NamespaceBinding, MetaData, Text, EntityRef, Atom, PCData, XML}
import scala.collection.mutable.{LinkedList, HashSet, ArrayBuffer}
import scala.collection.immutable.Stack
import scala.collection.jcl.LinkedHashSet
import scala.io.{Source, BufferedSource}

import java.io.{File, FileInputStream, IOException}
import java.nio.charset.Charset

import ubique.idp.batchprocessor.constants.Constants

/**
*  TODO refactor: make it object.
*  Runs through the contents of directory: xml files and performs collections of tags or apply specified function to each found node. 
*/
class XMLRunner (directoryName: String, namespace: String, predictedTagPrefix: String) {
  
  private var nB: NamespaceBinding = null
  private var dirFile = new File(directoryName)
  private var completeTree: Node = null
  private var completeTreeCurrentChild: Node = null
  type NamesList = LinkedHashSet[Tuple4[String,String,String,String]]
  type Path = Stack[String]
  
  /** Namespace binding getter */
  def getNB: NamespaceBinding = nB
  
  /** 
  * CompleteTree getter
  * @return as complete as possible tree learned from training set 
  */
  def getCompleteTree: Node = completeTree
  
  /**
  * Applies specified function to each Node of the XML tree of specified file in specified directory
  * @param dir directory with xml files
  * @param function 
  * @return Node root element produced by exposing XML file to specified function
  */
  def forAllNodes(function: (Node, Stack[String]) => Node, dir: String, filename: String ): Node = {
    
    val difFile = new File(dir);
    val aFile = new File(dirFile, filename);
    
    if (dirFile.isDirectory()) {
      if (aFile.isFile()) {
        val p = ConstructingParser.fromSource(Source.fromFile(aFile, Constants.defaultEncoding), true)
        val root = p.document.docElem
        val children = this.mapNodeRec(function, root, (new Stack[String]()) + root.label)
        if (children.isDefined) recreateElem(root, children.get)
        else {
          val res = recreateElem(root)
          res
        }
      } else {
        throw new IllegalArgumentException(aFile + " is not a file");
      }
    } else {
      throw new IllegalArgumentException(dirFile + " is not a directory");
    }
  }
  
  /**
  *   Recursively applies specified to specified Node and returns recreated node
  * @param function to apply
  * @param node Node to apply function to
  * @return Node recreated
  */
  private def mapNodeRec(function: (Node, Stack[String]) => Node, aNode: Node, pathAccumulator: Stack[String]): 
  Option[Seq[Node]] = {
    val res: ArrayBuffer[Node] = new ArrayBuffer[Node]
    if (aNode.isInstanceOf[Elem]) {
      for (aChild <- aNode.child) {
        if(aChild.child.length > 0) {
          val pathAccumulatorWChild = pathAccumulator + aChild.label
          val children = mapNodeRec(function, aChild, pathAccumulatorWChild)
          if (children isDefined) {
            res += recreateElem(aChild, children.get)
          } else {
            res += recreateElem(aChild)
          }
        } else if (aChild.isInstanceOf[Elem]) {
          res += recreateElem(aChild)
        } else if (aChild.isInstanceOf[Text]) {
          res += recreateText(function(aChild, pathAccumulator))
        } else if (aChild.isInstanceOf[EntityRef] || aChild.isInstanceOf[Atom[java.lang.String]]) {
          res += recreateText(aChild)
        } else {
          if (Constants.DEBUG) println (" ** else " + aChild + " " + aChild.getClass())
        }
      }
    }
    if (res.length > 0) Some(res)
    else None
  }
  
  
  /** 
  *  Collects tagnames with specified namespace and forms maximum complete tree
  * @param dir labels directory
  * @param namespace 
  * @return Tuple2 of HashSet that contains Tuple4 of: prefix, label, qname and predictedTagPrefix_prefix_label
  */ 
  def collectTagNames(): NamesList = {
    //val dirFile: File = new File(this.directoryName)
    var p: scala.xml.parsing.ConstructingParser = null
    var rootElem: Elem = null
    var qname: Tuple4[String, String, String, String] = null
    var nB: NamespaceBinding = null
    var currentTree: Node = null
    val res: NamesList = new LinkedHashSet
    var fileStream: FileInputStream = null
    
    if (dirFile.isDirectory()) {
      val aFiles: Array[File] = dirFile.listFiles()
      // file processing
      aFiles.map[Unit]((aFile) => {
        println (aFile.getName())
        if (aFile.isFile()){
          // p = ConstructingParser.fromSource(Source.fromFile(aFile, Constants.defaultEncoding), false)
          try {
            fileStream = new FileInputStream(aFile)
            rootElem = XML.load(fileStream)
            // println("----------------")
            println("Child")
            // currentTree = recursiveUniqueElemFilter(p.document.docElem)
            currentTree = recursiveUniqueElemFilter(rootElem)
          } catch {
            case e: IOException => throw new IllegalArgumentException(e.getMessage())
            case e: Exception => throw new IllegalArgumentException(aFile + " is not valid xml file ")
          } finally {
            if (fileStream != null) {
              fileStream close 
            }
          }
          if (this.completeTree == null) {
            this.completeTree = currentTree
            println("Formed complete tree \n" + completeTree )
          } else {
            this.completeTree = recursiveMerge(currentTree, completeTree)
          }
        }
      })

      this.completeTree = filterNamespaceTree(this.completeTree, namespace)
      //if (Constants.DEBUG) println (this.completeTree)
      currentTree = recursiveReverse(this.completeTree)
      filterNamespace(currentTree.descendant, namespace).map[Unit]((aNode) => {
        qname = createElementTuple(aNode) 
        if (!(res contains qname)) {res += (qname)}
        this.nB = aNode.scope
      })
      // println (" res " + res)
      res
    } else {
      throw new IllegalArgumentException(this.directoryName + " is not a directory");
    }
  }

  // Supplemental section
  /** Flat namespace filter */
  private def filterNamespace(aNodes: Seq[Node], namespace: String): Seq[Node] = {
    aNodes.filter((aNode) => { aNode.namespace == namespace })
  }
  
  /** Filters elements in tree, saves with specified namespace only */
  private def filterNamespaceTree(tree: Node, namespace: String): Node = {
   val children = filterNamespaceTreeRec(tree, namespace)
   if (children.isDefined) recreateElem(tree, children.get)
   else recreateElem(tree)
  }
  
  /** Filters XML elements on base of specified namespace */
  private def filterNamespaceTreeRec(aNode: Node, ns: String): Option[Seq[Node]] = {
    val res: ArrayBuffer[Node] = new ArrayBuffer[Node]
    if (aNode.isInstanceOf[Elem]) {
      for (aChild <- aNode.child) {
        // println(aChild + " " + aChild.namespace + " " + ns + " " + aChild.child.length)
        if (aChild.namespace == ns) {
          // println(aChild + " " + aChild.namespace + " " + ns + " " + aChild.child.length)
          if(aChild.child.length > 0) {
            val children = filterNamespaceTreeRec(aChild, ns)
            if (children isDefined) {
              res += recreateElem(aChild, children.get)
            } else {
              res += recreateElem(aChild)
              //     println(" res " + res)
            }
          } else {
            res += recreateElem(aChild)
            //     println(" res 0 ch " + res)
          }
        } else {
          if (aChild.child.length > 0) {
            val children = filterNamespaceTreeRec(aChild, ns)
            if (children.isDefined) {
              res ++ children.get
            }
          }
        }
      }
    }
    if (res.length > 0) Some(res)
    else None
  }
  
  /** Supplemental method to create tuple with Node attributes */ 
  private def createElementTuple(aElement: Node): Tuple4[String, String, String, String] = {
    new Tuple4(aElement.prefix , aElement.label, aElement.prefix + ":" + aElement.label, this.predictedTagPrefix+"_"+aElement.prefix+"_"+aElement.label)
  }
  
  /**
  * Filters only unique elements with attributes from document
  * @param - a Node to filter
  * @returns - result Node
  */
  private def recursiveUniqueElemFilter(subTree: Node): Node = {
    //var haveAlready: HashSet[Node] = new HashSet()
    var haveAlready: HashSet[Node] = new HashSet()
    var str_haveAlready: HashSet[String] = new HashSet() //added,iss#3
    var children = subTree.child.filter((aNode) => aNode.isInstanceOf[Elem])
    children = children.map[Option[Node]] ((aChild) => {
      // println(" + " + aChild)
      // println()
      
      //replaced,iss#3 if (haveAlready.contains(aChild)
      if (str_haveAlready.contains(aChild.toString()) || haveAlready.contains(aChild)
          && haveAlready.findEntry(aChild).isDefined
          && haveAlready.findEntry(aChild).get.attributes == aChild.attributes) {
        //println("None")
        None
      } else {
          //println("before : "+haveAlready)  
          haveAlready += aChild
          str_haveAlready += aChild.toString() //added,iss#3
          //println("after :"+haveAlready)
          if (aChild.child.size > 0){
            //println("Some(rec)")
            Some(recursiveUniqueElemFilter(aChild))
          } else {
            //println("Some(child)")
            Some(aChild)
          }
      }
    }).filter((aChildOption) => aChildOption.isDefined).map[Node]((aNodeOption) => aNodeOption.get)
    createElem(subTree, children)
  }
  
  /**
  *  Creates Elem on base of specified properties with replacement of all Text children with one specified as first child.
  *
  * @param aNode - source to create new Elem
  * @param value - the text to place as first child
  */
  def recreateElemWithText(aNode: Node, value: String): Node = {
    val children = aNode.child.filter( aNode => aNode.isInstanceOf[Elem] )
    val l:Seq[Node] = List(new Text(value)) ++ children
    new Elem(aNode.prefix, aNode.label, aNode.attributes, aNode.scope, l:_*)
  }
  
  /** 
  *  Creates Elem on base of specified properties with specified children
  * @param aNode - source to create new element
  * @param children - Seq[Node] to use as children
  */
  private def recreateElem(aNode: Node, children: Seq[Node]): Node = 
    new Elem(aNode.prefix, aNode.label, aNode.attributes, aNode.scope, children:_*)
    
  /**
  *  Creates Elem - copies all content without children 
  * @param Node - node to recreate
  * @return Node - recreated node
  */
  private def recreateElem(aNode: Node): Node = 
    new Elem(aNode.prefix, aNode.label, aNode.attributes, aNode.scope)
  
  private def recreateText(aNode: Node): Node = 
    new Text(aNode.text)
    
  /** 
  *  Creates Elem on base of specified properties with specified children 
  * @param aNode - source to create new element
  * @param children - Seq[Node] to use as children
  */
  private def createElem(aNode: Node, children: Seq[Node]): Node = 
    new Elem(aNode.prefix, aNode.label, aNode.attributes, aNode.scope, children:_*)
  
  /** 
  * Copies Elem except it's children
  * @param aNode - source node to create new element
  */
  private def copyElem(aNode:Node): Node = 
    new Elem(aNode.prefix, aNode.label, aNode.attributes, aNode.scope)
  
  /** Adds specified attributes in to attributes set of the node */
  private def addAttributes(aNode: Node, attributes: MetaData): Node = {
    new Elem(aNode.prefix, aNode.label, aNode.attributes append attributes, aNode.scope, aNode.child:_*)
  }
  
  /** Parallel merge of 2 xml trees */
  private def recursiveMerge(srcSubTree: Node, destSubTree: Node): Node = {
    val srcKids = srcSubTree.child
    val res: Node = null
    var children: Seq[Node] = destSubTree.child
    var previousIndex : int = 0
    val exception = new UnsupportedOperationException ("Index out of bounds")
    // kids comes from src children from desination
    if (srcKids.length>0) {
      srcKids.map[Unit] ((aKid) => {
        val aKidAttributes = aKid.attributes
        val currentChild = children.
          filter(aChild => aChild.label == aKid.label).
          filter((aNode) => aNode.isInstanceOf[Elem])
        val currentChildWithAttr = children.
          filter(aChild => aChild.label == aKid.label && aChild.attributes == aKidAttributes).
          filter((aNode) => aNode.isInstanceOf[Elem])
        // println(" * " + aKid.label + " current ch " + destSubTree)
        
        if (currentChildWithAttr.isEmpty) {
          // println(" - not found child with attributes")
          // crate element in dest
          if (currentChild.isEmpty) {
            if (srcKids.indexOf(aKid) == 0 && srcKids.size > 1) {
              // assume we received new first element in children sequence
              children = insertBefore (children, previousIndex, aKid)
            } else {
              children = insertAfter (children, previousIndex, aKid)
            }
            previousIndex = children.indexOf(aKid)
          } else {
            // recreate child
            val currentIndex = children.indexOf(currentChild.headOption.getOrElse(throw exception))
            previousIndex = currentIndex
            val childrenSize = children.size
            if (currentIndex == 0) {
              children = recursiveMerge(aKid, addAttributes(currentChild.headOption.getOrElse(throw exception), aKidAttributes)) ++ 
                children.slice(currentIndex+1, childrenSize)
            } else if (currentIndex >= children.size) {
              children = children.slice(0, currentIndex) ++ 
                recursiveMerge(aKid, addAttributes(currentChild.headOption.getOrElse(throw exception), aKidAttributes))
            } else {
              children = children.slice(0,currentIndex) ++ 
                recursiveMerge(aKid, addAttributes(currentChild.headOption.getOrElse(throw exception), aKidAttributes)) ++
                children.slice(currentIndex+1, childrenSize)
            }
          } 
        } else {
          // println(" + found child with attributes ")
          val currentIndex = children.indexOf(currentChild.headOption.getOrElse(throw exception))
          previousIndex = currentIndex
          val childrenSize = children.size
          if (currentIndex == 0) {
            children = recursiveMerge(aKid, currentChild.headOption.getOrElse(throw exception)) ++ 
              children.slice(currentIndex+1, childrenSize)
          } else if (currentIndex >= children.size) {
            children = children.slice(0, currentIndex) ++ recursiveMerge(aKid, currentChild(0))
          } else {
            children = children.slice(0,currentIndex) ++ 
              recursiveMerge(aKid, currentChild.headOption.getOrElse(throw exception)) ++
              children.slice(currentIndex+1, childrenSize)
          }
        }
        // println(" e ch " + children)
      })
    }
    createElem(destSubTree, children)
  }
  
  /**
    Compounds new sequence of nodes inserting specified newNode before specified position
    @param seq - Source sequence of Nodes
    @param index - int index where to put new Node
    @param newNod - Node to insert
    */
  private def insertBefore(seq: Seq[Node], index: int, newNode: Node): Seq[Node] = {
    var res : Seq[Node] = seq
    val seqSize = seq.size
    if (index<0 || index> seqSize) throw new IndexOutOfBoundsException(" Index " + index + " is out of bounds " + seqSize)
    if (index == 0) {
      res = newNode ++ res
    } else if (index >= seqSize) {
      res = res ++ newNode
    } else {
      res = res.slice(0,index) ++ 
      newNode ++
      res.slice(index, seqSize)
    }
    res
  }
  
  /**
    Compounds new sequence of nodes inserting specified newNode after specified position
    @param seq - Source sequence of Nodes
    @param index - int index where to put new Node
    @param newNod - Node to insert
    */
  private def insertAfter(seq: Seq[Node], index: int, newNode: Node): Seq[Node] = {
    var res : Seq[Node] = seq
    val seqSize = seq.size
    if (index<0 || index> seqSize) throw new IndexOutOfBoundsException(" Index " + index + " is out of bounds " + seqSize)
    if (index >= seqSize) {
      res = res ++ newNode
    } else {
      res = res.slice(0,index+1) ++ 
      newNode ++
      res.slice(index+1, seqSize)
    }
    res
  }
  
  /** Reverses recursively the xml tree 
  @param subTree - Node to reverse
  */
  private def recursiveReverse(subTree: Node): Node = {
    var children: Seq[Node] = subTree.child.reverse
    children = children.map[Node] ((aNode) => {
      recursiveReverse(aNode)
    })
    createElem(subTree, children)
  }
  
}

