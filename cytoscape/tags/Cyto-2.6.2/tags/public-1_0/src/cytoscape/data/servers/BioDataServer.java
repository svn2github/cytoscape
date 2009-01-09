// BioDataServer

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

//-----------------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------------
package cytoscape.data.servers;
//-----------------------------------------------------------------------------------------
import java.util.*;
import java.io.*;
import java.rmi.*;

import cytoscape.data.annotation.*;
import cytoscape.data.annotation.readers.*;
import cytoscape.data.synonyms.*;
import cytoscape.data.synonyms.readers.*;

import cytoscape.data.readers.TextFileReader;
import cytoscape.data.readers.TextJarReader;
//----------------------------------------------------------------------------------------
public class BioDataServer {
  protected BioDataServerInterface server;
//----------------------------------------------------------------------------------------
public BioDataServer (String serverName) throws Exception
{
  if (serverName.indexOf ("rmi://") >= 0)
    server = (BioDataServerInterface) Naming.lookup (serverName);
  else { // look for a readable file
    server = new BioDataServerRmi ();  // actually runs in process
    File fileTester = new File (serverName);
    if ((serverName.startsWith ("jar://")) ||
        (!fileTester.isDirectory () && fileTester.canRead ())) {
      String [] ontologyFiles = parseLoadFile (serverName, "ontology");
      String [] annotationFilenames = parseLoadFile (serverName, "annotation");
      loadAnnotationFiles (annotationFilenames, ontologyFiles);
      String [] thesaurusFilenames = parseLoadFile (serverName, "synonyms");
      loadThesaurusFiles (thesaurusFilenames);
      } // if a plausible candidate load file
    else {
      System.err.println ("could not read BioDataServer load file '" + serverName + "'");
      }
    } // else: look for a readable file


} // ctor
//----------------------------------------------------------------------------------------
protected String [] parseLoadFile (String filename, String key)
{
  String rawText;

  try {
    if (filename.trim().startsWith ("jar://")) {
      TextJarReader reader = new TextJarReader (filename);
      reader.read ();
      rawText = reader.getText ();
      }
    else {
      TextFileReader reader = new TextFileReader (filename);
      reader.read ();
      rawText = reader.getText ();
      }
    }
  catch (Exception e0) {
    System.err.println ("-- Exception while reading annotation server load file " + filename);
    System.err.println (e0.getMessage ());
    return new String [0];
    }

  String [] lines = rawText.split ("\n");

  Vector list = new Vector ();
  for (int i=0; i < lines.length; i++) {
    String line = lines [i].trim ();
    if (line.startsWith (key)) {
      String fileToRead = line.substring (line.indexOf ("=") + 1);
      list.add (fileToRead);
      } // if 
    } // for i

  return (String []) list.toArray (new String [0]);
  
} // parseLoadFile
//----------------------------------------------------------------------------------------
public BioDataServer () throws Exception
{
  server = new BioDataServerRmi ();

} // ctor
//----------------------------------------------------------------------------------------
public Ontology readOntologyFlatFile (String [] ontologyFilenames) throws Exception
// a quick hack.  this is called only if annotation & ontology are each flat files,
// which means they must be read separately.  and xml annotation file names its own
// ontology file, and the annotationXmlReader is responsible for loading its ontology.
{
  Ontology ontology = null;

  for (int i=0; i < ontologyFilenames.length; i++) {
    String filename = ontologyFilenames [i];
    if (filename.endsWith (".txt")) {
      OntologyFlatFileReader reader = new OntologyFlatFileReader (filename);
      ontology = reader.getOntology ();
      }
    } // for i

  return ontology;

} // loadOntologyFiles
//----------------------------------------------------------------------------------------
public void loadAnnotationFiles (String [] annotationFilenames, String [] ontologyFilenames) throws Exception
{
  Ontology ontology = readOntologyFlatFile (ontologyFilenames);

  for (int i=0; i < annotationFilenames.length; i++) {
    Annotation annotation;
    String filename = annotationFilenames [i];
    if (filename.endsWith (".txt")) {
      AnnotationFlatFileReader reader = new AnnotationFlatFileReader (filename);
      annotation = reader.getAnnotation ();
      annotation.setOntology (ontology);
      }
    else {
      File xmlFile = new File (annotationFilenames [i]);
      AnnotationXmlReader reader = new AnnotationXmlReader (xmlFile);
      annotation = reader.getAnnotation ();
      }
    server.addAnnotation (annotation);
    } // for i

} // loadAnnotationFiles
//----------------------------------------------------------------------------------------
public void loadThesaurusFiles (String [] thesaurusFilenames) throws Exception
{
  for (int i=0; i < thesaurusFilenames.length; i++) {
    String filename = thesaurusFilenames [i];
    ThesaurusFlatFileReader reader = new ThesaurusFlatFileReader (filename);
    Thesaurus thesaurus = reader.getThesaurus (); 
    server.addThesaurus (thesaurus.getSpecies (), thesaurus);
    }

} // loadThesaurusFiles
//----------------------------------------------------------------------------------------
public void clear ()
{
  try {
    server.clear ();
    }
  catch (Exception e) {
    System.err.println ("Error!  failed to clear");
    e.printStackTrace ();
    }

}
//----------------------------------------------------------------------------------------
public void addAnnotation (Annotation annotation)
{
  try {
    server.addAnnotation (annotation);
    }
  catch (Exception e) {
    System.err.println ("Error!  failed to add annotation " + annotation);
    e.printStackTrace ();
    }

}
//----------------------------------------------------------------------------------------
public int getAnnotationCount ()
{
  try {
    int count = server.getAnnotationCount ();
    return count;
    }
  catch (Exception e) {
    return 0;
    }

}
//----------------------------------------------------------------------------------------
public AnnotationDescription [] getAnnotationDescriptions () 
{
  try {
    return server.getAnnotationDescriptions ();
    }
  catch (Exception e) {
    return null;
    }
}
//----------------------------------------------------------------------------------------
public Annotation getAnnotation (String species, String curator, String type)
{
  try {
    return server.getAnnotation (species, curator, type);
    }
  catch (Exception e) {
    return null;
    }

}
//----------------------------------------------------------------------------------------
public Annotation getAnnotation (AnnotationDescription description)
{
  try {
    return server.getAnnotation (description);
    }
  catch (Exception e) {
    return null;
    }

}
//----------------------------------------------------------------------------------------
public int [] getClassifications (String species, String curator, String type, String entity) 
{
  try {
    return server.getClassifications (species, curator, type, entity);
    }
  catch (Exception e) {
    return null;
    }
}
//----------------------------------------------------------------------------------------
public int [] getClassifications (AnnotationDescription description, String entity)
{
  try {
    return server.getClassifications (description, entity);
    }
  catch (Exception e) {
    return null;
    }
}
//----------------------------------------------------------------------------------------
public String [][] getAllAnnotations (AnnotationDescription description, String entity)
{
  try {
    return server.getAllAnnotations (description, entity);
    }
  catch (Exception e) {
    return null;
    }
} 
//----------------------------------------------------------------------------------------
public String describe ()
{
  try {
    return server.describe ();
    }
  catch (Exception e) {
    return "error connecting to data server";
    }
}
//----------------------------------------------------------------------------------------
public void addThesaurus (String species, Thesaurus thesaurus) 
{
  try {
    server.addThesaurus (species, thesaurus);
    }
  catch (Exception e) {
    return;
    }

}
//----------------------------------------------------------------------------------------
public String getCanonicalName (String species, String commonName)
{
  try {
    return server.getCanonicalName (species, commonName);
    }
  catch (Exception e) {
    return null;
    }


}
//----------------------------------------------------------------------------------------
public String [] getAllCommonNames (String species, String commonName)
{
  try {
    return server.getAllCommonNames (species, commonName);
    }
  catch (Exception e) {
    return null;
    }


}
//----------------------------------------------------------------------------------------
public String getCommonName (String species, String canonicalName)
{
  try {
    return server.getCommonName (species, canonicalName);
    }
  catch (Exception e) {
    return null;
    }

}
//----------------------------------------------------------------------------------------
} // BioDataServer


