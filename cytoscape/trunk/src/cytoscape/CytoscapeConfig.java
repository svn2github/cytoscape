// CytoscapeConfig.java:  a class to handle run-time configuration

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//------------------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape;
//------------------------------------------------------------------------------------------
import java.io.*;
import java.util.*;

import cytoscape.data.readers.*;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
//------------------------------------------------------------------------------------------
/**
 * handles the parsing of, and access to, command line arguments for cytoscape, and
 * the control of various features and attributes via 'cytoscape.props' files
 */
public class CytoscapeConfig {

  protected String argSpecificationString = "n:j:g:b:i:he:vWs:l:p:;";

  protected String [] commandLineArguments;
  protected String[] argsCopy;
  protected boolean helpRequested = false;
  protected boolean inputsError = false;
  protected boolean displayVersion = false;
  protected String geometryFilename = null;
  protected String bioDataDirectory = null;
  protected String expressionFilename = null;
  protected String projectFilename = null;
  protected String interactionsFilename = null;
  protected Vector nodeAttributeFilenames = new Vector ();
  protected Vector edgeAttributeFilenames = new Vector ();
  protected String defaultSpeciesName = null;
  protected File projectPropsFile = null;

  protected String [] layoutStrategies = {"organic", "hierarchical", "embedded", "circular"};
  protected String defaultLayoutStrategy = layoutStrategies [0];

