package cytoscape.giny;

import giny.model.*;
import cytoscape.*;
import fing.model.*;
import cern.colt.map.*;

import cytoscape.util.intr.*;
import java.util.Collection;

import giny.model.Node;
import giny.model.Edge;

public class CytoscapeFingRootGraph  
  extends FingExtensibleRootGraph 
  implements  CytoscapeRootGraph {


  public CytoscapeFingRootGraph () {
    super( new CyNodeDepot(),
           new CyEdgeDepot() );
  }

  public CyNetwork createNetwork ( Collection nodes, Collection edges ) {
    Node[] node = ( Node[] )  nodes.toArray( new Node[] {} );
    Edge[] edge = ( Edge[] )  nodes.toArray( new Edge[] {} );
    return  createNetwork( node, edge ) ;

  }
  /**
   * Creates a new Network
   */
  public CyNetwork createNetwork ( Node[] nodes, Edge[] edges ) {

    final Node[] nodeArr = ((nodes != null) ? nodes : new Node[0]);
    final Edge[] edgeArr = ((edges != null) ? edges : new Edge[0]);
    final RootGraph root = this;
    try {
      return new FingCyNetwork
        (this,
         new IntIterator() {
           private int index = 0;
           public boolean hasNext() { return index < nodeArr.length; }
           public int nextInt() {
             if (nodeArr[index] == null ||
                 nodeArr[index].getRootGraph() != root)
               throw new IllegalArgumentException();
             return nodeArr[index++].getRootGraphIndex(); } },
         new IntIterator() {
           private int index = 0;
           public boolean hasNext() { return index < edgeArr.length; }
           public int nextInt() {
             if (edgeArr[index] == null ||
                 edgeArr[index].getRootGraph() != root)
               throw new IllegalArgumentException();
             return edgeArr[index++].getRootGraphIndex(); } }); }
    catch (IllegalArgumentException exc) { return null; } 
  }

  /**
   * Uses Code copied from ColtRootGraph to create a new Network.
   */
  public CyNetwork createNetwork ( int[] nodeInx, int[] edgeInx ) {
    if (nodeInx == null) nodeInx = new int[0];
    if (edgeInx == null) edgeInx = new int[0];
    try { return new FingCyNetwork
            (this, new ArrayIntIterator(nodeInx, 0, nodeInx.length),
             new ArrayIntIterator(edgeInx, 0, edgeInx.length)); }
    catch (IllegalArgumentException exc) { return null; } 
  }
}
  


