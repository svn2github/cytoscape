// CytoscapeConfig.java:  a class to handle run-time configuration of luca
//------------------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape;
//------------------------------------------------------------------------------------------
import java.io.*;
import java.util.*;

// import cytoscape.plugins.activePaths.data.ActivePathFinderParameters;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
//------------------------------------------------------------------------------------------
/**
 * handles the parsing of, and access to, command line arguments for cytoscape
 */
public class CytoscapeConfig {

  protected String argSpecificationString = "n:j:g:b:i:he:vW;";

  protected String [] commandLineArguments;
  protected boolean helpRequested = false;
  protected boolean inputsError = false;
  protected boolean displayVersion = false;
  protected String geometryFilename = null;
  protected String bioDataDirectory = null;
  protected String expressionFilename = null;
  protected String interactionsFilename = null;
  protected Vector nodeAttributeFilenames = new Vector ();
  protected Vector edgeAttributeFilenames = new Vector ();

  protected boolean activePathParametersPresent = false;
  // protected ActivePathFinderParameters activePathParameters;
  protected StringBuffer errorMessages = new StringBuffer ();
    // system and user property files use the same name
  protected Properties props;

//------------------------------------------------------------------------------------------
public CytoscapeConfig (String [] args)
{
  commandLineArguments = args;
  // activePathParameters = new ActivePathFinderParameters ();
  parseArgs ();
  props = readProperties ();

}
//------------------------------------------------------------------------------------------
public String getGeometryFilename ()
{
  return geometryFilename;
}
//------------------------------------------------------------------------------------------
public String getExpressionFilename ()
{
  return expressionFilename;
}
//------------------------------------------------------------------------------------------
public String getBioDataDirectory ()
{
  return bioDataDirectory;
}
//------------------------------------------------------------------------------------------
public String getInteractionsFilename ()
{
  return interactionsFilename;
}
//------------------------------------------------------------------------------------------
public int getNumberOfNodeAttributeFiles ()
{
  return nodeAttributeFilenames.size ();
}
//------------------------------------------------------------------------------------------
public int getNumberOfEdgeAttributeFiles ()
{
  return edgeAttributeFilenames.size ();
}
//------------------------------------------------------------------------------------------
public String [] getNodeAttributeFilenames ()
{
  return (String []) nodeAttributeFilenames.toArray (new String [0]);
}
//------------------------------------------------------------------------------------------
public String [] getEdgeAttributeFilenames ()
{
  return (String []) edgeAttributeFilenames.toArray (new String [0]);
}
//------------------------------------------------------------------------------------------
public String [] getAllDataFileNames ()
{
  Vector allFileNames = new Vector ();
  String [] nodeAttributeFiles = getNodeAttributeFilenames ();
  String [] edgeAttributeFiles = getEdgeAttributeFilenames ();

  for (int n=0; n < nodeAttributeFiles.length; n++)
    allFileNames.add (nodeAttributeFiles [n]);

  for (int e=0; e < edgeAttributeFiles.length; e++)
    allFileNames.add (edgeAttributeFiles [e]);

  if (geometryFilename != null)
    allFileNames.add (geometryFilename);

  if (interactionsFilename != null)
    allFileNames.add (interactionsFilename);

  if (expressionFilename != null)
    allFileNames.add (expressionFilename);

  return (String []) allFileNames.toArray (new String [0]);

} // getAllDataFileNames
//------------------------------------------------------------------------------------------
public String [] getAllDataFileExtensions ()
{
  String [] fullNames = getAllDataFileNames ();
  Vector allExtensions = new Vector ();

  for (int i=0; i < fullNames.length; i++) {
    String filename = fullNames [i];
    int positionOfLastDot = filename.lastIndexOf (".");
    if (positionOfLastDot > 0) {
      String extension = filename.substring (positionOfLastDot + 1);
      if (!allExtensions.contains (extension))
        allExtensions.add (extension);
      } // if
    } // for i

  return (String []) allExtensions.toArray (new String [0]);

} // getAllDataFileExtensions
//------------------------------------------------------------------------------------------
public boolean helpRequested ()
{
  return helpRequested;
}
//------------------------------------------------------------------------------------------
public boolean inputsError ()
{
  return inputsError;
}
//------------------------------------------------------------------------------------------
public boolean displayVersion ()
{
  return displayVersion;
}
//------------------------------------------------------------------------------------------
public boolean activePathParametersPresent ()
{
  return activePathParametersPresent;
}
//------------------------------------------------------------------------------------------
//public ActivePathFinderParameters getActivePathParameters ()
//{
//  return activePathParameters;
//}
//------------------------------------------------------------------------------------------
public Properties getProperties ()
{
  return props;
}
//------------------------------------------------------------------------------------------
/**
 * read (if possible) properties from CYTOSCAPE_HOME/cytoscape.props, 
 * from HOME/cytoscape.props, and  finally from PWD/cytoscape.props.
 * the latter properties (when they are duplicates) superceed previous ones
 * in the absence of any props, hard-coded defaults within various classes 
 * will be used.
 */
protected Properties readProperties ()
{
  Properties systemProps = null;
  Properties userGeneralProps = null;
  Properties userSpecialProps = null;
  String propsFileName = "cytoscape.props"; // there may be 3 copies of this

  File propsFile = createFile (System.getProperty ("CYTOSCAPE_HOME"), propsFileName);
  if (propsFile != null)
    systemProps = readOnePropertyFile (null, propsFile);

  File userGeneralPropsFile = createFile (System.getProperty ("user.home"), propsFileName);
  if (userGeneralPropsFile != null)
    userGeneralProps = readOnePropertyFile (systemProps, userGeneralPropsFile);

  File userSpecialPropsFile = createFile  (System.getProperty ("user.dir"), propsFileName);
  if (userSpecialPropsFile != null)
    userGeneralProps = readOnePropertyFile (userGeneralProps, userSpecialPropsFile);

  if (userSpecialProps != null)
    return userSpecialProps;
  else if (userGeneralProps != null)
    return userGeneralProps;
  else if (systemProps != null)
    return systemProps;
  else
    return new Properties ();


} // readProperties
//------------------------------------------------------------------------------------------
/**
 * return a File which is known to exist, and is readable.
 */
private File createFile (String directory, String filename)
{
  if (directory != null) {
    File result = new File (directory, filename);
    if (result.canRead ())
      return result;
    }

  return null;

} // createFile
//------------------------------------------------------------------------------------------
/**
 *  read properties from the named file, combining with previously read properties
 *  when possible.
 *  @param priorProps  previously-read properties of a more general status
 *  @param propsFile   a property file which is guaranteed to be readable
 */
private Properties readOnePropertyFile (Properties priorProps, File propsFile)
{
  Properties newProps = new Properties ();

  if (priorProps != null)
    newProps =  new Properties (priorProps);

  try {
    FileInputStream in = new FileInputStream (propsFile);
    newProps.load (in);
    } // try
  catch (FileNotFoundException ignore) {;}
  catch (IOException ignore) {;}

  return newProps;

} // readOnePropertyFile
//------------------------------------------------------------------------------------------
protected void parseArgs ()
{
  helpRequested = false;
  boolean argsError = false;
  String tmp;

    // use LongOpt command line arguments for the ActivePathFinderParameters
    // the presence of any one of these sets 'activePathParam
  LongOpt [] longOpts = new LongOpt [7];
  longOpts[0] = new LongOpt ("APsig", LongOpt.REQUIRED_ARGUMENT, null, 0); // significance 
  longOpts[1] = new LongOpt ("APt0",  LongOpt.REQUIRED_ARGUMENT, null, 1); // initial temp
  longOpts[2] = new LongOpt ("APtf",  LongOpt.REQUIRED_ARGUMENT, null, 2); // final temp
  longOpts[3] = new LongOpt ("APni",  LongOpt.REQUIRED_ARGUMENT, null, 3); // iterations
  longOpts[4] = new LongOpt ("APnp",  LongOpt.REQUIRED_ARGUMENT, null, 4); // number of paths
  longOpts[5] = new LongOpt ("APdi",  LongOpt.REQUIRED_ARGUMENT, null, 5); // display interval
  longOpts[6] = new LongOpt ("APrs",  LongOpt.REQUIRED_ARGUMENT, null, 6); // random seed

  if (commandLineArguments == null || commandLineArguments.length == 0)
    return;

  Getopt g = new Getopt ("cytoscape", commandLineArguments, argSpecificationString, longOpts);
  g.setOpterr (false); // We'll do our own error handling

  int c;
  while ((c = g.getopt ()) != -1) {
   switch (c) {
      /*********************************************
     case 0:
       tmp = g.getOptarg ();
       try {
         activePathParameters.setSignificanceThreshold (Double.parseDouble (tmp));
         activePathParametersPresent = true;
         }
       catch (NumberFormatException e) {
         inputsError = true;
         System.err.println ("illegal value for --APsig: " + tmp);
         }
       break;
     case 1:
       tmp = g.getOptarg ();
       try {
         activePathParameters.setInitialTemperature (Double.parseDouble (tmp));
         activePathParametersPresent = true;
         }
       catch (NumberFormatException e) {
         inputsError = true;
         System.err.println ("illegal value for --APt0: " + tmp);
         }
       break;
     case 2:
       tmp = g.getOptarg ();
       try {
         activePathParameters.setFinalTemperature (Double.parseDouble (tmp));;
         activePathParametersPresent = true;
         }
       catch (NumberFormatException e) {
         inputsError = true;
         System.err.println ("illegal value for --APtf: " + tmp);
         }
       break;
     case 3:
       tmp = g.getOptarg ();
       try {
         activePathParameters.setTotalIterations (Integer.parseInt (tmp));
         activePathParametersPresent = true;
         }
       catch (NumberFormatException e) {
         inputsError = true;
         System.err.println ("illegal value for --APni: " + tmp);
         }
       break;
     case 4:
       tmp = g.getOptarg ();
       try {
         activePathParameters.setNumberOfPaths (Integer.parseInt (tmp));
         activePathParametersPresent = true;
         }
       catch (NumberFormatException e) {
         inputsError = true;
         System.err.println ("illegal value for --APnp: " + tmp);
         }
       break;
     case 5:
       tmp = g.getOptarg ();
       try {
         activePathParameters.setDisplayInterval (Integer.parseInt (tmp));
         activePathParametersPresent = true;
         }
       catch (NumberFormatException e) {
         inputsError = true;
         System.err.println ("illegal value for --APdi: " + tmp);
         }
       break;
     case 6:
       tmp = g.getOptarg ();
       try {
         activePathParameters.setRandomSeed (Integer.parseInt (tmp));
         activePathParametersPresent = true;
         }
       catch (NumberFormatException e) {
         inputsError = true;
         System.err.println ("illegal value for --APrs: " + tmp);
         }
       break;
     ****************************/
     case 'n':
       nodeAttributeFilenames.add (g.getOptarg ());
       break;
     case 'j':
       edgeAttributeFilenames.add (g.getOptarg ());
       break;
     case 'g':
       geometryFilename = g.getOptarg ();
       break;
     case 'b':
       bioDataDirectory = g.getOptarg ();
       break;
     case 'i':
       interactionsFilename = g.getOptarg ();
       break;
     case 'e':
       expressionFilename = g.getOptarg ();
       break;
     case 'h':
       helpRequested = true;
       break;
     case 'v':
       displayVersion = true;
       break;
     case '?':
          System.out.println ("The option '" + (char)g.getOptopt() + 
                           "' is not valid");
          break;
     default:
       System.err.println ("unexpected argument: " + c);
       inputsError = true;
       break;
     } // switch on c
    }   

  if (!inputsError)
    inputsError = !legalArguments ();

} // parseArgs
//---------------------------------------------------------------------------------
protected boolean legalArguments ()
{
  boolean legal = true;
  
    // make sure there is just one source for the graph
  if ((geometryFilename != null) && (interactionsFilename != null)) {
    errorMessages.append (" -  geometry & interactions both specify a graph: use only one\n");
    legal = false;
    }

    // if the command line indicates that active paths are to be calculated, then
    // a few other prerequisites must be satisfied:
    //    - there has to be expression data
    //    - there has to be a graph

  if (activePathParametersPresent) {
     if (expressionFilename == null) {
       errorMessages.append (" - you must provide expression data in order to find active paths\n");
       legal = false;
       }
     if (geometryFilename == null && interactionsFilename == null) {
       errorMessages.append (" - you must provide a geometry or an interactions file in\n");
       errorMessages.append ("   in order to find active paths\n");
       legal = false;
       }
     } // if activePathParametersPresent
 
  return legal;

} // legalArguments
//---------------------------------------------------------------------------------
public String getErrorMessages ()
{
  return errorMessages.toString ();

}
//---------------------------------------------------------------------------------
public String getUsage ()
{
   StringBuffer sb = new StringBuffer ();
   String programName = "cytoscape";
   sb.append ("usage: ");
   sb.append (programName);
   sb.append (" [optional arguments]");
   sb.append ("\n\n");

   sb.append ("\n      optional arguments\n");
   sb.append ("      ------------------\n");
   sb.append (" -g  <geometry file name>          (xxxx.gml)\n");
   sb.append (" -b  <bioData directory>           (./biodata\n");
   sb.append (" -i  <interactions filename>       (yyyy.intr)\n");
   sb.append (" -e  <expression filename>         (zzz.mrna)\n");
   sb.append (" -n  <nodeAttributes filename>     (zero or more)\n");
   sb.append (" -j  <edgeAttributes filename>     (zero or more)\n");
   sb.append ("\n");
   sb.append ("      -------- active paths parameters\n");
   sb.append ("      (with one or more present, path finding starts automatically)\n\n");
   sb.append (" --APsig  <significance threshold>  (default:  20.0)\n");
   sb.append (" --APt0   <initial temperarture>    (default:  10.0)\n");
   sb.append (" --APtf   <final temperature>       (default:  0.0)\n");
   sb.append (" --APni   <number of iterations>    (default:  2500)\n");
   sb.append (" --APnp   <number of paths to find> (default:  5)\n");
   sb.append (" --APdi   <display update interval> (default:  500)\n");
   sb.append (" --APrs   <random seed>             (default:  pseudo-random, from system clock)\n");
   sb.append ("\n");

   sb.append (" -h  (display usage\n");
   sb.append (" -v  (display version)\n");

   return sb.toString ();

} // getUsage
//---------------------------------------------------------------------------------
public String toString ()
{
   StringBuffer sb = new StringBuffer ();
   sb.append ("---------- requested options:\n");
   sb.append ("            geometry file: " + geometryFilename + "\n");
   sb.append ("        interactions file: " + interactionsFilename + "\n");
   sb.append ("          expression file: " + expressionFilename + "\n");
   sb.append ("         bioDataDirectory: " + bioDataDirectory + "\n");
 
   for (int i=0; i < nodeAttributeFilenames.size (); i++)
     sb.append ("        nodeAttributeFile: " + (String) nodeAttributeFilenames.get(i) + "\n");

  for (int i=0; i < edgeAttributeFilenames.size (); i++)
     sb.append ("        edgeAttributeFile: " + (String) edgeAttributeFilenames.get(i) + "\n");
   
  //if (activePathParametersPresent) {
  // sb.append ("     ----- active paths\n");
   // sb.append (activePathParameters);
  // }

  return sb.toString ();

} // toString 
//---------------------------------------------------------------------------------
} // class CytoscapeConfig
