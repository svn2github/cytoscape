// GeneProductXmlReader
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

import cytoscape.data.GeneProduct;
//-------------------------------------------------------------------------
public class GeneProductXmlReader { 
  File xmlFile;
//-------------------------------------------------------------------------
public GeneProductXmlReader (File xmlFile)
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
  Vector geneProducts = new Vector (children.size ());
  ListIterator iterator = children.listIterator ();

  while (iterator.hasNext ()) {
    String symbol = "";
    String molecularFunction = "";
    String cellularComponent = "";
    String biologicalProcess = "";
    Element geneProduct = (Element) iterator.next ();

      // symbol is required:  force exception if missing
    symbol = geneProduct.getChild("symbol").getText().trim();

    Element mf = geneProduct.getChild("molecularFunction");
    Element cc = geneProduct.getChild("cellularComponent");
    Element bp = geneProduct.getChild("biologicalProcess");
    //if (s != null) symbol = s.getText().trim();

    if (mf != null) molecularFunction = mf.getText().trim();
    if (cc != null) cellularComponent = cc.getText().trim();
    if (bp != null) biologicalProcess = bp.getText().trim();
    GeneProduct gp = new GeneProduct (symbol, molecularFunction,
                                      cellularComponent, biologicalProcess);
    geneProducts.addElement(gp);
    }

  return geneProducts;

} // read
//-------------------------------------------------------------------------
} // class GeneProductXmlReader
