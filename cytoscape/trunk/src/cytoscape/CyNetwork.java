package cytoscape;

import giny.model.GraphPerspective;
import java.util.*;

import cytoscape.data.ExpressionData;
import cytoscape.data.GraphObjAttributes;

// save for linking 
//javadoc -d API -link file:///users/xmas/CSBI/giny/API/ -private `find . -name "*.java" -print`

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
   * Appends all of the nodes and edges in teh given Network to 
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
  public GraphObjAttributes getNodeAttributes();

  /**
   * @deprecated
   */
  public GraphObjAttributes getEdgeAttributes();

  /**
   * @deprecated
   */
  public GraphPerspective getGraphPerspective();


  

  //--------------------//
  // Member Data
   
  /**
   * Return the requested Attribute for the given Node
   * @param node the given CyNode
   * @param attribute the name of the requested attribute
   * @return the value for the give node, for the given attribute
   */
  public Object getNodeAttributeValue ( CyNode node, String attribute );

  /**
   * Return the requested Attribute for the given Node
   */
  public Object getNodeAttributeValue ( int node, String attribute );

  /**
   * Return the requested Attribute for the given Edge
   */
  public Object getEdgeAttributeValue ( CyEdge edge, String attribute );

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
  public String[] getNodeAttributesList ( CyNode[] nodes);

  /**
   * Return all availble Attributes for the Edges in this CyNetwork
   */
  public String[] getEdgeAttributesList ();

  /**
   * Return all available Attributes for the given Edges
   */
  public String[] getNodeAttributesList ( CyEdge[] edges );


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
   * This method will create a new node.
   * @return the Cytoscape index of the created node 
   */
  public int createNode ();

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
  public CyNode addNode ( CyNode cytoscape_node );
 
  /**
   * Adds a node to this Network, by looking it up via the 
   * given attribute and value
   * @return the Network Index of this node
   */
  public int addNode ( String attribute, Object value );

  /**
   * This will remove this node from the Network. However,
   * unless forced, it will remain in Cytoscape to be possibly
   * resused by another Network in the future.
   * @param force force this node to be removed from all Networks
   * @return true if the node is still present in Cytoscape 
   *          ( i.e. in another Network )
   */
  public boolean removeNode ( int node_index, boolean force );

  //--------------------//
  // Edges

  /**
   * This method will create a new edge.
   * @param source the source node
   * @param target the target node
   * @param directed weather the edge should be directed
   * @return the Cytoscape index of the created edge 
   */
  public int createEdge ( int source, int target, boolean directed );

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
  public CyEdge addEdge ( CyEdge cytoscape_edge );
 
  /**
   * Adds a edge to this Network, by looking it up via the 
   * given attribute and value
   * @return the Network Index of this edge
   */
  public int addEdge ( String attribute, Object value );

  /**
   * This will remove this edge from the Network. However,
   * unless forced, it will remain in Cytoscape to be possibly
   * resused by another Network in the future.
   * @param force force this edge to be removed from all Networks
   * @return true if the edge is still present in Cytoscape 
   *          ( i.e. in another Network )
   */
  public boolean removeEdge ( int edge_index, boolean force );

}
