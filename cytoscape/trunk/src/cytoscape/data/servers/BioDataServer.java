// BioDataServer
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
    if (!fileTester.isDirectory () && fileTester.canRead ()) {
      String [] annotationFilenames = parseLoadFile (serverName, "annotation");
      loadAnnotationFiles (annotationFilenames);
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
  TextFileReader reader = new TextFileReader (filename);
  reader.read ();
  String rawText = reader.getText ();
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
public void loadAnnotationFiles (String [] annotationFilenames) throws Exception
{
  for (int i=0; i < annotationFilenames.length; i++) {
    File xmlFile = new File (annotationFilenames [i]);
    AnnotationXmlReader reader = new AnnotationXmlReader (xmlFile);
    server.addAnnotation (reader.getAnnotation ());
    }

} // loadAnnotationFiles
//----------------------------------------------------------------------------------------
public void loadThesaurusFiles (String [] thesaurusFilenames) throws Exception
{
  for (int i=0; i < thesaurusFilenames.length; i++) {
    ThesaurusFlatFileReader reader = new ThesaurusFlatFileReader (thesaurusFilenames [i]);
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
