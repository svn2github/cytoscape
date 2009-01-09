package cytoscape;

import giny.model.Node;
import giny.model.Edge;
import giny.model.GraphPerspective;
import java.util.*;

import cytoscape.data.ExpressionData;
import cytoscape.data.CytoscapeData;
import cytoscape.data.FlagFilter;
import cytoscape.data.FlagEventListener;

/**
 *CyNetwork is the primary class for algorithm writing.&nbsp; All
algorithms should take a CyNetwork as input, and do their best to only
use the API of CyNetwork.&nbsp; Plugins that want to affect the display
of a graph can look into using CyNetworkView as well.<br>
<br>
A CyNetwork can create Nodes or Edges.&nbsp; Any Nodes or Edges that
wish to be added to a CyNetwork firt need to be created in <span
 style="font-style: italic;">Cytoscape.</span>&nbsp; <br>
<br>
The methods that are defined by CyNetwork mostly deal with data
integration and flagging of nodes/edges.&nbsp; All methods that deal
with graph traversal are part of the inherited API of the
GraphPerspective class.&nbsp; Links to which can be found at the bottom
of the methods list.&nbsp; <br>
<br>
In general, all methods are supported for working with Nodes/Edges as
objects, and as indices.<br>
 */
public interface CyNetwork extends GraphPerspective {

  /**
   * Can Change
   */
  public String getTitle ();

  
  /**
   * Can Change
   */
  public void setTitle ( String new_id );

  /**
   * Can't Change
   */
  public String getIdentifier ();

  
  /**
   * Can't Change
   */
  public String setIdentifier ( String new_id );



  //----------------------------------------//
  // Network Methods
  //----------------------------------------//
  
  /**
   * Appends all of the nodes and edges in the given Network to 
   * this Network
   */
  public void appendNetwork ( CyNetwork network );


  
  //------------------------------//
  // Listener Methods
  //------------------------------//
  
  /**
   * A new Network should be made instead.  
   * @see #appendNetwork 
   * @deprecated
   */
  public void setNewGraphFrom(CyNetwork newNetwork, boolean replaceAttributes);

   /**
   * @deprecated
   */
  public void beginActivity(String callerID) ;
    
  /**
   * @deprecated
    */
  public void endActivity(String callerID) ;
    
  /**
   * @deprecated
    */
  public boolean isStateClear() ;
    
  /**
   * @deprecated
     */
  public void forceClear(String callerID) ;


  /**
   * Registers the argument as a listener to this object. Does nothing if
   * the argument is already a listener.
   */
  public void addCyNetworkListener ( CyNetworkListener listener ) ;

  /**
   * Removes the argument from the set of listeners for this object. Returns
   * true if the argument was a listener before this call, false otherwise.
   */
  public boolean removeCyNetworkListener ( CyNetworkListener listener );
  /**
   * Returns the set of listeners registered with this object.
   */
  public Set getCyNetworkListeners ();
    

  //----------------------------------------//
  // Data Access Methods
  //----------------------------------------//

  /**
   * @deprecated
   * This should not be used by any user-code
   */
  public  CytoscapeData getNodeData ();
  
  /**
   * @deprecated
   * This should not be used by any user-code
   */
  public  CytoscapeData getEdgeData ();

  /**
   * @deprecated @see{getNetworkData}
   * Returns the expression data object associated with this network.
   */
  public ExpressionData getExpressionData () ;
  
  /**
   * @deprecated
   * Sets the expression data object associated with this network.
   */
  public void setExpressionData ( ExpressionData newData );

  /**
   * @deprecated
   */
  public CytoscapeData getNodeAttributes();

  /**
   * @deprecated
   */
  public CytoscapeData getEdgeAttributes();

  /**
   * @deprecated
   */
  public GraphPerspective getGraphPerspective();

  // /**
//    * Returns the default object for flagging graph objects.
//    */
//   public FlagFilter getFlagger();

  //--------------------//
  // Flagging 
  
  public void flagAllNodes () ;

  public void flagAllEdges ()  ;
  
  public void unFlagAllNodes ()  ;

  public void unFlagAllEdges ()  ;

  /**
   * Flags a node
   */
  public void setFlagged ( Node node, boolean state )  ;

  /**
   * Flag a group of node
   */
  public void setFlaggedNodes ( Collection nodes, boolean state )  ;

  /**
   * Flag a group of nodes using their indices
   */
  public void  setFlaggedNodes( int[] nodes, boolean state )  ;

  /**
   * Flags a edge
   */
  public void setFlagged ( Edge edge, boolean state )  ;

  /**
   * Flag a group of edge
   */
  public void setFlaggedEdges ( Collection edges, boolean state ) ; 

  /**
   * Flag a group of edges using their indices
   */
  public void  setFlaggedEdges( int[] edges, boolean state )  ;

