// GoTermXmlReader.java:  read xml into a vector of objects, and return it
//------------------------------------------------------------------------------
// $Revision$  $Date$
//------------------------------------------------------------------------------
package cytoscape.data.readers;
//------------------------------------------------------------------------------
import java.io.*; 
import org.jdom.*; 
import org.jdom.input.*; 
import org.jdom.output.*; 
import java.util.Vector;
import java.util.List;
import java.util.ListIterator;

import cytoscape.data.GoTerm;
//-------------------------------------------------------------------------
public class GoTermXmlReader { 
  File xmlFile;
//-------------------------------------------------------------------------
public GoTermXmlReader (File xmlFile)
{
   this.xmlFile = xmlFile;
}
//-------------------------------------------------------------------------
public Vector read () throws Exception
{

  SAXBuilder builder = new SAXBuilder (); 
  Document doc = builder.build (xmlFile);
  Element root = doc.getRootElement ();
  List children = root.getChildren ();
  Vector goTerms = new Vector (children.size ());
  ListIterator iterator = children.listIterator ();

  while (iterator.hasNext ()) {
    Element goTerm = (Element) iterator.next ();
    String name = goTerm.getChild("name").getText().trim();
    String tmp  = goTerm.getChild("id").getText().trim();
    int id = Integer.parseInt (tmp);

    GoTerm gp = new GoTerm (name, id);

    List parents = goTerm.getChildren ("isa");
    ListIterator parentIterator = parents.listIterator ();
    while (parentIterator.hasNext ()) {
       Element parentElement = (Element) parentIterator.next ();
       String parentTmp = parentElement.getText().trim();
       int parent = Integer.parseInt (parentTmp);
       gp.addParent (parent);
       }
 
    List containers = goTerm.getChildren ("partof");
    ListIterator containerIterator = containers.listIterator ();
    while (containerIterator.hasNext ()) {
       Element containerElement = (Element) containerIterator.next ();
       String containerTmp = containerElement.getText().trim();
       int container = Integer.parseInt (containerTmp);
       gp.addContainer (container);
       }
 
   goTerms.addElement(gp);
    }

  return goTerms;

} // read
//-------------------------------------------------------------------------
} // class GoTermXmlReader
