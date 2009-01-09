
/*
  File: CyProject.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies
  
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.
  
  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute 
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute 
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute 
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

 //-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape;
//-------------------------------------------------------------------------
import java.util.*;
import java.io.File;

import cytoscape.data.readers.TextFileReader;
import cytoscape.data.readers.TextJarReader;
//-------------------------------------------------------------------------
/**
 * This class is constructed from a project file, and contains string
 * values associated with various meaningful variable names that identify
 * the component of a CyNetwork object or various customization options.
 *
 * See the documentation for the readProjectFile method for the
 * recognized file formats.
 * @deprecated Will be removed October 2006. CyProject is not used in
 * any way in the core.  Cytoscape Sessions now encapsulate all of this
 * functionality.
 */
public class CyProject {
    protected String projectFilename = null;

    protected String interactionsFilename = null;
    protected String geometryFilename = null;
    protected String expressionFilename = null;
    protected List nodeAttributeFilenames = new Vector();
    protected List edgeAttributeFilenames = new Vector();
    protected String bioDataDirectory = null;
    protected String defaultSpeciesName = null;
    protected String [] layoutStrategies = {
            "organic", "hierarchical", "embedded", "circular"};
    protected String defaultLayoutStrategy = layoutStrategies [0];
    protected String projectPropsFileName = null;
    protected String projectVizmapPropsFileName = null;
    protected boolean canonicalize = true;
    protected String[] otherArgs;
//-------------------------------------------------------------------------
/**
 * Constructs a Project object by reading the file at the supplied
 * location, by calling readProjectFile with the argument.
 */
public CyProject(String fileLocation) {
    readProjectFile(fileLocation);
}
//-------------------------------------------------------------------------
/**
 * @deprecated Will be removed October 2006. CyProject is not used in
 * any way in the core.  Cytoscape Sessions now encapsulate all of this
 * functionality.
 */
public String getProjectFilename() {return projectFilename;}
//-------------------------------------------------------------------------
/**
 * @deprecated Will be removed October 2006. CyProject is not used in
 * any way in the core.  Cytoscape Sessions now encapsulate all of this
 * functionality.
 */
public String getInteractionsFilename() {return interactionsFilename;}
//-------------------------------------------------------------------------
/**
 * @deprecated Will be removed October 2006. CyProject is not used in
 * any way in the core.  Cytoscape Sessions now encapsulate all of this
 * functionality.
 */
public String getGeometryFilename() {return geometryFilename;}
//-------------------------------------------------------------------------
/**
 * @deprecated Will be removed October 2006. CyProject is not used in
 * any way in the core.  Cytoscape Sessions now encapsulate all of this
 * functionality.
 */
public String getExpressionFilename() {return expressionFilename;}
//-------------------------------------------------------------------------
/**
 * @deprecated Will be removed October 2006. CyProject is not used in
 * any way in the core.  Cytoscape Sessions now encapsulate all of this
 * functionality.
 */
public int getNumberOfNodeAttributeFiles() {
    return nodeAttributeFilenames.size();
}
//-------------------------------------------------------------------------
/**
 * @deprecated Will be removed October 2006. CyProject is not used in
 * any way in the core.  Cytoscape Sessions now encapsulate all of this
 * functionality.
 */
public int getNumberOfEdgeAttributeFiles() {
  return edgeAttributeFilenames.size();
}
//-------------------------------------------------------------------------
/**
 * @deprecated Will be removed October 2006. CyProject is not used in
 * any way in the core.  Cytoscape Sessions now encapsulate all of this
 * functionality.
 */
public String[] getNodeAttributeFilenames() {
  return (String[]) nodeAttributeFilenames.toArray(new String[0]);
}
//-------------------------------------------------------------------------
/**
 * @deprecated Will be removed October 2006. CyProject is not used in
 * any way in the core.  Cytoscape Sessions now encapsulate all of this
 * functionality.
 */
public String[] getEdgeAttributeFilenames() {
  return (String[]) edgeAttributeFilenames.toArray(new String[0]);
}
//-------------------------------------------------------------------------
/**
 * @deprecated Will be removed October 2006. CyProject is not used in
 * any way in the core.  Cytoscape Sessions now encapsulate all of this
 * functionality.
 */
public String getBioDataDirectory() {return bioDataDirectory;}
//-------------------------------------------------------------------------
/**
 * @deprecated Will be removed October 2006. CyProject is not used in
 * any way in the core.  Cytoscape Sessions now encapsulate all of this
 * functionality.
 */
public String getDefaultSpeciesName() {return defaultSpeciesName;}
//-------------------------------------------------------------------------
/**
 * @deprecated Will be removed October 2006. CyProject is not used in
 * any way in the core.  Cytoscape Sessions now encapsulate all of this
 * functionality.
 */
public String getDefaultLayoutStrategy() {return defaultLayoutStrategy;}
//-------------------------------------------------------------------------
/**
 * @deprecated Will be removed October 2006. CyProject is not used in
 * any way in the core.  Cytoscape Sessions now encapsulate all of this
 * functionality.
 */
public String getProjectPropsFileName() {return projectPropsFileName;}
//-------------------------------------------------------------------------
/**
 * @deprecated Will be removed October 2006. CyProject is not used in
 * any way in the core.  Cytoscape Sessions now encapsulate all of this
 * functionality.
 */
public String getProjectVizmapPropsFileName() {return projectVizmapPropsFileName;}
//-------------------------------------------------------------------------
/**
 * @deprecated Will be removed October 2006. CyProject is not used in
 * any way in the core.  Cytoscape Sessions now encapsulate all of this
 * functionality.
 */
public boolean getCanonicalize() {return canonicalize;}
//-------------------------------------------------------------------------
/**
 * @deprecated Will be removed October 2006. CyProject is not used in
 * any way in the core.  Cytoscape Sessions now encapsulate all of this
 * functionality.
 */
public String[] getOtherArgs() {return otherArgs;}
//-------------------------------------------------------------------------
/**
 * Reads the project file at the specified location. Currently, the
 * location can either be a local file (in which case the location
 * should be an absolute or relative path to the file), or a file in
 * a jar archive, in which case the location should be the name of
 * the file in the jar archive, prefaced with the header string
 * "jar://". For example:
 *
 * fileLocation = "/users/me/projects/project"  (local text file)
 * fileLocation = "jar://jarProject"  (jar archive, presumably in path)
 *
 * Support could be added for reading files from web pages using the
 * TextHttpReader helper class, but I'm not sure what issues would be
 * involved with this.
 *
 * In any case, the actual file should be a project file, which is
 * a sequence of lines of the general form "key=value", where the
 * keys are particular recognized strings. For most keys, any
 * occurances after the first are ignored. The exceptions are the
 * "noa", "eda", and "arg" keys, for which multiple occurances are
 * saved as an array of String values. Other lines are ignored, so
 * reading a file that isn't a project file simply produces empty
 * values for every field.
 * The known keys are as follows:
 *
 * "sif"  specifies the location of a graph file in interactions format
 * "gml"  specifies the location of a graph file in GML format.
 *        it is not strictly an error to include both the "sif" and
 *        "gml" keys in the project file, but many classes that try
 *        to use this information do not expect both
 * "noa"  specifies the location of a node attributes file.
 * "eda"  specifies the location of an edge attributes file.
 * "expr" specifies the location of an expression data file
 * "dataserver"  specifies the location of a data server
 * "species"  specifies a default species name
 * "layout"  specifies a default layout algorithm
 * "props"   specifies the locatipn of a customization properties file
 *           (ex: cytoscape.props)
 * "vprops"  specifies the location of a properties file containing
 *           specifications of visual mappings (ex: vizmap.props)
 * "canonicalize"  determines whether the data server is used to get
 *                 canonical names from the supplied names that may be
 *                 common names or synonyms
 * "args"  a catch=all category for any other String values that may
 *         be significant to some class that uses this project information
 *
 *
 * For information on the formats of the different data files mentioned
 * here and the classes that read them, see the following classes:
 *
 * graph interactions file format: cytoscape.data.readers.InteractionsReader
 * graph GML file format: cytoscape.data.readers.GMLReader
 * customization properties file: cytoscape.CytoscapeConfig
 * visual mappings properties file: cytoscape.visual.CalculatorIO
 * @deprecated Will be removed October 2006. CyProject is not used in
 * any way in the core.  Cytoscape Sessions now encapsulate all of this
 * functionality.
 */
protected void readProjectFile(String fileLocation) {
    boolean readingFromJar = false;
    String rawText;
    File projectFileDirectoryAbsolute = null;
    
    try {
        if (fileLocation.trim().startsWith ("jar://")) {
            readingFromJar = true;
            TextJarReader reader = new TextJarReader (projectFilename);
            reader.read ();
            rawText = reader.getText ();
        } else {
            File projectFile = new File (fileLocation);
            TextFileReader reader = new TextFileReader (projectFile.getPath ());
            reader.read ();
            rawText = reader.getText ();
            projectFileDirectoryAbsolute = projectFile.getAbsoluteFile().getParentFile ();
        }
    } catch (Exception e0) {
        throw new IllegalArgumentException ("cannot read project file: " + projectFilename);
    }
    
    projectFilename = fileLocation;
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
    String [] exprFiles = parseProjectFileText (lines, "expr");
    String [] dataServers = parseProjectFileText (lines, "dataServer");
    String [] speciesEntries = parseProjectFileText (lines, "species");
    String [] defaultLayouts = parseProjectFileText (lines, "layout");
    String [] propsFiles = parseProjectFileText (lines, "props");
    String [] vizmapPropsFiles = parseProjectFileText (lines, "vprops");
    String [] canonicalization = parseProjectFileText (lines, "canonicalizeNames"); // whether or not canonicalization should be done
    String [] otherArgs = parseProjectFileText (lines, "arg");
    
    if (sifFiles.length >= 1) {
        if (readingFromJar)
            interactionsFilename = sifFiles [0];
            else
                interactionsFilename = absolutizeFilename (projectFileDirectoryAbsolute, sifFiles [0]);
    }
    
    if (gmlFiles.length >= 1) {
        if (readingFromJar)
            geometryFilename = gmlFiles [0];
            else
                geometryFilename = absolutizeFilename (projectFileDirectoryAbsolute, gmlFiles [0]);
    }
    
    if (exprFiles.length >= 1) {
        if (readingFromJar)
            expressionFilename = exprFiles [0];
            else
                expressionFilename = absolutizeFilename (projectFileDirectoryAbsolute, exprFiles [0]);
    }
    
    for (int i=0; i < noaFiles.length; i++) {
        if (readingFromJar)
            nodeAttributeFilenames.add (noaFiles[i]);
            else      
                nodeAttributeFilenames.add (absolutizeFilename (projectFileDirectoryAbsolute, noaFiles [i]));
    }
    
    for (int i=0; i < edaFiles.length; i++) {
        if (readingFromJar)
            edgeAttributeFilenames.add (edaFiles[i]);
            else      
                edgeAttributeFilenames.add (absolutizeFilename (projectFileDirectoryAbsolute, edaFiles [i]));
    }
    
    if (dataServers.length >= 1) {
        String tmp = dataServers [0];
        if ((!tmp.startsWith ("rmi://")) && 
        (!tmp.startsWith ("jar://"))) {
            bioDataDirectory = absolutizeFilename (projectFileDirectoryAbsolute, tmp);
        }
        else
            bioDataDirectory = tmp;
    } // if dataServers.length > 0
    
    if (speciesEntries.length > 0) {
        defaultSpeciesName = speciesEntries [0];
    }
    
    if (defaultLayouts.length > 0) {
        defaultLayoutStrategy = defaultLayouts [0];
    }
    
    if (propsFiles.length >= 1) {
        projectPropsFileName = propsFiles [0];
    }
    
    if (vizmapPropsFiles.length >= 1) {
        projectVizmapPropsFileName = vizmapPropsFiles [0];
    }
    
    // Whether or not names in input files should be canonicalized
    if (canonicalization.length > 0){
        String canValue = canonicalization[0];
        if(canValue.equals("yes")){
            canonicalize = true;
        }else if(canValue.equals("no")){
            canonicalize = false;
        }
    }
    
    this.otherArgs = otherArgs;
    // this is how to pass info on to a plugin.
    /*
    if (otherArgs.length > 0) {
        int lenOld = argsCopy.length;
        int lenOther = otherArgs.length;
        int lenNew = lenOld+lenOther;
        // first make a copy that is exactly what we had before.
        String[] argsCopy2;
        argsCopy2 = new String [lenOld];
        System.arraycopy (argsCopy, 0, argsCopy2, 0, lenOld);
        // then make the new one.
        argsCopy = new String[lenNew];
        // then copy everything in.
        System.arraycopy (argsCopy2, 0, argsCopy, 0, lenOld);
        System.arraycopy (otherArgs, 0, argsCopy, lenOld, lenOther);
        //
        // arraycopy arguments, fyi:
        //
        // (Obj source, int sourceStart, Obj target, int targetStart, int size)
        //
    }
    */
    
} // readProjectFile
//---------------------------------------------------------------------------------
/**
 * Helper method to reconstruct the absolute path to a file.
 */
protected String absolutizeFilename (File parentDirectory, String filename)
{
  if (filename.trim().startsWith ("/"))
    return filename;
  else 
    return (new File (parentDirectory, filename)).getPath ();

}
//---------------------------------------------------------------------------------
/**
 * Helper method that, given an array of text lines, searches for lines
 * of the form "key=value", where the key is the second argument, and
 * returns an array of all the values.
 */
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
//---------------------------------------------------------------------------------
}

