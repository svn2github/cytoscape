package cytoscape;

import giny.model.*;
import cytoscape.giny.Node;
import cytoscape.giny.CytoscapeRootGraph;

public class CyNode extends Node {

  
  /**
   * Creates a new CytoscapeNode
   * @param root_graph_index the index of this node in the RootGraph
   * @param root_graph the RootGraph that this node is in
   */
  public CyNode ( int root_graph_index, RootGraph root_graph ) {
    this( root_graph_index, root_graph, null ); 
  }

  /**
   * Creates a new CytoscapeNode
   * @param root_graph_index the index of this node in the RootGraph
   * @param root_graph the RootGraph that this node is in
   */
  public CyNode ( int root_graph_index, 
                         RootGraph root_graph, 
                         CyNetwork network ) {
    this.rootGraphIndex = root_graph_index;
    this.rootGraph = ( CytoscapeRootGraph )root_graph;    
    this.network = network;
  }

  //========================================//
  // CytoscapeNode methods
  //========================================//

   /**
   * Gets the Identifier for this Node, this is often used 
   * for getting the Visible Label
   */
  public String getIdentifier () {
    if ( identifier == null ) {
      return ( new Integer( rootGraphIndex )).toString();
    } 
    return identifier;
  }
  
  /**
   * Sets the Identifier for this Node, this is often used 
   * for getting the Visible Label
   */
  public boolean setIdentifier ( String new_id ) {
    identifier = new_id;
    return true;
  }
  
  /**
   * Gets the Identifier for this Node, this is often used 
   * for getting the Visible Label
   */
  public String toString () {
    return getIdentifier();
  }

  /**
   * Returns the Network that consists of all of this nodes meta children
   */
  public CyNetwork getNetwork () {
    return ( CyNetwork )getGraphPerspective();
  }
 
  /**
   * The UID for this node. Use it to get info from NetworkData via
   * the NetworkData.getNodeData( int, string ) methods
   */
  public int getUniqueIdentifier () {
    return rootGraphIndex;
  }

  /**
   * Makes the nodes and edges in the given network meta-children
   * of this node <B>in addition</B> to the meta-children it 
   * already has.
   */
  public boolean setNetwork ( CyNetwork new_network ) {
    return setGraphPerspective( new_network );
  }
   
   
       
  
} 