  protected StringBuffer errorMessages = new StringBuffer ();
    // system and user property files use the same name
  protected Properties props;

//------------------------------------------------------------------------------------------
public CytoscapeConfig (String [] args)
{
    // make a copy of the args to parse here (getopt can mangle the array it parses)
  commandLineArguments = new String [args.length];
  System.arraycopy (args, 0, commandLineArguments, 0, args.length);
    // make a copy of the arguments for later use
  argsCopy = new String [args.length];
  System.arraycopy (args, 0, argsCopy, 0, args.length);

  parseArgs ();
  readProjectFile ();
  props = readProperties ();
  getConfigurationsFromProperties ();

} // ctor
//------------------------------------------------------------------------------------------
public String [] getArgs () 
{
  String [] returnVal = new String [argsCopy.length];
  System.arraycopy (argsCopy, 0, returnVal, 0, argsCopy.length);
  return returnVal;
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
public String getProjectFilename ()
{
  return projectFilename;
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
  System.out.println ("number of edge attributes: " + edgeAttributeFilenames.size ());
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
/**
 * Add the given node attributes filename (as per opened in gui).
 *
 * added by dramage 2002-08-21
 */
public void addNodeAttributeFilename (String filename) {
    if (!nodeAttributeFilenames.contains(filename))
	nodeAttributeFilenames.add(filename);
}
//------------------------------------------------------------------------------------------
/**
 * Add the given edge attributes filename (as per opened in gui).
 *
 * added by dramage 2002-08-21
 */
public void addEdgeAttributeFilename (String filename) {
    if (!edgeAttributeFilenames.contains(filename))
	edgeAttributeFilenames.add(filename);
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
public String getDefaultSpeciesName ()
{
  return defaultSpeciesName;
}
//------------------------------------------------------------------------------------------
public String getDefaultLayoutStrategy ()
{
  return defaultLayoutStrategy;
}
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
  Properties projectProps = null;

  String propsFileName = "cytoscape.props"; // there may be 3 copies of this

  File propsFile = createFile (System.getProperty ("CYTOSCAPE_HOME"), propsFileName);
  if (propsFile != null)
    systemProps = readOnePropertyFile (null, propsFile);

  File userGeneralPropsFile = createFile (System.getProperty ("user.home"), propsFileName);
  if (userGeneralPropsFile != null)
    userGeneralProps = readOnePropertyFile (systemProps, userGeneralPropsFile);

  File userSpecialPropsFile = createFile  (System.getProperty ("user.dir"), propsFileName);
  if (userSpecialPropsFile != null)
    userSpecialProps = readOnePropertyFile (userGeneralProps, userSpecialPropsFile);

  if (projectPropsFile != null) 
     projectProps = readOnePropertyFile (projectProps, projectPropsFile);

  /* we will return a valid Properties object; if any properties files
   * were found and read, we copy them in sequentially so that duplicate
   * keys in the users file overwrite the sytems defaults 
   */

  Properties fullProps = new Properties ();

  if (systemProps != null) 
     fullProps.putAll (systemProps);

  if (userGeneralProps != null) 
    fullProps.putAll (userGeneralProps);

  if (userSpecialProps != null) 
    fullProps.putAll (userSpecialProps);

  if (projectProps != null) 
    fullProps.putAll (projectProps);

  return fullProps;

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

  if (commandLineArguments == null || commandLineArguments.length == 0)
    return;

  LongOpt[] longopts = new LongOpt[0];
  Getopt g = new Getopt ("cytoscape", commandLineArguments, argSpecificationString, longopts);
  g.setOpterr (false); // We'll do our own error handling

  int c;
  while ((c = g.getopt ()) != -1) {
   switch (c) {
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
     case 'l':
       defaultLayoutStrategy = g.getOptarg ();
       break;
     case 'e':
       expressionFilename = g.getOptarg ();
       break;
     case 'h':
       helpRequested = true;
       break;
     case 'p':
       projectFilename = g.getOptarg ();
       break;
     case 's':
       defaultSpeciesName = g.getOptarg ();
       break;
     case 'v':
       displayVersion = true;
       break;
     case '?': // Optopt==0 indicates an unrecognized long option, which is reserved for plugins 
      int theOption = g.getOptopt();
      if (theOption != 0 )
        errorMessages.append ("The option '" + (char)theOption + "' is not valid\n");
       break;
     default:
       errorMessages.append ("unexpected argument: " + c + "\n");
       inputsError = true;
       break;
     } // switch on c
    }   

  if (!inputsError)
    inputsError = !legalArguments ();

} // parseArgs
//---------------------------------------------------------------------------------
/**
 *  any values read from properties may be overridden at the command line; be sure
 *  that 'parseArgs' is called later than this method
 */
protected void getConfigurationsFromProperties ()
{
  defaultLayoutStrategy = props.getProperty ("defaultLayoutStrategy", defaultLayoutStrategy);

}
//---------------------------------------------------------------------------------
/**
 *  a project file contains one or more lines, each of which is a key/value pair.
 *  by example (using every possible key): 
 * 
 *  <code>
 *   sif=galFiltered.sif
 *   gml=galFiltered.gml
 *   noa=nodeAttributes1.noa
 *   noa=nodeAttributes2.noa
 *   eda=edgeAttributes1.eda
 *   eda=edgeAttributes2.eda
 *   layout=hierarchical
 *   dataServer=rmi://hazel/yeast
 *   species=Saccharomyces cerevisiae
 *   props=/net/compbio/cytoscape/projects/galFiltered/cytoscape.props
 *  </code>
 *
 * further information:
 *  <ul>
 *   <li> Most of the possible entries are filenames.  If a filename is not absolute,
 *        then it is assumed to be relative to the parent path of the project file itself
 *   <li> Any entries on the command line, or in the various props files, override any
 *        found in the project file.  This most obviously applies to the species value.
 *   <li> It makes no sense to specify <em> both </em> a sif and a gml file, but 
 *        there is nothing here (yet) which catches that error.
 *   <li> Values set by properties files will override any values set here.  In some 
 *        cases this may not be desirable.
 *  </ul>
 *
 */
protected void readProjectFile ()
{
  if (projectFilename == null) return;
  File projectFile = new File (projectFilename);
  if (!projectFile.canRead ()) 
    throw new IllegalArgumentException ("cannot read project file: " + projectFilename);

  File directoryAbsolute = projectFile.getAbsoluteFile().getParentFile ();
  TextFileReader reader = new TextFileReader (projectFile.getPath ());
  reader.read ();
  String rawText = reader.getText ();
  String [] lines = rawText.split ("\n");

    // most entities name in a project file are singular:  species, sif or gml,
    // props, dataServer.  edge & node attribute files, however, are frequently
    // plural.  to support them, the helper method 'parseProjectFileText' returns
    // an array of Strings; only the first element in that array is used for
    // most entitites below

  String [] sifFiles = parseProjectFileText (lines, "sif");
  String [] gmlFiles = parseProjectFileText (lines, "gml");
  String [] noaFiles = parseProjectFileText (lines, "noa");
  String [] edaFiles = parseProjectFileText (lines, "eda");
  String [] dataServers = parseProjectFileText (lines, "dataServer");
  String [] speciesEntries = parseProjectFileText (lines, "species");
  String [] defaultLayouts = parseProjectFileText (lines, "layout");
  String [] propsFiles = parseProjectFileText (lines, "props");

  if (sifFiles.length >= 1) {
    interactionsFilename = absolutizeFilename (directoryAbsolute, sifFiles [0]);
    }

  if (gmlFiles.length >= 1) {
    geometryFilename = absolutizeFilename (directoryAbsolute, gmlFiles [0]);
    }

  for (int i=0; i < noaFiles.length; i++) {
    nodeAttributeFilenames.add (absolutizeFilename (directoryAbsolute, noaFiles [i]));
    }
    
  for (int i=0; i < edaFiles.length; i++) {
    edgeAttributeFilenames.add (absolutizeFilename (directoryAbsolute, edaFiles [i]));
    }
    
  if (dataServers.length >= 1) {
    String tmp = dataServers [0];
    if (!tmp.startsWith ("rmi://")) {
      bioDataDirectory = absolutizeFilename (directoryAbsolute, tmp);
      }
    else
      bioDataDirectory = tmp;
    } // if dataServers.length > 0

  if (speciesEntries.length > 0) {
    defaultSpeciesName = speciesEntries [0];
    }

  if (defaultLayouts.length > 0) {
    defaultLayoutStrategy = defaultLayouts [0];
    System.out.println ("readProject file, setting layout to: " + defaultLayoutStrategy);
    }

  if (propsFiles.length >= 1) {
    projectPropsFile = new File (absolutizeFilename (directoryAbsolute, propsFiles [0]));
    }


} // readProjectFile
//---------------------------------------------------------------------------------
protected String absolutizeFilename (File parentDirectory, String filename)
{
  if (filename.trim().startsWith ("/"))
    return filename;
  else 
    return (new File (parentDirectory, filename)).getPath ();

}
//---------------------------------------------------------------------------------
protected String [] parseProjectFileText (String [] lines, String key)
{
  Vector list = new Vector ();
  for (int i=0; i < lines.length; i++) {
    String line = lines [i].trim ();
    if (line.startsWith (key)) {
      String fileToRead = line.substring (line.indexOf ("=") + 1);
      list.add (fileToRead.trim());
      } // if 
    } // for i

  return (String []) list.toArray (new String [0]);
  
} // parseProjectFileText
//----------------------------------------------------------------------------------------
protected boolean legalArguments ()
{
  boolean legal = true;
  
    // make sure there is just one source for the graph
  if ((geometryFilename != null) && (interactionsFilename != null)) {
    errorMessages.append (" -  geometry & interactions both specify a graph: use only one\n");
    legal = false;
    }

  boolean illegalLayoutSelected = true;
  for (int i=0; i < layoutStrategies.length; i++) {
    if (defaultLayoutStrategy.equals (layoutStrategies [i])) {
      illegalLayoutSelected = false;
      break;
      }
    } // for i
  
  legal = legal && (!illegalLayoutSelected);  

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
   sb.append (" -b  <bioData directory>           (./biodata)\n");
   sb.append (" -i  <interactions filename>       (yyyy.intr)\n");
   sb.append (" -e  <expression filename>         (zzz.mrna)\n");
   sb.append (" -s  <default species name>        (\"Saccharomyces cerevisiae\")\n");
   sb.append (" -n  <nodeAttributes filename>     (zero or more)\n");
   sb.append (" -j  <edgeAttributes filename>     (zero or more)\n");
   sb.append (" -l  <layout strategy>             (organic|hierarchical|embedded|circular)\n");
   sb.append ("\n");

   sb.append (" -h  (display usage)\n");
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
   sb.append ("       defaultSpeciesName: " + defaultSpeciesName + "\n");
   sb.append ("    defaultLayoutStrategy: " + defaultLayoutStrategy + "\n");
 
   for (int i=0; i < nodeAttributeFilenames.size (); i++)
     sb.append ("        nodeAttributeFile: " + (String) nodeAttributeFilenames.get(i) + "\n");

  for (int i=0; i < edgeAttributeFilenames.size (); i++)
     sb.append ("        edgeAttributeFile: " + (String) edgeAttributeFilenames.get(i) + "\n");
   
  return sb.toString ();

} // toString 
//---------------------------------------------------------------------------------
} // class CytoscapeConfig


