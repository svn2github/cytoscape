// GeneSynonymXmlReader
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

import cytoscape.data.GeneSynonym;
//-------------------------------------------------------------------------
public class GeneSynonymXmlReader { 
  File xmlFile;
//-------------------------------------------------------------------------
public GeneSynonymXmlReader (File xmlFile)
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
  Vector geneSynonyms = new Vector (children.size ());
  ListIterator iterator = children.listIterator ();

  while (iterator.hasNext ()) {
    Element geneSynonym = (Element) iterator.next ();
    String name = geneSynonym.getAttribute ("name").getValue ();
    String value= geneSynonym.getAttribute ("value").getValue ();
    GeneSynonym gs = new GeneSynonym (name, value);
    geneSynonyms.addElement(gs);
    }

  return geneSynonyms;

} // read
//-------------------------------------------------------------------------
} // class GeneSynonymXmlReader
