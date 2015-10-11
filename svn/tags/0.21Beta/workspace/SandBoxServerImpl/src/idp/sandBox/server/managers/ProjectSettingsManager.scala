package idp.sandBox.server.managers;

import scala.collection.immutable.{Map, HashMap, Set, HashSet}
import java.util.Properties
import java.io.{File, FileInputStream, FileOutputStream, IOException}
import org.apache.log4j.Logger
import idp.sandBox.server.constants.Constants

object ProjectSettingsManager {
  
  type PropertiesMap = Map[String, Properties]
  
  private val log = Logger.getLogger(this.getClass())
  private var projectPropertiesMap: PropertiesMap = new HashMap[String, Properties]
  private var changedProjectProperties: Set[String] = new HashSet[String]
  
  /**
    *   Gets properties of the specified project(project==project directory) and caches if not found in cache.
    * @param project - project name (now == project directory)
    * @param filename - name of project properties file in project directory
    * @return Properties
    */
  def get(project: String, filename: String): Properties = {
    val propsInStream = new FileInputStream(new File(project, filename))
    projectPropertiesMap.get(project).getOrElse({
      try {
        val properties = new Properties
        properties.load(propsInStream)
        projectPropertiesMap = projectPropertiesMap + new Pair(project, properties)
        properties
      } finally {
        propsInStream close
      }
    })
  }

  /**
  *  Sets specified batch of properties of specified project.
  * @param project - project name
  * @filename - name of properties file
  * @param name - property name
  * @param value - property value
  */
  def setProperties(project: String, filename: String, props: Properties) {
    projectPropertiesMap = projectPropertiesMap.update(project, props)
    changedProjectProperties = changedProjectProperties + project
    this.storeOne(project, filename)
  }
  
  /**
  *  Sets specified property of specified project.
  * @param project - project name
  * @filename - name of properties file
  * @param name - property name
  * @param value - property value
  */
  def setProperty(project: String, filename: String, name: String, value: String) {
    val properties = projectPropertiesMap.get(project).getOrElse({
      log.error("Could not find properties for project " + project + " possibly not logged")
      throw new IllegalArgumentException("Could not find properties for project " + project + " possibly not logged")
    })
    properties.setProperty(name, value)
    changedProjectProperties = changedProjectProperties + project
    this.storeOne(project, filename)
  }
  
  /**
   *  Sets specified property of specified project.
   * @param project - project name
   * @param name - property name
   * @param value - property value
   * @return - String value
   */
   def getProperty(project: String, filename: String, name: String, defaultValue: String): String = {
     val properties = projectPropertiesMap.get(project).getOrElse({
       log.warn("Could not find properties for project " + project + " possibly not logged")
       this.get(project, filename)
     })
     properties.getProperty(name, defaultValue)
   }
  
  /**
    *  Stores properties of specified project in specified file, if no properties found in cache does nothin, but logs and Warning.
    * @param project - project name (now == project directory)
    * @param filename - name of the project properties file
    */
  private def storeOne(project: String, filename: String) {
    val propertiesOutStream = new FileOutputStream(new File(project, filename))
    val properties = projectPropertiesMap.get(project).getOrElse({
      log.warn("Could not find properties for project " + project + " possibly not logged")
      return
    })
    try {
      properties.store(propertiesOutStream, "")
    } finally {
      propertiesOutStream close
    }
  }
  
  /**
    *  Stores changed properties files of projects.
    * @param - filename to store 
    */
  def storeChanged(filename: String) {
    this.changedProjectProperties.map((project) => {
      projectPropertiesMap.get(project) match {
        case Some(properties) => {
          this.storeOne(project, filename)
        } 
        case None => log.warn("Project: " + project + " properties not found")
      }
    })
  }
}
