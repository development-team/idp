package org.idp.kb.applicationgenerator

import org.idp.kb.model.domain.Module
import org.apache.log4j.Logger
import java.io.{File, IOException, FileWriter, BufferedWriter}

/**
 * @author Max
 * @date 18.12.2009
 * Time 22:52:56
 */

object ModuleWriter {
  val log = Logger.getLogger(this.getClass)

  /**
   * Writes the module representation to the file specified in the module if any
   */
  def apply(rootDir: File, module: Module): Option[File] = {
    //get the file from the module
    var res: Option[File] = None
    module.getFile match {
      case Some(file) => {
        val fileInDir = new File(rootDir, file.getPath)
        val out = new BufferedWriter(new FileWriter(fileInDir))
        try {
          val moduleString = (ModuleGenerator(module))(module)
          // log debug moduleString
          out.write(moduleString);
          res = Some(file)
        } catch {
          case e: IOException => log error e.getMessage
        } finally {
          out.close();
        }
      }
      case None => log debug "The absence of file in the module " + module.OWLURIString + " silently ignored."
    }
    res
  }
}