  public boolean isFlagged ( Node node )  ;
   
  public boolean isFlagged ( Edge edge )  ;

  public Set getFlaggedNodes ()  ;

  public Set getFlaggedEdges ()  ;

  public int[] getFlaggedNodeIndicesArray ()  ;

  public int[] getFlaggedEdgeIndicesArray ()  ;
  
  public void addFlagEventListener (FlagEventListener listener);
  
  public void removeFlagEventListener (FlagEventListener listener);
  
  public FlagFilter getFlagger ();


  //--------------------//
  // Member Data
   
  //get

  /**
   * Return the requested Attribute for the given Node
   * @param node the given CyNode
   * @param attribute the name of the requested attribute
   * @return the value for the give node, for the given attribute
   */
  public Object getNodeAttributeValue ( Node node, String attribute );

  /**
   * Return the requested Attribute for the given Node
   */
  public Object getNodeAttributeValue ( int node, String attribute );

  /**
   * Return the requested Attribute for the given Edge
   */
  public Object getEdgeAttributeValue ( Edge edge, String attribute );

  /**
   * Return the requested Attribute for the given Edge
   */
  public Object getEdgeAttributeValue ( int edge, String attribute );
 
  /**
   * Return all availble Attributes for the Nodes in this CyNetwork
   */
  public String[] getNodeAttributesList ();

  /**
   * Return all available Attributes for the given Nodes
   */
  public String[] getNodeAttributesList ( Node[] nodes);

  /**
   * Return all availble Attributes for the Edges in this CyNetwork
   */
  public String[] getEdgeAttributesList ();

  /**
   * Return all available Attributes for the given Edges
   */
  public String[] getNodeAttributesList ( Edge[] edges );

  //set

  /**
   * Return the requested Attribute for the given Node
   * @param node the given CyNode
   * @param attribute the name of the requested attribute
   * @param value the value to be set
   * @return if it overwrites a previous value
   */
  public boolean setNodeAttributeValue ( Node node, String attribute, Object value );

  /**
   * Return the requested Attribute for the given Node
   */
  public boolean setNodeAttributeValue ( int node, String attribute, Object value );

  /**
   * Return the requested Attribute for the given Edge
   */
  public boolean setEdgeAttributeValue ( Edge edge, String attribute, Object value );

  /**
   * Return the requested Attribute for the given Edge
   */
  public boolean setEdgeAttributeValue ( int edge, String attribute, Object value );
 
  /**
   * Deletes the attribute with the given name from node attributes
   */
  public void deleteNodeAttribute (String attribute);

  /**
   * Deleted the attribute with the given name from edge attributes
   */
  public void deleteEdgeAttribute (String attribute);

  //--------------------//
  // Network Client Data
  
  /**
   * Networks can support client data.
   * @param data_name the name of this client data
   */
  public void putClientData ( String data_name, Object data );

  /**
   * Get a list of all currently available ClientData objects
   */
  public Collection getClientDataNames ();
  
  /**
   * Get Some client data
   * @param data_name the data to get
   */
  public Object getClientData ( String data_name );
    

  //----------------------------------------//
  // Node and Edge creation/deletion
  //----------------------------------------//

  //--------------------//
  // Nodes

  /**
   * Add a node to this Network that already exists in 
   * Cytoscape
   * @return the Network Index of this node
   */
  public int addNode ( int cytoscape_node );

  /**
   * Add a node to this Network that already exists in 
   * Cytoscape
   * @return the Network Index of this node
   */
  public CyNode addNode ( Node cytoscape_node );
 
  /**
   * This will remove this node from the Network. However,
   * unless forced, it will remain in Cytoscape to be possibly
   * resused by another Network in the future.
   * @param set_remove true removes this node from all of Cytoscape, 
   *                   false lets it be used by other CyNetworks
   * @return true if the node is still present in Cytoscape 
   *          ( i.e. in another Network )
   */
  public boolean removeNode ( int node_index, boolean set_remove );

  


  //--------------------//
  // Edges

  /**
   * Add a edge to this Network that already exists in 
   * Cytoscape
   * @return the Network Index of this edge
   */
  public int addEdge ( int cytoscape_edge );

  /**
   * Add a edge to this Network that already exists in 
   * Cytoscape
   * @return the Network Index of this edge
   */
  public CyEdge addEdge ( Edge cytoscape_edge );
 
  /**
   * This will remove this edge from the Network. However,
   * unless forced, it will remain in Cytoscape to be possibly
   * resused by another Network in the future.
   * @param set_remove true removes this edge from all of Cytoscape, 
   *                   false lets it be used by other CyNetworks
   * @return true if the edge is still present in Cytoscape 
   *          ( i.e. in another Network )
   */
  public boolean removeEdge ( int edge_index, boolean set_remove );

}
