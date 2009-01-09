// BindingPairsXmlReader
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

import cytoscape.data.BindingPair;
//-------------------------------------------------------------------------
public class BindingPairXmlReader { 
  File xmlFile;
//-------------------------------------------------------------------------
public BindingPairXmlReader (File xmlFile)
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
  Vector bindingPairs = new Vector (children.size ());
  ListIterator iterator = children.listIterator ();

  while (iterator.hasNext ()) {
    Element bindingPair = (Element) iterator.next ();
    String a = bindingPair.getChild("a").getText().trim();
    String b = bindingPair.getChild("b").getText().trim();
    String species = bindingPair.getChild ("species").getText ();
    // System.out.println (a + ":" + b + ":" + species);
    BindingPair bp = new BindingPair (a, b, species);
    bindingPairs.addElement(bp);
    }

  return bindingPairs;

} // read
//-------------------------------------------------------------------------
} // class BindingPairXmlReader
