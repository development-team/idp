package edu.cmu.minorthird.text;

import edu.cmu.minorthird.text.mixup.*;

import org.apache.log4j.Logger;
import java.io.*;
import java.util.*;

/**
 * Contains static methods to run annotators (either mixup or java code)
 * see runDependency(MonotonicTextLabels, String, String)
 * @author ksteppe
 * @deprecated Use the require mechanism instead.
 * deprecatd as of 3/9/2007
 */
public class Dependencies
{
  private static Logger log = Logger.getLogger(Dependencies.class);
  private static String configFile = "annotators.config";
  private static Properties providerProps;

  /**
   * This runs the given file or the default (from <code>configFile</code>) to generate
   * the requested annotations in the given labels.
   *
   * If no file is given and no default is registered then a null pointer will be thrown
   * Exceptions are converted to IllegalStateException objects
   *
   * If the file provided is a mixup program it runs the program.  However, this assumes that
   * if the program creates new levels using mixup's multilevel capabilities, that the program
   * itself will import any results back to the root level.  This method does not inspect any
   * levels created by the mixup program for annotations created.
   *
   * note here - could catch the annotate exception stuff
   *
   * @param labels TextLabels to place annotations in - must hold the text base to run against
   * @param reqAnnotation the name of the annotations requested
   * @param file file to run in order to get annotations, if null then the configuration file is
   *        checked for a default
   *
   */
  public static void runDependency(MonotonicTextLabels labels, String reqAnnotation, String file)
  {
    log.debug("runDependency : " + reqAnnotation + " : " + file);
    try
    {
      if (file == null)
        file = getDependency(reqAnnotation);

      //error - if (file==null)  throw "can't find annotator for " + reqAnnotation;
      if (file == null)
        throw new Exception("no annotator found for '" + reqAnnotation + "'");

      if (file.endsWith("mixup")) {
          File f = new File(file);
          InputStream inStream;
          if (f.exists())
              inStream = new FileInputStream(f);
          else
              inStream = Dependencies.class.getClassLoader().getResourceAsStream(file);
          
          log.debug("got stream " + inStream);
          byte[] chars = new byte[inStream.available()];
          inStream.read(chars);
          
          String program = new String(chars);
          
          log.info("Evaluating mixup program "+file+" to provide "+reqAnnotation);
          MixupProgram subProgram =  new MixupProgram(program);
          MixupInterpreter interp = new MixupInterpreter(subProgram);

          // Evaluate the loaded program here.  Note that this code assumes that the program will
          // export all results back to the root level if it creates any new new levels during 
          // it's execution.
          interp.eval(labels);

          if (!labels.isAnnotatedBy(reqAnnotation)) {
            throw new IllegalArgumentException("file "+file+" did not provide expected annotation type "+reqAnnotation);
          }
      }
      else // if (file.endsWith("java") || file.endsWith(("class")))
      {
        //run the java code
//        String className = file.substring(0, file.lastIndexOf('.'));
        Class clazz = Dependencies.class.getClassLoader().loadClass(file);
        Annotator annotator = (Annotator)clazz.newInstance();
        annotator.annotate(labels);
      }
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
      IllegalStateException exc = new IllegalStateException("error running mixup: " + file + ": " + e.getMessage());
      exc.setStackTrace(e.getStackTrace());
      throw exc;
    }
    catch (Mixup.ParseException e)
    {
      IllegalStateException exc = new IllegalStateException("error running mixup: " + file + ": " + e.getMessage());
      exc.setStackTrace(e.getStackTrace());
      throw exc;
    }
    catch (Exception e)
    {
      IllegalStateException exc;
      if (file != null)
        exc = new IllegalStateException("error loading " + file + ": " + e.getMessage());
      else
        exc = new IllegalStateException("error loading annotator: " + e.getMessage());
      exc.setStackTrace(e.getStackTrace());
      throw exc;
    }
  }

  /**
   *  Return the file required for this dependency
   * @param reqAnnotation
   * @return name of the default provider (file or java class) for the required
   *        annotation
   */
  protected static String getDependency(String reqAnnotation) throws IOException
  {

    if (providerProps == null)
    {  
      //read Properties file
      // There is a problem with ClassLoader's static getSystemResource method in java 1.4.2  
      // The following is a work around that seems to work
      InputStream inStream = Dependencies.class.getClassLoader().getResourceAsStream(configFile);
      // This is the preferred way to access system resources, but it is broken
      // ClassLoader.getSystemResourceAsStream(configFile);
      log.debug("in stream = " + inStream);
      if (inStream == null)
      { throw new IllegalStateException("can't find " + configFile + " on classpath"); }

      providerProps = new Properties();
      providerProps.load(inStream);

    }

    return providerProps.getProperty(reqAnnotation, reqAnnotation+".mixup");
  }
}
