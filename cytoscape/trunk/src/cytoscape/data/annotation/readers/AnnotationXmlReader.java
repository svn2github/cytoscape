// AnnotationXmlReader.java
//------------------------------------------------------------------------------
// $Revision$  $Date$
//------------------------------------------------------------------------------
package cytoscape.data.annotation.readers;
//------------------------------------------------------------------------------
import java.io.*; 
import org.jdom.*; 
import org.jdom.input.*; 
import org.jdom.output.*; 
import java.util.Vector;
import java.util.List;
import java.util.ListIterator;

import cytoscape.data.annotation.*;
//-------------------------------------------------------------------------
public class AnnotationXmlReader { 
  File xmlFile;
  Annotation annotation;
  File directoryAbsolute;
//-------------------------------------------------------------------------
public AnnotationXmlReader (File xmlFile) throws Exception
{
  if (!xmlFile.canRead ()) {
    System.out.println ("---- data.annotation.readers.AnnotationXmlReader error, cannot read");
    System.out.println ("        " + xmlFile);
    throw new Exception ("cannot read input: " + xmlFile);
    }

  this.xmlFile = xmlFile;
  directoryAbsolute = xmlFile.getAbsoluteFile().getParentFile ();
  read ();
}
//-------------------------------------------------------------------------
private void read () throws Exception
{
  SAXBuilder builder = new SAXBuilder (); 
  Document doc = builder.build (xmlFile);
  Element root = doc.getRootElement ();

  String species = root.getAttributeValue ("species");
  String ontologyXmlFileName = root.getAttributeValue ("ontology");
  String annotationType = root.getAttributeValue ("type");

  File ontologyXmlFileAbsolutePath = new File (directoryAbsolute, 
                                               ontologyXmlFileName);

  if (!ontologyXmlFileAbsolutePath.canRead ()) {
    System.err.println ("annotation xml file must name its associated ontology xml file");
    System.err.println ("by giving its path relative to the actual location of the");
    System.err.println ("annotation xml file.\n");
    System.err.println ("could not find:");
    System.err.println ("  " + ontologyXmlFileAbsolutePath);
    throw new FileNotFoundException (ontologyXmlFileAbsolutePath.getPath ());
    }

  OntologyXmlReader oReader = new OntologyXmlReader (ontologyXmlFileAbsolutePath);
  Ontology ontology = oReader.getOntology ();

  annotation = new Annotation (species, annotationType, ontology);

  List children = root.getChildren ();
  ListIterator iterator = children.listIterator ();

  while (iterator.hasNext ()) {
    Element termElement = (Element) iterator.next ();
    String entityName = termElement.getChild("entity").getText().trim();
    String tmp  = termElement.getChild("id").getText().trim();
    int id = Integer.parseInt (tmp);
    annotation.add (entityName, id);
    }

} // read
//-------------------------------------------------------------------------
public Annotation getAnnotation ()
{
  return annotation;
}
//-------------------------------------------------------------------------
} // class AnnotationXmlReader
