// SelectedSubGraphFactory: 
//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape;
//------------------------------------------------------------------------------
import y.base.*;
import y.view.*;
import java.util.*;
//------------------------------------------------------------------------------
/**
 * creates a new graph, using canonical names when available, with 
 * appropriate node and edge attributes, based upon nodes selected in the
 * parentGraph;
 */
public class SelectedSubGraphFactory {

  Graph2D parentGraph;
  Graph2D subGraph;
  GraphObjAttributes parentNodeAttributes, parentEdgeAttributes;
  GraphObjAttributes newNodeAttributes, newEdgeAttributes;
  HashMap parentNameMap = new HashMap ();  // maps from commonName to canonicalName

//------------------------------------------------------------------------------
public SelectedSubGraphFactory (Graph2D parentGraph, GraphObjAttributes nodeAttributes,
                         GraphObjAttributes edgeAttributes) 
{

  this.parentGraph = parentGraph;
  this.parentNodeAttributes = nodeAttributes;
  this.parentEdgeAttributes = edgeAttributes;

  NodeCursor nc = parentGraph.selectedNodes (); 

  for (nc.toFirst (); nc.ok (); nc.next ()) { 
    String canonicalName = parentNodeAttributes.getCanonicalName (nc.node ());
    if (canonicalName != null) {
      String commonName = (String) parentNodeAttributes.getValue ("commonName", canonicalName);
      if (commonName != null) 
         parentNameMap.put (commonName, canonicalName);
      } // if
    } // for nc

  EdgeCursor ec = parentGraph.selectedEdges (); 

  nc.toFirst ();
  subGraph = new Graph2D (parentGraph, nc);  // creates a copy
  Node [] newNodes = subGraph.getNodeArray ();
  // System.out.println ("nodes in new subgraph: " + newNodes.length);

  newNodeAttributes = (GraphObjAttributes) parentNodeAttributes.clone ();
  newNodeAttributes.clearNameMap ();
    
  for (int i=0; i < newNodes.length; i++) {
    Node newNode = newNodes [i];
    String commonName = subGraph.getLabelText (newNode);
    String canonicalName = (String) parentNameMap.get (commonName);
    NodeRealizer r = subGraph.getRealizer (newNode);
    r.setLabelText (canonicalName);
    newNodeAttributes.addNameMapping (canonicalName, newNode);
    // System.out.println (" new graph, commonName: " + commonName + "   canonical: " + canonicalName); 
    }

  newEdgeAttributes = (GraphObjAttributes) parentEdgeAttributes.clone ();

} // ctor
//------------------------------------------------------------------------------
public Graph2D getSubGraph () { return subGraph; }
public GraphObjAttributes getNodeAttributes () { return newNodeAttributes; }
public GraphObjAttributes getEdgeAttributes () { return newEdgeAttributes; }
//------------------------------------------------------------------------------
} // class SelectedSubGraphFactory
