// OntologyXmlReader.java
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

import cytoscape.data.annotation.OntologyTerm;
import cytoscape.data.annotation.Ontology;
//-------------------------------------------------------------------------
public class OntologyXmlReader { 
  File xmlFile;
  Ontology ontology;
//-------------------------------------------------------------------------
public OntologyXmlReader (File xmlFile) throws Exception
{
  this.xmlFile = xmlFile;
  read ();
}
//-------------------------------------------------------------------------
private void read () throws Exception
{
  SAXBuilder builder = new SAXBuilder (); 
  Document doc = builder.build (xmlFile);
  Element root = doc.getRootElement ();

  String curator = root.getAttributeValue ("curator");
  String ontologyType = root.getAttributeValue ("type");

  ontology = new Ontology (curator, ontologyType);

  List children = root.getChildren ();
  ListIterator iterator = children.listIterator ();

  while (iterator.hasNext ()) {
    Element termElement = (Element) iterator.next ();
    String name = termElement.getChild("name").getText().trim();
    String tmp  = termElement.getChild("id").getText().trim();
    int id = Integer.parseInt (tmp);

    OntologyTerm term = new OntologyTerm (name, id);

    List parents = termElement.getChildren ("isa");
    ListIterator parentIterator = parents.listIterator ();
    while (parentIterator.hasNext ()) {
       Element parentElement = (Element) parentIterator.next ();
       String parentTmp = parentElement.getText().trim();
       int parent = Integer.parseInt (parentTmp);
       term.addParent (parent);
       }
 
    List containers = termElement.getChildren ("partof");
    ListIterator containerIterator = containers.listIterator ();
    while (containerIterator.hasNext ()) {
       Element containerElement = (Element) containerIterator.next ();
       String containerTmp = containerElement.getText().trim();
       int container = Integer.parseInt (containerTmp);
       term.addContainer (container);
       }
 
   ontology.add (term);
   }

} // read
//-------------------------------------------------------------------------
public Ontology getOntology ()
{
  return ontology;
}
//-------------------------------------------------------------------------
} // class OntologyXmlReader
