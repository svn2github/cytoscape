package cytoscape;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.giny.*;

import giny.model.*;
import giny.model.Node;
import giny.model.Edge;

import giny.filter.Filter;

import java.util.*;

public class CyNetwork
  implements
    CyNetworkInterface,
    GraphPerspective {

  // this is the GP that we wrap 
  private GraphPerspective gp;

  // this is our parent
  private CytoscapeRootGraph root;
  
  /**
   * The Network Listeners Set 
   */
  //TODO: implement the bean accepted way
  protected Set listeners = new HashSet();

  /**
   * The ClientData map
   */
  protected Map clientData;
  
  /**
   * The default object for flagging graph objects
   */
  protected FlagFilter flagger;

  int activityCount = 0;

  String identifier;
  String title;
  static int count = 0;


  public CyNetwork ( GraphPerspective gp, CytoscapeRootGraph root ) {
    this.gp = gp;
    this.root = root;
    identifier = "gp_"+count;
    title = identifier;
    count++;

    flagger = new FlagFilter(this);
    clientData = new HashMap();

  }


  ////////////////////////////////////////
  // GraphPerspectiveChangeListener


  ////////////////////
  // CyNetwork methods

  /**
   * Can Change
   */
  public String getTitle () {
    if ( title == null ) 
      return identifier;
    return title;
  }

  
  /**
   * Can Change
   */
  public void setTitle ( String new_id ) {
    title = new_id;
  }

  public String getIdentifier () {
    return identifier;
  }

  public String setIdentifier ( String new_id ) {
    identifier = new_id;
    return identifier;
  }


  //------------------------------//
  // Client Data
  //------------------------------//

  /**
   * Networks can support client data.
   * @param data_name the name of this client data
   */
  public void putClientData ( String data_name, Object data ) {
    clientData.put( data_name, data );
  }

  /**
   * Get a list of all currently available ClientData objects
   */
  public Collection getClientDataNames () {
    return clientData.keySet();
  }
  
  /**
   * Get Some client data
   * @param data_name the data to get
   */
  public Object getClientData ( String data_name ) {
    return clientData.get( data_name );
  }
  

  //------------------------------//
  // Deperecation
  //------------------------------//
 /**
   * @deprecated
   * This method should be called before reading or changing the data held
   * in this network object. A CyNetworkEvent of type CyNetworkEvent.BEGIN
   * will be fired to all listeners attached to this object, *only* if this
   * is the first begin of a nested stack of begin/end methods. No event
   * will be fired if a previous beginActivity call hasn't been closed by
   * a matching endActivity call.<P>
   *
   * The argument is simply a String that is useful for identifying the
   * caller of this method. This is provided for debugging purposes, in case
   * an algorithm forgets to provide a matching end method for each begin.
   */
  public void beginActivity(String callerID) {
    activityCount++;
    if (activityCount == 1) {fireEvent(CyNetworkEvent.BEGIN);}
  }
    
  /**
   * @deprecated
   * This method should be called when an algorithm is finished reading
   * or changing the data held in this network object. A CyNetworkEvent
   * of type CyNetworkEvent.END will be fired to listeners attached to
   * this object, *only* if this is the last end in a nested block of
   * begin/end calls.<P>
   *
   * The argument is a String for identifying the caller of this method.
   */
  public void endActivity(String callerID) {
    if (activityCount == 0) {return;} //discard calls without a matching begin
    activityCount--;
    if (activityCount == 0) {fireEvent(CyNetworkEvent.END);}
  }
    
  /**
   * @deprecated
   * This method returns true if the current state of this object is clear;
   * that is, if every beginActivity call has been followed by a matching
   * endActivity call, so that one can reasonably assume that no one is
   * currently working with the network.
   */
  public boolean isStateClear() {return (activityCount == 0);}
    
  /**
   * @deprecated
   * This method is provided as a failsafe in case an algorithm fails to
   * close its beginActivity calls without matching endActivity calls. If
   * the current state is not clear, this method resets this object to the
   * state of no activity and fires a CyNetworkEvent of type
   * CyNetworkEvent.END to all registered listeners.<P>
   *
   * If the current state is clear (i.e., there are no calls to beginActivity
   * without matching endActivity calls), then this method does nothing.<P>
   *
   * The argument is a String for identifying the caller of this method.
   */
  public void forceClear(String callerID) {
    if (activityCount > 0) {
      activityCount = 0;
      fireEvent(CyNetworkEvent.END);
    }
  }
  /**
   * @deprecated
   * use @link{Cytoscape.getRootGraph()} instead
   */
  public RootGraph getRootGraph() {
    return root;
  }
  
  /**
   * @deprecated
   * This <b>is a</b> GraphPerspective now! Therefore treat it as such.
   * This method will not be changed and will simply return itself, 
   * recasted as a GraphPerspective
   */
  public GraphPerspective getGraphPerspective() {
    return ( GraphPerspective )this;
  }

  /**
   * A new Network should be made instead.  
   * @see #appendNetwork 
   * @deprecated
   */
  public void setGraphPerspective( GraphPerspective perspective ) {
   
    // hide the current nodes 
    hideNodes( getNodeIndicesArray() );
    // hide the current edges
    hideEdges( getEdgeIndicesArray() );

    // restore the new nodes and edges
    restoreNodes( perspective.getNodeIndicesArray() );
    restoreEdges( perspective.getEdgeIndicesArray() );
    
    fireEvent(CyNetworkEvent.GRAPH_REPLACED);
  }

  /**
   * A new Network should be made instead.  
   * @see #appendNetwork 
   * @deprecated
   */
  public void setNewGraphFrom(CyNetwork newNetwork, boolean replaceAttributes) {
  
    // this will call the GRAPH_REPLACED event as well
    setGraphPerspective( newNetwork );
  }

  /**
   * Appends all of the nodes and edges in teh given Network to 
   * this Network
   */
  public void appendNetwork ( CyNetwork network ) {
    int[] nodes = network.getNodeIndicesArray();
    int[] edges = network.getEdgeIndicesArray();
    restoreNodes( nodes );
    restoreEdges( edges );
  }
    
  /**
   * @deprecated
   */
  public boolean getNeedsLayout () {
    return false;
  }
  
  /**
   * @deprecated
   */
  public void setNeedsLayout ( boolean needsLayout ) {
  }
    
   /**
   *@deprecated
   * Returns the node attributes data object for this network.
   */
  public GraphObjAttributes getNodeAttributes () {
    return ( GraphObjAttributes )Cytoscape.getNodeNetworkData();
  }
  
  /**
   * @deprecated
   * does nothing, all attributes are shared right now
   */
  public void setNodeAttributes ( GraphObjAttributes newNodeAttributes ) {
  }

  /**
   * @deprecated @see{getNetworkData}
   * Returns the edge attributes data object for this network.
   */
  public GraphObjAttributes getEdgeAttributes () {
    return ( GraphObjAttributes )Cytoscape.getEdgeNetworkData();
  }
  
  /**
   * @deprecated
   * does nothing, all attributes are shared right now
   */
  public void setEdgeAttributes ( GraphObjAttributes newEdgeAttributes ) {
  }

  /**
   * @deprecated @see{getNetworkData}
   * Returns the expression data object associated with this network.
   */
  public ExpressionData getExpressionData () {
    return Cytoscape.getExpressionData();
  }
  
  /**
   * @deprecated
   * Sets the expression data object associated with this network.
   */
  public void setExpressionData ( ExpressionData newData ) {
    //null?
    // use Cytoscape.loadExpressionData instead
  }
  
  /**
    * Returns the default object for flagging graph objects.
    */
  public FlagFilter getFlagger() {return flagger;}

  //--------------------//
  // Flagging 
  

  public void flagAllNodes () {
    flagger.flagAllNodes();
  }

  public void flagAllEdges () {
    flagger.flagAllEdges();
  }
  
  public void unFlagAllNodes () {
    flagger.unflagAllNodes();
  }

  public void unFlagAllEdges () {
    flagger.unflagAllEdges();
  }

  /**
   * Flags a node
   */
  public void setFlagged ( Node node, boolean state ) {
    flagger.setFlagged( node, state );
  }

  /**
   * Flag a group of node
   */
  public void setFlaggedNodes ( Collection nodes, boolean state ) {
    flagger.setFlaggedNodes( nodes, state );
  }

  /**
   * Flag a group of nodes using their indices
   */
  public void  setFlaggedNodes( int[] nodes, boolean state ) {
    for ( int i = 0; i < nodes.length; ++i ) {
      flagger.setFlagged( getNode( nodes[i] ), state );
    }
  }

  /**
   * Flags a edge
   */
  public void setFlagged ( Edge edge, boolean state ) {
    flagger.setFlagged( edge, state );
  }

  /**
   * Flag a group of edge
   */
  public void setFlaggedEdges ( Collection edges, boolean state ) {
    flagger.setFlaggedEdges( edges, state );
  }

  /**
   * Flag a group of edges using their indices
   */
  public void  setFlaggedEdges( int[] edges, boolean state ) {
    for ( int i = 0; i < edges.length; ++i ) {
      flagger.setFlagged( getEdge( edges[i] ), state );
    }
  }

  public boolean isFlagged ( Node node ) {
    return flagger.isFlagged( node );
  }

  public boolean isFlagged ( Edge edge ) {
    return flagger.isFlagged( edge );
  }

  public Set getFlaggedNodes () {
    return flagger.getFlaggedNodes();
  }

  public Set getFlaggedEdges () {
    return flagger.getFlaggedEdges();
  }

  public int[] getFlaggedNodeIndicesArray () {
    Set set = flagger.getFlaggedNodes();
    int[] nodes = new int[ set.size() ];
    int count = 0;
    for ( Iterator i = set.iterator(); i.hasNext(); count++) {
      nodes[count] = ( ( Node )i.next() ).getRootGraphIndex();
    }
    return nodes;
  }

  public int[] getFlaggedEdgeIndicesArray () {
    Set set = flagger.getFlaggedEdges();
    int[] edges = new int[ set.size() ];
    int count = 0;
    for ( Iterator i = set.iterator(); i.hasNext(); count++) {
      edges[count] = ( ( Edge )i.next() ).getRootGraphIndex();
    }
    return edges;
  }
  
  public void addFlagEventListener (FlagEventListener listener) {
    flagger.addFlagEventListener(listener);
  }
  
  public void removeFlagEventListener (FlagEventListener listener) {
    flagger.removeFlagEventListener(listener);
  }

  
   //----------------------------------------//
  // Data Access Methods
  //----------------------------------------//

  //--------------------//
  // Member Data

  // get
  
  /**
   * Return the requested Attribute for the given Node
   * @param node the given CyNode
   * @param attribute the name of the requested attribute
   * @return the value for the give node, for the given attribute
   */
  public Object getNodeAttributeValue ( Node node, String attribute ) {
    return Cytoscape.getNodeNetworkData().get( attribute, 
                                               Cytoscape.getNodeNetworkData().getCanonicalName( node ) );
  }

  /**
   * Return the requested Attribute for the given Node
   */
  public Object getNodeAttributeValue ( int node, String attribute ) {
    return Cytoscape.getNodeNetworkData().get( attribute, 
                                               Cytoscape.getNodeNetworkData().getCanonicalName( getNode( node ) ) );
  }

  /**
   * Return the requested Attribute for the given Edge
   */
  public Object getEdgeAttributeValue ( Edge edge, String attribute ) {
    return Cytoscape.getEdgeNetworkData().get( attribute, 
                                               Cytoscape.getEdgeNetworkData().getCanonicalName( edge ) );
  }

  /**
   * Return the requested Attribute for the given Edge
   */
  public Object getEdgeAttributeValue ( int edge, String attribute ) {
    return Cytoscape.getEdgeNetworkData().get( attribute, 
                                               Cytoscape.getEdgeNetworkData().getCanonicalName( getEdge( edge ) ) );
  }

  /**
   * Return all availble Attributes for the Nodes in this CyNetwork
   */
  public String[] getNodeAttributesList () {
    return Cytoscape.getNodeNetworkData().getAttributeNames();
  }
  
  /**
   * Return all available Attributes for the given Nodes
   */
  public String[] getNodeAttributesList ( Node[] nodes ) {
    return Cytoscape.getNodeNetworkData().getAttributeNames();
  }

  /**
   * Return all availble Attributes for the Edges in this CyNetwork
   */
  public String[] getEdgeAttributesList () {
    return Cytoscape.getEdgeNetworkData().getAttributeNames();
  }

  /**
   * Return all available Attributes for the given Edges
   */
  public String[] getNodeAttributesList ( Edge[] edges ) {
    return Cytoscape.getEdgeNetworkData().getAttributeNames();
  }


   /**
   * Return the requested Attribute for the given Node
   * @param node the given CyNode
   * @param attribute the name of the requested attribute
   * @param value the value to be set
   * @return if it overwrites a previous value
   */
  public boolean setNodeAttributeValue ( Node node, String attribute, Object value ) {
    return Cytoscape.getNodeNetworkData().set( attribute, 
                                               Cytoscape.
                                               getNodeNetworkData().
                                               getCanonicalName( node ),
                                               value );
    

  }

  /**
   * Return the requested Attribute for the given Node
   */
  public boolean setNodeAttributeValue ( int node, String attribute, Object value ) {
    return Cytoscape.getNodeNetworkData().set( attribute, 
                                               Cytoscape.
                                               getNodeNetworkData().
                                               getCanonicalName( getNode(node) ),
                                               value );
    

  }

  /**
   * Return the requested Attribute for the given Edge
   */
  public boolean setEdgeAttributeValue ( Edge edge, String attribute, Object value ) {
    return Cytoscape.getEdgeNetworkData().set( attribute, 
                                               Cytoscape.
                                               getEdgeNetworkData().
                                               getCanonicalName( edge ),
                                               value );
  }

  /**
   * Return the requested Attribute for the given Edge
   */
  public boolean setEdgeAttributeValue ( int edge, String attribute, Object value ) {
    return Cytoscape.getEdgeNetworkData().set( attribute, 
                                               Cytoscape.
                                               getEdgeNetworkData().
                                               getCanonicalName( getEdge(edge) ),
                                               value );
    

  }
 
  /**
   * Deletes the attribute with the given name from node attributes
   */
  public void deleteNodeAttribute (String attribute){
    Cytoscape.getNodeNetworkData().deleteAttribute(attribute);
  }
  
  /**
   * Deletes the attribute with the given name from edge attributes
   */
  public void deleteEdgeAttribute (String attribute){
    Cytoscape.getEdgeNetworkData().deleteAttribute(attribute);
  }




  //------------------------------//
  // Listener Methods
  //------------------------------//
  
    
  /**
   * Registers the argument as a listener to this object. Does nothing if
   * the argument is already a listener.
   */
  public void addCyNetworkListener ( CyNetworkListener listener ) {
    listeners.add(listener);
  }

  /**
   * Removes the argument from the set of listeners for this object. Returns
   * true if the argument was a listener before this call, false otherwise.
   */
  public boolean removeCyNetworkListener ( CyNetworkListener listener ) {
    return listeners.remove(listener);
  }

  /**
   * Returns the set of listeners registered with this object.
   */
  public Set getCyNetworkListeners () {
    return new HashSet(listeners);
  }
    
  //--------------------//
  // Event Firing
  //--------------------//
  

  /**
   * Fires an event to all listeners registered with this object. The argument
   * should be a constant from the CyNetworkEvent class identifying the type
   * of the event.
   */
  protected void fireEvent(int type) {
    CyNetworkEvent event = new CyNetworkEvent(this, type);
    for (Iterator i = listeners.iterator(); i.hasNext(); ) {
      CyNetworkListener listener = (CyNetworkListener)i.next();
      listener.onCyNetworkEvent(event);
    }
  }

  //----------------------------------------//
  // Implements Network
  //----------------------------------------//
  
  //----------------------------------------//
  // Node and Edge creation/deletion
  //----------------------------------------//

  //--------------------//
  // Nodes

  /**
   * This method will create a new node.
   * @return the Cytoscape index of the created node 
   */
  public int createNode () {
    return restoreNode(  root.createNode() );
  }

  /**
   * Add a node to this Network that already exists in 
   * Cytoscape
   * @return the Network Index of this node
   */
  public int addNode ( int cytoscape_node ) {
    return restoreNode( cytoscape_node );
  }

  /**
   * Add a node to this Network that already exists in 
   * Cytoscape
   * @return the Network Index of this node
   */
  public CyNode addNode ( Node cytoscape_node ) {
    return ( CyNode )restoreNode( cytoscape_node);
  }
 
  /**
   * Adds a node to this Network, by looking it up via the 
   * given attribute and value
   * @return the Network Index of this node
   */
  public int addNode ( String attribute, Object value ) {
    return 0;
  }

  /**
   * This will remove this node from the Network. However,
   * unless forced, it will remain in Cytoscape to be possibly
   * resused by another Network in the future.
   * @param force force this node to be removed from all Networks
   * @return true if the node is still present in Cytoscape 
   *          ( i.e. in another Network )
   */
  public boolean removeNode ( int node_index, boolean force ) {
    hideNode( node_index );
    return true;
  }

  //--------------------//
  // Edges

  /**
   * This method will create a new edge.
   * @param source the source node
   * @param target the target node
   * @param directed weather the edge should be directed
   * @return the Cytoscape index of the created edge 
   */
  public int createEdge ( int source, int target, boolean directed ) {
    return restoreEdge( Cytoscape.getRootGraph().createEdge( source, target, directed ) );
  }

  /**
   * Add a edge to this Network that already exists in 
   * Cytoscape
   * @return the Network Index of this edge
   */
  public int addEdge ( int cytoscape_edge ) {
    return restoreEdge( cytoscape_edge );
  }

  /**
   * Add a edge to this Network that already exists in 
   * Cytoscape
   * @return the Network Index of this edge
   */
  public CyEdge addEdge ( Edge cytoscape_edge ) {
    return ( CyEdge )restoreEdge( cytoscape_edge );
  }
 
  /**
   * Adds a edge to this Network, by looking it up via the 
   * given attribute and value
   * @return the Network Ind 
ex of this edge
   */
  public int addEdge ( String attribute, Object value ) {
    return 0;
  }

  /**
   * This will remove this edge from the Network. However,
   * unless forced, it will remain in Cytoscape to be possibly
   * resused by another Network in the future.
   * @param force force this edge to be removed from all Networks
   * @return true if the edge is still present in Cytoscape 
   *          ( i.e. in another Network )
   */
  public boolean removeEdge ( int edge_index, boolean force ) {
    hideEdge( edge_index );
    return true;
  }



  ////////////////////
  // GraphPerspective methods

  public void addGraphPerspectiveChangeListener ( GraphPerspectiveChangeListener listener ) {
    gp.addGraphPerspectiveChangeListener( listener );
  }
  
  public void removeGraphPerspectiveChangeListener ( GraphPerspectiveChangeListener listener ) {
    gp.removeGraphPerspectiveChangeListener( listener );
  }

  /**
   * Create a new ColtGraphPerspective with the same RootGraph and a new copy
   * of the rootNodeIndexToPerspectiveNodeIndexMap and
   * rootEdgeIndexToPerspectiveEdgeIndexMap of this one.
   * @return a new ColtGraphPerspective with the same Nodes and Edges as this
   * one.
   */
  public Object clone () {
    return gp.clone();
  }

  
  /**
   * Returns number of active nodes in this perspective.
   * @return an int value; the number of nodes in this ColtGraphPerspective.
   */
  public int getNodeCount () {
    return gp.getNodeCount();
  }
 
  /**
   * Returns number of active edges in this perspective.
   * @return an int value; the number of edges in this ColtGraphPerspective.
   */
  public int getEdgeCount () {
    return gp.getEdgeCount();
  }
 
  /**
   * Returns an Iterator over all giny.model.Node objects in this GraphPerspective.<p>
   * TECHNICAL DETAIL: Iterating over the set of all nodes in a GraphPerspective and
   * manipulating a GraphPerspective's topology (by calling hideXXX() and restoreXXX()
   * methods) concurrently will have undefined effects on the returned Iterator.
   * @return an Iterator over the Nodes in this graph; each Object in the
   *   returned Iterator is of type giny.model.Node.
   */
  public Iterator nodesIterator () {
    int[] nodes = gp.getNodeIndicesArray();
    return intArray2CyNodeList( nodes ).iterator();
  }
    
  /**
   * @deprecated Use nodesIterator() instead.
   * @see #nodesIterator()
   */
  public List nodesList () {
    int[] nodes = gp.getNodeIndicesArray();
    return intArray2CyNodeList( nodes );
  }
 
  /**
   * Returns an array of length getNodeCount() + 1; the entry at index 0 in
   * the returned array is fixed to be 0, and the rest of the array contains
   * RootGraph indices of Node objects in this GraphPerspective.
   * @deprecated Use nodesIterator() together with Node.getRootGraphIndex().
   * @see #nodesIterator()
   * @see Node#getRootGraphIndex()
   */
  public int[] getNodeIndicesArray () {
    return gp.getNodeIndicesArray();
  }

  /**
   * @return an Iterator over the Edges in this graph.
   */
  public Iterator edgesIterator () {
    int[] edges = gp.getEdgeIndicesArray();
    return intArray2CyEdgeList( edges ).iterator();
  }
   
  /**
   * @deprecated Use edgesIterator() instead.
   * @see #edgesIterator()
   */
  public List edgesList () {
    int[] edges = gp.getEdgeIndicesArray();
    return intArray2CyEdgeList( edges );
  }
 
  /**
   * Returns an array of length getEdgeCount() + 1; the entry at index 0 in
   * the returned array is fixed to be 0, and the rest of the array contains
   * RootGraph indices of Edge objects in this GraphPerspective.
   * @deprecated Use edgesIterator() together with Edge.getRootGraphIndex().
   * @see #edgesIterator()
   * @see Edge#getRootGraphIndex()
   */
  public int[] getEdgeIndicesArray () {
    return gp.getEdgeIndicesArray();
  }

  /**
   * @return null is returned if either of the specified Nodes is not in this
   *   GraphPerspective.
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public int[] getEdgeIndicesArray ( 
                                    int from_node_index,
                                    int to_node_index,
                                    boolean include_undirected_edges,
                                    boolean include_both_directions
                                    ) {
    return gp.getEdgeIndicesArray( from_node_index,
                                   to_node_index,
                                   include_undirected_edges,
                                   include_both_directions );
  }

  /**
   * If this GraphPerspective does not hide the given Node, change it so that
   * it does hide the node and all of its incident edges.
   * @param node The Node to hide.
   * @return The given node, unless it was already hidden, in which case
   * null.
   */
  public Node hideNode ( Node node ) {
    gp.hideNode( node.getRootGraphIndex() );
    return node;
  }
 
  /**
   * If this GraphPerspective does not hide the Node with the given index in
   * the underlying RootGraph, change it so that it does hide the node and all of
   * its incident edges.
   * @param node_index The index in the underlying RootGraph of the Node to hide.
   * @return The given index, unless the corresponding Node was already hidden or
   *   does not exist in the underlying RootGraph, in which case 0.
   */
  public int hideNode ( int node_index ) {
    return gp.hideNode( node_index );
  }
 
  /**
   * @deprecated Use hideNode(Node) or hideNodes(int[]) instead.
   * @see #hideNode(Node)
   * @see #hideNodes(int[])
   */
  public List hideNodes ( List nodes ) {
    for ( Iterator i = nodes.iterator(); i.hasNext(); ) {
      hideNode( ( ( Node )i.next() ).getRootGraphIndex() );
    }
    return nodes;
  }
 
  /**
   * If this GraphPerspective does not hide any of the Nodes corresponding to
   * the indices in the given array, change it so that it does hide those nodes
   * and all Edges incident on them.
   * Returns an array of equal length to the one given, in which each
   * corresponding position is either the same as in the argument array or is
   * 0, indicating that the node with that index was already hidden.
   * @param node_indices The int array of indices in the underlying RootGraph of
   * the Nodes to hide.
   * @return An int array of equal length to the argument array, and with equal
   * values except at positions that in the input array contain indices
   * corresponding to Nodes that were already hidden; at these positions the
   * result array will contain the value 0.
   */
  public int[] hideNodes ( int[] node_indices ) {
    return gp.hideNodes( node_indices );
  }
 
  /**
   * If this GraphPerspective hides the given Node, change it so that it does
   * not hide the node.
   * @param node The Node to restore.
   * @return The given node, unless it was not hidden or it doesn't exist
   *   in the RootGraph, in which case null.
   */
  public Node restoreNode ( Node node ) {
    gp.restoreNode( node.getRootGraphIndex() );
    return node;
  }
 
  /**
   * If this GraphPerspective hides the Node with the given index in the
   * underlying RootGraph, change it so that it does not hide the node.
   * @param node_index The index in the underlying RootGraph of the Node to
   *   restore.
   * @return The given index, unless the corresponding Node was already
   *   restored or does not exist in the RootGraph, in which case 0.
   */
  public int restoreNode ( int node_index ) {
    return gp.restoreNode( node_index );
  }
 
  /**
   * @deprecated Use restoreNode(Node) restoreNodes(int[]) instead.
   * @see #restoreNode(Node)
   * @see #removeNodes(int[])
   */
  public List restoreNodes ( List nodes ) {
    for ( Iterator i = nodes.iterator(); i.hasNext(); ) {
      restoreNode( ( ( Node )i.next() ).getRootGraphIndex() );
    }
    return nodes;
  }
  
  /**
   * @deprecated Use restoreNodes(int[]) and restoreEdges(int[]) instead; to
   *   get edges incident to specified nodes, use
   *   RootGraph.getConnectingEdgeIndicesArray(int[]).
   * @see #restoreNodes(int[])
   * @see #restoreEdges(int[])
   * @see RootGraph.getConnectingEdgeIndicesArray(int[])
   */
  public List restoreNodes (List nodes, boolean restore_incident_edges) {
    gp.restoreNodes( cyNodeList2intArray( nodes ),
                     restore_incident_edges );
    return nodes;
  }
  
  /**
   * If this GraphPerspective hides any of the Nodes with the given RootGraph
   * indices change it so that it does not hide those nodes.
   * @param node_indices The Node indices.
   * @param restore_incident_edges Whether or not the incident edges to the
   *   restored nodes should also be restored.
   * @return An array of length equal to the length of the argument array,
   *   and with equal values except at positions that in the input array
   *   contain indices corresponding to Nodes that were already restored or
   *   don't exist in the RootGraph; at these positions the result array will
   *   contain the value 0.
   * @deprecated Use restoreNodes(int[]) and restoreEdges(int[]) instead; to
   *   get edges incident to specified nodes, use
   *   RootGraph.getConnectingEdgeIndicesArray(int[]).
   * @see #restoreNodes(int[])
   * @see #restoreEdges(int[])
   * @see RootGraph.getConnectingEdgeIndicesArray(int[])
   */
  public int [] restoreNodes (int [] node_indices, boolean restore_incident_edges) {
    return gp.restoreNodes( node_indices,
                            restore_incident_edges );
  }
  
  /**
   * If this GraphPerspective hides any of the Nodes corresponding to the
   * indices in the given array, change it so that it does not hide those
   * nodes.  Returns an array of equal length
   * to the one given, in which each corresponding position is either the same
   * as in the argument array or is 0, indicating that the node with that
   * index was already restored or does not exist in the RootGraph.
   * @param node_indices The int array of indices in the underlying RootGraph
   *   of the Nodes to restore.
   * @return An int array of equal length to the argument array, and with equal
   *   values except at positions that in the input array contain indices
   *   corresponding to Nodes that were already restored or don't exist in
   *   the RootGraph; at these positions the
   *   result array will contain the value 0.
   */
  public int[] restoreNodes ( int[] node_indices ) {
    return gp.restoreNodes( node_indices );
  }
 
  /**
   * If this GraphPerspective does not hide the given Edge, change it so that
   * it does hide the edge.
   * @param edge The Edge to hide.
   * @return The given edge, unless it was already hidden, in which case
   * null.
   */
  public Edge hideEdge ( Edge edge ) {
    gp.hideEdge( edge.getRootGraphIndex() );
    return edge;
  }
 
  /**
   * If this GraphPerspective does not hide the Edge with the given index in
   * the RootGraph, change it so that it does hide the edge.
   * @param edge_index The index in the underlying RootGraph of the Edge to
   *   hide.
   * @return The given index, unless the corresponding Edge was already hidden
   *   or does not exist in the underlying RootGraph, in which case 0.
   */
  public int hideEdge ( int edge_index ) {
    return gp.hideEdge( edge_index );
  }
 
  /**
   * @deprecated Use hideEdge(Edge) or hideEdges(int[]) instead.
   * @see #hideEdge(Edge)
   * @see #hideEdges(int[])
   */
  public List hideEdges ( List edges ) {
   gp.hideEdges( cyEdgeList2intArray( edges ) );
   return edges;
  }
 
  /**
   * If this GraphPerspective does not hide any of the Edges corresponding to
   * the indices in the given array, change it so that it does hide those
   * edges.  Returns an array of equal length
   * to the one given, in which each corresponding position is either the same
   * as in the argument array or is 0, indicating that the edge with that
   * index was already hidden.
   * @param edge_indices The int array of indices in the underlying RootGraph of
   * the Edges to hide.
   * @return An int array of equal length to the argument array, and with equal
   * values except at positions that in the input array contain indices
   * corresponding to Edges that were already hidden; at these positions the
   * result array will contain the value 0.
   */
  public int[] hideEdges ( int[] edge_indices ) {
    return gp.hideEdges( edge_indices );
  }
 
  /**
   * If this GraphPerspective hides the given Edge, change it so that it does
   * not hide the edge or the Nodes on which the edge is incident.
   * @param edge The Edge to restore.
   * @return The given edge, unless it was not hidden or does not exist
   *   in the underlying RootGraph, in which case null.
   */
  public Edge restoreEdge ( Edge edge ) {
    gp.restoreEdge( edge.getRootGraphIndex() );
    return edge;
  }
 
  /**
   * If this GraphPerspective hides the Edge with the given index in the
   * underlying RootGraph, change it so that it does not hide the edge or the
   * Nodes on which the edge is incident.
   * @param edge_index The index in the underlying RootGraph of the Edge to
   *   restore.
   * @return The given index, unless the corresponding Edge was already
   *   restored or does not exist in the RootGraph, in which case 0.
   */
  public int restoreEdge ( int edge_index ) {
    return gp.restoreEdge( edge_index );
  }
 
  /**
   * @deprecated Use restoreEdges(int[]) instead.
   * @see restoreEdges(int[])
   */
  public List restoreEdges ( List edges ) {
    gp.restoreEdges( cyEdgeList2intArray( edges ) );
    return edges;
  }
 
  /**
   * If this GraphPerspective hides any of the Edges corresponding to the
   * indices in the given array, change it so that it does not hide those edges
   * or any of the Nodes on which they are incident.
   * @param edge_indices An array of indices in the underlying RootGraph of
   *   the Edges to restore.
   * @return An int array of equal length to the argument array, and with equal
   *   values except at positions that in the input array contain indices
   *   corresponding to Edges that were already restored or don't exist in the
   *   underlying RootGraph; at these positions the
   *   result array will contain the value 0.
   */
  public int[] restoreEdges ( int[] edge_indices ) {
    return gp.restoreEdges( edge_indices );
  }
 
  /**
   * Return true if the given Node is in this GraphPerspective.  False
   * otherwise.  This method is recursive, so even if this GraphPerspective
   * does hide the Node, this method will return true if the given Node is
   * contained within any non-hidden Node (via the MetaParent->MetaChild
   * relationship) at any depth.
   * This method is equivalent to calling containsNode(Node, boolean)
   * with a true boolean argument.
   * @return true iff the given Node is in this GraphPerspective.
   * @see #containsNode(Node, boolean)
   */
  public boolean containsNode ( Node node ) {
    return true;
    //TODO
  }
 
  /**
   * Return true if the given Node is in this GraphPerspective.  False
   * otherwise.  If the <tt>recurse</tt> flag is true then this method will be
   * recursive, so even if this GraphPerspective does hide the Node, this
   * method will return true if the given Node is contained within any
   * non-hidden Node (via the MetaParent->MetaChild relationship) at any depth.  If
   * <tt>recurse</tt> is false then this method will return false iff the
   * given Node is hidden in this GraphPerspective.
   * @return true iff the given Node is in this GraphPerspective.
   */
  public boolean containsNode ( Node node, boolean recurse ) {
    return true;
    //TODO
  }
 
  /**
   * Return true if the given Edge is in this GraphPerspective.  False
   * otherwise.  This method is recursive, so even if this GraphPerspective
   * does hide the Edge, this method will return true if the given Edge is
   * contained within any non-hidden Node (via the MetaParent->MetaChild
   * relationship) at any depth.  This method calls {@link #containsEdge( Edge,
   * boolean ) } with a true <tt>recurse</tt> boolean argument.
   * @return true iff the given Edge is in this GraphPerspective.
   */
  public boolean containsEdge ( Edge edge ) {
    return true;
    //TODO
  }
 
  /**
   * Return true if the given Edge is in this GraphPerspective.  False
   * otherwise.  If the <tt>recurse</tt> flag is true then this method will be
   * recursive, so even if this GraphPerspective does hide the Edge, this
   * method will return true if the given Edge is contained within any
   * non-hidden Node (via the MetaParent->MetaChild relationship) at any depth.  If
   * <tt>recurse</tt> is false then this method will return false iff the
   * given Edge is hidden in this GraphPerspective.
   * @return true iff the given Edge is in this GraphPerspective.
   */
  public boolean containsEdge ( Edge edge, boolean recurse ) {
    return true;
    //TODO
  }
 
  /**
   * Creates a union GraphPerspective.  The given GraphPerspective must have
   * the same rootGraph as this one.
   * @return a new GraphPerspective that contains the union of Nodes and Edges
   * from this GraphPerspective and the given GraphPerspective.
   */
  public GraphPerspective join ( GraphPerspective peer ) {
    return gp.join( peer );
  }
 
  /**
   * Create a new GraphPerspective with just the given Nodes and Edges (and all
   * Nodes incident on the given Edges).
   * @param nodes A [possibly null] array of Nodes in this GraphPerspective to
   *   include in the new GraphPerspective.
   * @param edges A [possibly null] array of Edges in this GraphPerspective to
   *   include in the new GraphPerspective.
   * @return a new GraphPerspective on this GraphPerspective containing only the
   *   given Nodes and Edges, plus any Nodes incident on the given Edges array but
   *   omitted from the given Nodes array; returns null if any of the specified Nodes
   *   or Edges are not in this GraphPerspective.
   */
   public GraphPerspective createGraphPerspective ( Node[] nodes,
                                                    Edge[] edges
                                                    ) {
     return gp.createGraphPerspective( nodes,
                                       edges );
   }
 
  /**
   * Create a new GraphPerspective with just the Nodes with the given
   * <tt>node_indices</tt> and just the Edges with the given
   * <tt>edge_indices</tt> (and all Nodes incident on the given Edges).
   * @param node_indices A [possibly null] array of [RootGraph] indices
   *   of Nodes in this GraphPerspective to include in the new GraphPerspective.
   * @param edge_indices A [possibly null] array of [RootGraph] indices
   *   of Edges in this GraphPerspective to include in the new GraphPerspective.
   * @return a new GraphPerspective on this GraphPerspective containing only Nodes
   *   and Edges with the given indices, including all Nodes incident on those Edges;
   *   returns null if any of the specified Node or Edge indices do not correspond
   *   to Nodes or Edges existing in this GraphPerspective.
   */
  public GraphPerspective createGraphPerspective (int[] node_indices,
                                                  int[] edge_indices
                                                  ) {
    return gp.createGraphPerspective( node_indices,
                                      edge_indices );
  }
 
  /**
   * Create a new GraphPerspective with all of the Nodes from this one that
   * pass the given filter and all of the Edges from this one that pass the
   * filter (and all Nodes incident on those edges).
   */
  public GraphPerspective createGraphPerspective ( Filter filter ) {
    return gp.createGraphPerspective( filter );
  }
 
  /**
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead;
   *   if you decide to use this method anyways, please note that the definition
   *   of "node neighbor" is such: Node A is a "node neighbor" of node B if and only
   *   if there exists an edge [directed or undirected] E such that A is E's target and
   *   B is E's source, or
   *   A is E's source and B is E's target; this method then returns a non-repeating list
   *   of all nodes N in this
   *   GraphPerspective such that N is a "node neighbor" of node, the input parameter.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public List neighborsList ( Node node ) {
    return intArray2CyNodeList( gp.neighborsArray( node.getRootGraphIndex() ) );
  }

  /**
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead;
   *   if you decide to use this method anyways, please note that the definition
   *   of "node neighbor" is such: Node A is a "node neighbor" of node B if and only
   *   if there exists an edge [directed or undirected] E such that A is E's target and
   *   B is E's source, or
   *   A is E's source and B is E's target; this method then returns a non-repeating list
   *   of indices of all nodes N in this
   *   GraphPerspective such that N is a "node neighbor" of the node at specified index,
   *   or null if no node at specified index exists in this GraphPerspective.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public int[] neighborsArray ( int node_index ) {
    return gp.neighborsArray( node_index );
  }
  
  /**
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead;
   *   if you decide to use this method anyways, please note that the definition
   *   of "node neighbor" is such: Node A is a "node neighbor" of node B if and only
   *   if there exists an edge [directed or undirected] E such that A is E's target and
   *   B is E's source, or A is E's source and B is E's target; this method then returns
   *   true if and only if a_node is a "node neighbor" of another_node in this
   *   GraphPerspective.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public boolean isNeighbor ( Node a_node, Node another_node ) {
    return gp.isNeighbor( a_node.getRootGraphIndex(), another_node.getRootGraphIndex() );
  }
  
  /**
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead;
   *   if you decide to use this method anyways, please note that the definition
   *   of "node neighbor" is such: Node A is a "node neighbor" of node B if and only
   *   if there exists an edge [directed or undirected] E such that A is E's target and
   *   B is E's source, or A is E's source and B is E's target; this method then returns
   *   true if and only if node at index a_node_index is a "node neighbor" of node at
   *   index another_node_index in this GraphPerspective.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public boolean isNeighbor ( int a_node_index, int another_node_index ) {
    return gp.isNeighbor( a_node_index, another_node_index );
  }
 
  /**
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead;
   *   if you decide to use this method anyways: this method returns true if and only if
   *   either 1) there exists a directed edge E in this GraphPerspective such that the from
   *   node specified is E's source node and the target node specified is E's target node
   *   or 2) there exists an undirected edge E in this GraphPerspective such that E's
   *   endpoints are the from and to nodes specified.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public boolean edgeExists ( Node from, Node to ) {
    return gp.edgeExists( from.getRootGraphIndex(), to.getRootGraphIndex() );
  }
 
  /**
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead;
   *   if you decide to use this method anyways: this method returns true if and only if
   *   either 1) there exists a directed edge E in this GraphPerspective such that
   *   from_node_index is E's source node's index and to_node_index is E's target node's
   *   index or 2) there exists an undirected edge E in this GraphPerspective such that E's
   *   endpoint nodes have indices from_node_index and to_node_index.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public boolean edgeExists ( int from_node_index, int to_node_index ) {
    return edgeExists( from_node_index, to_node_index );
  }
 
  /**
   * Count the number of edges from the first Node to the second.  Note that if
   * count_undirected_edges is false, any Edge <tt><i>e</i></tt> such that
   * <tt><i>e</i>.isDirected() == false</tt> will not be included in the count.
   * @param from the Node in this GraphPerspective that is the source of the
   * edges to be counted.
   * @param to the Node in this GraphPerspective that is the target of the
   * edges to be counted.
   * @param count_undirected_edges Undirected edges will be included in the
   * count iff count_undirected_edges is true.
   * @return the number of Edges from the <tt>from</tt> Node to the 
   * <tt>to</tt> Node; returns -1 if either the <tt>from</tt> or the
   * <tt>to</tt> Node is not in this GraphPerspective.
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public int getEdgeCount (Node from,
                           Node to,
                           boolean count_undirected_edges
                           ) {
    return gp.getEdgeCount( from.getRootGraphIndex(),
                            to.getRootGraphIndex(), 
                            count_undirected_edges );
  }
 
  /**
   * Count the number of edges from the Node with index <tt>from_index</tt> to
   * the Node with index <tt>to_index</tt> (where this.getIndex( to_node ) ==
   * to_index).  Note that if count_undirected_edges is false, any Edge
   * <tt><i>e</i></tt> such that <tt><i>e</i>.isDirected() == false</tt> will
   * not be included in the count.
   * @param from_node_index the index in this GraphPerspective of the Node to
   * count edges from.
   * @param to_node_index the index in this GraphPerspective of the Node to
   * find edges to.
   * @param count_undirected_edges Undirected edges will be included in the
   * count iff count_undirected_edges is true.
   * @return the number of Edges from the Node with index <tt>from_index</tt>
   * to the Node with index <tt>to_index</tt>; returns -1 if either one of the two
   * specified nodes is not in this GraphPerspective.
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public int getEdgeCount ( int from_node_index,
                            int to_node_index,
                            boolean count_undirected_edges
                            ) {
    return gp.getEdgeCount( from_node_index,
                            to_node_index,
                            count_undirected_edges );
  }
 
  /**
   * Return a new List of the Edges in this GraphPerspective from the first
   * given Node to the second given Node.
   * @param from the Node in this GraphPerspective that is the source of the
   * Edges to be returned.
   * @param to the Node in this GraphPerspective that is the target of the
   * Edges to be returned.
   * @return a new List of the Edges from the <tt>from</tt> Node to the
   * <tt>to</tt> Node, or the empty List if none exist; null is returned if either
   * of the specified nodes is not in this GraphPerspective.
   */
  public List edgesList ( Node from, Node to ) {
    return gp.edgesList( from.getRootGraphIndex(), to.getRootGraphIndex(), true );
  }
 
  /**
   * Return an array of the indices in this GraphPerspective of all Edges from
   * the Node with the first given index to the Node with the second given
   * index.
   * @param from_node_index the index in this GraphPerspective of the Node to
   * return edges from.
   * @param to_node_index the index in this GraphPerspective of the Node to
   * return edges to.
   * @param include_undirected_edges Undirected edges will be included in the
   * List iff include_undirected_edges is true.
   * @return a new List of the Edges from the Node corresponding to
   * <tt>from_node_index</tt> to the Node corresponding to
   * <tt>to_node_index</tt>, or the empty List if none exist; null is returned
   * if either of the specified nodes does not exist in this GraphPerspective.
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public List edgesList (int from_node_index,
                         int to_node_index,
                         boolean include_undirected_edges
                         ) {
    return gp.edgesList( from_node_index,
                         to_node_index,
                         include_undirected_edges );
  }
 
  /**
   * Return an array of the indices in this GraphPerspective of all Edges from
   * the Node with the first given index to the Node with the second given
   * index.
   * <br>
   * The result should be considered final; it <b>must not</b> be modified by
   * the receiver.
   * @param from_node_index the index in this GraphPerspective of the Node to
   * return edges from.
   * @param to_node_index the index in this GraphPerspective of the Node to
   * return edges to.
   * @param include_undirected_edges Undirected edges will be included in the
   * array iff include_undirected_edges is true.
   * @return an array of indices corresponding to Edges from the Node at index
   * <tt>from_node_index</tt> to the Node at index
   * <tt>to_node_index</tt>; the empty array is returned if no such Edges exist;
   * null is returned if either of the specified Nodes does not exist in this
   * GraphPerspective.
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
   public int[] getEdgeIndicesArray (int from_node_index,
                                     int to_node_index,
                                     boolean include_undirected_edges
                                     ) {
     return gp.getEdgeIndicesArray( from_node_index,
                                    to_node_index,
                                    include_undirected_edges );
   }
 
  /**
   * Return the number of Edges <tt><i>e</i></tt> in this GraphPerspective such
   * that <tt><i>e</i>.getTarget().equals( node )</tt>.  Note that this
   * includes undirected edges, so it will not always be the case that
   * <tt>getInDegree( node ) + getOutDegree( node ) == getDegree( node )</tt>.
   * @param node the Node to count in-edges of.
   * @return the in-degree of the given Node, or -1 if the specified Node is not
   *   in this GraphPerspective.
   */
  public int getInDegree ( Node node ) {
    return gp.getInDegree( node.getRootGraphIndex() );
  }
 
  /**
   * Return the number of Edges <tt><i>e</i></tt> in this GraphPerspective such
   * that <tt><i>e</i>.getTarget().equals( node )</tt>.  Note that this
   * includes undirected edges, so it will not always be the case that
   * <tt>getInDegree( node ) + getOutDegree( node ) == getDegree( node )</tt>.
   * @param node_index the index of the Node to count in-edges of.
   * @return the in-degree of the Node with the given index, or -1 if this
   *   GraphPerspective has no Node with specified index.
   */
  public int getInDegree ( int node_index ) {
    return gp.getInDegree( node_index );
  }
 
  /**
   * Return the number of Edges <tt><i>e</i></tt> in this GraphPerspective
   * such that <tt><i>e</i>.getSource().equals( node )</tt>.  Note that if
   * count_undirected_edges is true, this includes undirected edges, so it will
   * not always be the case that <tt>getInDegree( node, true ) + getOutDegree(
   * node, true ) == getDegree( node )</tt>, but it <i>will</i> always be the
   * case that <tt>getInDegree( node, true ) + getOutDegree( node, <b>false</b>
   * ) == getDegree( node )</tt>.
   * @param node the Node to count in-edges of.
   * @param count_undirected_edges Undirected edges will be included in the
   * count iff count_undirected_edges is true.
   * @return the in-degree of the given Node or -1 if specified Node is not
   *   in this GraphPerspective.
   */
  public int getInDegree ( Node node, boolean count_undirected_edges ) {
    return gp.getInDegree( node.getRootGraphIndex(), count_undirected_edges );
  }
 
  /**
   * Return the number of Edges <tt><i>e</i></tt> in this GraphPerspective
   * such that <tt><i>e</i>.getSource().equals( node )</tt>.  Note that if
   * count_undirected_edges is true, this includes undirected edges, so it will
   * not always be the case that <tt>getInDegree( node, true ) + getOutDegree(
   * node, true ) == getDegree( node )</tt>, but it <i>will</i> always be the
   * case that <tt>getInDegree( node, true ) + getOutDegree( node, <b>false</b>
   * ) == getDegree( node )</tt>.
   * @param node_index the index of the Node to count in-edges of.
   * @param count_undirected_edges Undirected edges will be included in the
   * count iff count_undirected_edges is true.
   * @return the in-degree of the Node with the given index or -1 if this 
   *   GraphPerspective has no Node at specified index.
   */
  public int getInDegree ( int node_index, boolean count_undirected_edges ) {
    return gp.getInDegree( node_index, count_undirected_edges );
  }
 
  /**
   * Return the number of Edges <tt><i>e</i></tt> in this GraphPerspective such
   * that <tt><i>e</i>.getSource().equals( node )</tt>.  Note that this
   * includes undirected edges, so it will not always be the case that
   * <tt>getInDegree( node ) + getOutDegree( node ) == getDegree( node )</tt>.
   * @param node the Node to count out-edges of.
   * @return the out-degree of the given Node, or -1 if specified Node is not
   *   in this GraphPerspective.
   */
  public int getOutDegree ( Node node ) {
    return gp.getOutDegree( node.getRootGraphIndex() );
  }
 
  /**
   * Return the number of Edges <tt><i>e</i></tt> in this GraphPerspective such
   * that <tt><i>e</i>.getSource().equals( node )</tt>.  Note that this
   * includes undirected edges, so it will not always be the case that
   * <tt>getInDegree( node ) + getOutDegree( node ) == getDegree( node )</tt>.
   * @param node_index the index of the Node to count out-edges of.
   * @return the out-degree of the Node with the given index or -1 if index
   *   specified does not correspond to a Node in this GraphPerspective.
   */
  public int getOutDegree ( int node_index ) {
    return gp.getOutDegree( node_index );
  }
 
  /**
   * Return the number of Edges <tt><i>e</i></tt> in this GraphPerspective
   * such that <tt><i>e</i>.getSource().equals( node )</tt>.  Note that if
   * count_undirected edges is true, this includes undirected edges, so it will
   * not always be the case that <tt>getInDegree( node, true ) + getOutDegree(
   * node, true ) == getDegree( node )</tt>, but it <i>will</i> always be the
   * case that <tt>getInDegree( node, true ) + getOutDegree( node,
   * <b>false</b> ) == getDegree( node )</tt>.
   * @param node the Node to count out-edges of.
   * @param count_undirected_edges Undirected edges will be included in the
   * count iff count_undirected_edges is true.
   * @return the out-degree of the given Node or -1 if specified Node is not
   *   in this GraphPerspective.
   */
  public int getOutDegree ( Node node, boolean count_undirected_edges ) {
    return gp.getOutDegree( node.getRootGraphIndex(), count_undirected_edges );
  }
 
  /**
   * Return the number of Edges <tt><i>e</i></tt> in this GraphPerspective
   * such that <tt><i>e</i>.getSource().equals( node )</tt>.  Note that if
   * count_undirected edges is true, this includes undirected edges, so it will
   * not always be the case that <tt>getInDegree( node, true ) + getOutDegree(
   * node, true ) == getDegree( node )</tt>, but it <i>will</i> always be the
   * case that <tt>getInDegree( node, true ) + getOutDegree( node,
   * <b>false</b> ) == getDegree( node )</tt>.
   * @param node_index the index of the Node to count out-edges of.
   * @param count_undirected_edges Undirected edges will be included in the
   * count iff count_undirected_edges is true.
   * @return the out-degree of the Node with the given index or -1 if this
   *   GraphPerspective has no Node with specified RootGraph index.
   */
  public int getOutDegree ( int node_index, boolean count_undirected_edges ) {
    return gp.getOutDegree( node_index,
                            count_undirected_edges );
  }
 
  /**
   * Return the number of distinct Edges in this GraphPerspective incident on
   * the given Node.  By 'distinct' we mean that no Edge will be counted twice,
   * even if it is undirected.
   * @return the degree, in this GraphPerspective, of the given Node, or -1 if
   *   specified Node is not in this GraphPerspective.
   */
  public int getDegree ( Node node ) {
    return gp.getDegree( node.getRootGraphIndex() );
  }
 
  /**
   * Return the number of distinct Edges in this GraphPerspective incident on
   * the Node with the given index.  By 'distinct' we mean that no Edge will be
   * counted twice, even if it is undirected.
   * @return the degree, in this GraphPerspective, of the Node with the given
   * index, or -1 if this GraphPerspective has no Node with specified [RootGraph]
   * index.
   */
  public int getDegree ( int node_index ) {
    return gp.getDegree( node_index );
  }
 
  /**
   * Return the index of the given Node in the underlying RootGraph.
   * If the Node is hidden in this perspective, the result will be 0.
   * @param node the Node to find a corresponding index for.
   * @return the index of the given Node in the RootGraph
   *   (node.getRootGraphIndex()), or 0 if it is hidden or does not exist
   *   in the underlying RootGraph.
   */
  public int getIndex ( Node node ) {
    return  node.getRootGraphIndex() ;
  }
 
  /**
   * @deprecated Use getRootGraphNodeIndex(int), whose functionality is
   *   identical.
   * @see getRootGraphNodeIndex(int)
   */
  public int getNodeIndex ( int root_graph_node_index ) {
    return gp.getNodeIndex( root_graph_node_index );
  }
 
  /**
   * This method returns the input parameter if and only if a Node at the
   * specified RootGraph index exists in this GraphPerspective; otherwise 0
   * is returned.
   */
  public int getRootGraphNodeIndex ( int root_graph_node_index ) {
    return gp.getRootGraphNodeIndex( root_graph_node_index );
  }
 
  /**
   * Return a Node which is in this GraphPerspective.
   * in this GraphPerspective.
   * @param index the index into the underlying RootGraph to find a
   *   corresponding GraphPerspective Node for.
   * @return the Node in this GraphPerspective, or null if
   *   no such Node exists in this GraphPerspective.
   */
  public Node getNode ( int index ) {
    return root.getNode( index );
  }
 
  /**
   * Return the index of the given Edge in the underlying RootGraph.
   * If the Edge is hidden in this perspective, the result will be 0.
   * @param edge the Edge to find a corresponding index for.
   * @return the index of the given Edge in the RootGraph
   *   (edge.getRootGraphIndex()), or 0 if it is hidden or does not exist
   *   in the underlying RootGraph.
   */
  public int getIndex ( Edge edge ) {
    return edge.getRootGraphIndex();
  }
 
  /**
   * @deprecated Use getRootGraphEdgeIndex(int), whose functionality is
   *   identical.
   * @see getRootGraphEdgeIndex(int)
   */
  public int getEdgeIndex ( int root_graph_edge_index ) {
    return gp.getEdgeIndex( root_graph_edge_index );
  }
 
  /**
   * This method returns the input parameter if and only if an Edge at the
   * specified RootGraph index exists in this GraphPerspective; otherwise 0 is
   * returned.
   */
  public int getRootGraphEdgeIndex ( int root_graph_edge_index ) {
    return gp.getRootGraphEdgeIndex( root_graph_edge_index );
  }
 
  /**
   * Return an Edge which is in this GraphPerspective.
   * @param index the index into the underlying RootGraph to find a
   *   corresponding GraphPerspective Edge for.
   * @return the Edge in this GraphPerspective, or null if
   *   no such Edge exists in this GraphPerspective.
   */
  public Edge getEdge ( int index ) {
    return root.getEdge( index );
  }
 
  /**
   * Retrieve the index of the Node that is the source of the Edge in this
   * GraphPerspective with the given index.  Note that if the edge is
   * undirected, the edge also connects the target to the source.
   * @param edge_index the [RootGraph] index of the Edge in this GraphPerspective.
   * @return the index of the Edge's source Node, or 0
   * if the Edge is not in this GraphPerspective.
   */
  public int getEdgeSourceIndex ( int edge_index ) {
    return gp.getEdgeSourceIndex( edge_index );
  }
 
  /**
   * Retrieve the index of the Node that is the target of the Edge in this
   * GraphPerspective with the given index.  Note that if the edge is
   * undirected, the edge also connects the target to the source.
   * @param edge_index the [RootGraph] index of the Edge in this GraphPerspective.
   * @return the index of the Edge's target Node, or 0
   * if the Edge is not in this GraphPerspective.
   */
  public int getEdgeTargetIndex ( int edge_index ) {
    return gp.getEdgeTargetIndex( edge_index );
  }
 
  /**
   * Retrieve the directedness of the Edge in this GraphPerspective with the
   * given [RootGraph] index.  Note that if the edge is undirected, the edge also
   * connects the target to the source.
   * @param edge_index the [RootGraph] index of the Edge in this GraphPerspective.
   * @return true iff the edge is directed; if no Edge at specified index exists
   *   in this GraphPerspective, the result of this method is undefined.
   */
  public boolean isEdgeDirected ( int edge_index ) {
    return gp.isEdgeDirected( edge_index );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaParents.
   * isMetaParent returns true iff the second argument (<tt>parent</tt>) is an
   * MetaParent of the first argument (<tt>child</tt>).  Calls {@link
   * #isNodeMetaParent( int, int )}.
   * <br>
   * Note the inverse relationship between this method and {@link #isMetaChild(
   * Node, Node )}: <tt>isMetaChild( parent, child ) == isMetaParent( child, parent
   * )</tt>.
   * @param child the Node that is the child (the contain<i>ee</i>) in the
   * contains-a relationship that we are querying.
   * @param parent the Node that is the parent (the contain<i>er</i>) in the
   * contains-a relationship that we are querying.
   * @return true iff the latter argument is a MetaParent of the former argument
   * in this GraphPerspective.
   */
  public boolean isMetaParent ( Node child, Node parent ) {
    return gp.isNodeMetaParent( child.getRootGraphIndex(), parent.getRootGraphIndex() );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaParents.
   * isMetaParent returns true iff the Node corresponding to the second argument
   * (<tt>parent_index</tt>) is a MetaParent of the Node corresponding to the
   * first argument (<tt>child_index</tt>).
   * <br>
   * Note the inverse relationship between this method and {@link
   * #isNodeMetaChild( int, int )}: <tt>isNodeMetaChild( parent_index, child_index )
   * == isNodeMetaParent( child_index, parent_index )</tt>.
   * @param child_node_index the index in this GraphPerspective of the Node
   * that is the child (the contain<i>ee</i>) in the contains-a relationship
   * that we are querying.
   * @param parent_index the index in this GraphPerspective of the Node that is
   * the parent (the contain<i>er</i>) in the contains-a relationship that we
   * are querying.
   * @return true iff the Node corresponding to the latter argument is an
   * MetaParent (in this GraphPerspective) of the Node corresponding to the former
   * argument.
   */
  public boolean isNodeMetaParent ( int child_node_index, int parent_index ) {
    return gp.isNodeMetaParent( child_node_index, parent_index );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaParents.
   * metaParentsList returns a new List of the MetaParents (in this GraphPerspective)
   * of the given Node.  If there are no MetaParents then the result will be null.
   * Calls {@link #nodeMetaParentsList( int )}.
   * @param node the Node that is the child (the contain<i>ee</i>) in the
   * contains-a relationship that we are querying.
   * @return a new List of the Nodes in this GraphPerspective that contain the
   * given Node, or null if the given Node is not in this GraphPerspective or
   * if there are none.
   */
  public List metaParentsList ( Node node ) {
    int[] parents = gp.getNodeMetaParentIndicesArray( node.getRootGraphIndex() );
    return intArray2CyNodeList( parents );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaParents.
   * nodeMetaParentsList returns a new List of the MetaParents (in this
   * GraphPerspective) of the Node in this GraphPerspective with the given
   * index.  If there are no MetaParents then the result will be null.
   * @param node_index the index in this GraphPerspective of the Node that is
   * the child (the contain<i>ee</i>) in the contains-a relationship that we
   * are querying.
   * @return a new List of the Nodes in this GraphPerspective that contain the
   * Node with the given index, or null if the index is 0 or if there are none.
   */
  public List nodeMetaParentsList ( int node_index ) {
    int[] parents = gp.getNodeMetaParentIndicesArray( node_index );
    return intArray2CyNodeList( parents );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaParents.
   * getNodeMetaParentIndicesArray returns an array of the MetaParents (in this
   * GraphPerspective) of the Node with the given index.  If there are no
   * MetaParents then the result will be null.
   * <br>
   * The result should be considered final; it <b>must not</b> be modified by
   * the receiver.
   * @param node_index the index in this GraphPerspective of the Node that is
   * the child (the contain<i>ee</i>) in the contains-a relationship that we
   * are querying.
   * @return an array of the indices of the Nodes in this GraphPerspective that
   * contain the Node with the given index, or null if the index is 0 or if
   * there are none.
   */
  public int[] getNodeMetaParentIndicesArray ( int node_index ) {
    return gp.getNodeMetaParentIndicesArray( node_index );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * isMetaChild returns true iff the second argument (<tt>child</tt>) is an
   * MetaChild of the first argument (<tt>parent</tt>).  Calls {@link
   * #isNodeMetaChild( int, int )}.
   * <br>
   * Note the inverse relationship between this method and {@link #isMetaParent(
   * Node, Node )}: <tt>isMetaChild( parent, child ) == isMetaParent( child, parent
   * )</tt>.
   * @param parent the Node that is the parent (the contain<i>er</i>) in the
   * contains-a relationship that we are querying.
   * @param child the Node that is the child (the contain<i>ee</i>) in the
   * contains-a relationship that we are querying.
   * @return true iff the latter argument is a MetaChild of the former argument
   * in this GraphPerspective.
   */
  public boolean isMetaChild ( Node parent, Node child ) {
    return gp.isNodeMetaChild( parent.getRootGraphIndex(), child.getRootGraphIndex() );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * isMetaChild returns true iff the Node corresponding to the second argument
   * (<tt>child_index</tt>) is a MetaChild of the Node corresponding to the first
   * argument (<tt>parent_index</tt>) in this GraphPerspective.
   * <br>
   * Note the inverse relationship between this method and {@link
   * #isNodeMetaParent( int, int )}: <tt>isNodeMetaChild( parent_index, child_index )
   * == isNodeMetaParent( child_index, parent_index )</tt>.
   * @param parent_index the index in this GraphPerspective of the Node that is
   * the parent (the contain<i>er</i>) in the contains-a relationship that we
   * are querying.
   * @param child_node_index the index in this GraphPerspective of the Node
   * that is the child (the contain<i>ee</i>) in the contains-a relationship
   * that we are querying.
   * @return true iff the Node corresponding to the latter argument is an
   * MetaChild (in this GraphPerspective) of the Node corresponding to the former
   * argument.
    */
  public boolean isNodeMetaChild ( int parent_index, int child_node_index ) {
    return gp.isNodeMetaChild( parent_index, child_node_index );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * nodeMetaChildrenList returns a new List of the MetaChildren (in this
   * GraphPerspective) of the given Node.  If there are no MetaChildren then the
   * result will be null.  Calls {@link #nodeMetaChildrenList( int )}.
   * @param node the Node that is the parent (the contain<i>er</i>) in the
   * contains-a relationship that we are querying.
   * @return a new List of the Nodes in this GraphPerspective that are
   * contained by the given Node, or null if that Node is not in this
   * GraphPerspective or if there are none.
   */
  public List nodeMetaChildrenList ( Node node ) {
    int[] children = getNodeMetaChildIndicesArray( node.getRootGraphIndex() );
    return intArray2CyNodeList( children );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * nodeMetaChildrenList returns a new List of the Node MetaChildren (in this
   * GraphPerspective) of the Node in this GraphPerspective with the given
   * index.  If there are no MetaChildren then the result will be null.
   * @param node_index the index in this GraphPerspective of the Node that is
   * the parent (the contain<i>er</i>) in the contains-a relationship that we
   * are querying.
   * @return a new List of the Nodes in this GraphPerspective that are
   * contained by the Node with the given index, or null if the index is 0 or
   * if there are none.
   */
  public List nodeMetaChildrenList ( int parent_index ) {
    int[] children = gp.getNodeMetaChildIndicesArray( parent_index );
   return intArray2CyNodeList( children );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * getNodeMetaChildIndicesArray returns an array of the MetaChildren (in this
   * GraphPerspective) of the Node with the given index.  If there are no
   * MetaChildren then the result will be null.
   * <br>
   * The result should be considered final; it <b>must not</b> be modified by
   * the receiver.
   * @param node_index the index in this GraphPerspective of the Node that is
   * the parent (the contain<i>ee</i>) in the contains-a relationship that we
   * are querying.
   * @return an array of the indices of the Nodes in this GraphPerspective that
   * are contained by the Node with the given index, or null if the index is 0 or if
   * there are none.
   */
  public int[] getNodeMetaChildIndicesArray ( int node_index ) {
    return gp.getNodeMetaChildIndicesArray( node_index );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  An Edge may have any number of MetaParents.
   * isMetaParent returns true iff the second argument (<tt>parent</tt>) is an
   * MetaParent of the first argument (<tt>child</tt>).  Calls {@link
   * #isEdgeMetaParent( int, int )}.
   * <br>
   * Note the inverse relationship between this method and {@link #isMetaChild(
   * Node, Edge )}: <tt>isMetaChild( parent, child ) == isMetaParent( child, parent
   * )</tt>.
   * @param child the Edge that is the child (the contain<i>ee</i>) in the
   * contains-a relationship that we are querying.
   * @param parent the Node that is the parent (the contain<i>er</i>) in the
   * contains-a relationship that we are querying.
   * @return true iff the latter argument is a MetaParent of the former argument
   * in this GraphPerspective.
   */
  public boolean isMetaParent ( Edge child, Node parent ) {
    return gp.isEdgeMetaParent( child.getRootGraphIndex(), parent.getRootGraphIndex() );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  An Edge may have any number of MetaParents.
   * isMetaParent returns true iff the Node corresponding to the second argument
   * (<tt>parent_index</tt>) is a MetaParent of the Edge corresponding to the
   * first argument (<tt>child_index</tt>).
   * <br>
   * Note the inverse relationship between this method and {@link
   * #isEdgeMetaChild( int, int )}: <tt>isEdgeMetaChild( parent_index, child_index )
   * == isEdgeMetaParent( child_index, parent_index )</tt>.
   * @param child_edge_index the index in this GraphPerspective of the Edge
   * that is the child (the contain<i>ee</i>) in the contains-a relationship
   * that we are querying.
   * @param parent_index the index in this GraphPerspective of the Node that is
   * the parent (the contain<i>er</i>) in the contains-a relationship that we
   * are querying.
   * @return true iff the Node corresponding to the latter argument is an
   * MetaParent (in this GraphPerspective) of the Edge corresponding to the former
   * argument.
   */
  public boolean isEdgeMetaParent ( int child_edge_index, int parent_index ) {
    return gp.isEdgeMetaParent( child_edge_index, parent_index );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  An Edge may have any number of MetaParents.
   * metaParentsList returns a new List of the MetaParents (in this GraphPerspective)
   * of the given Edge.  If there are no MetaParents then the result will be null.
   * Calls {@link #edgeMetaParentsList( int )}.
   * @param edge the Edge that is the child (the contain<i>ee</i>) in the
   * contains-a relationship that we are querying.
   * @return a new List of the Nodes in this GraphPerspective that contain the
   * given Edge, or null if the given Edge is not in this GraphPerspective or
   * if there are none.
   */
  public List metaParentsList ( Edge edge ) {
    int[] parents = getEdgeMetaParentIndicesArray( edge.getRootGraphIndex() );
    return intArray2CyEdgeList( parents );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  An Edge may have any number of MetaParents.
   * EdgeMetaParentsList returns a new List of the MetaParents (in this
   * GraphPerspective) of the Edge in this GraphPerspective with the given
   * index.  If there are no MetaParents then the result will be null.
   * @param edge_index the index in this GraphPerspective of the Edge that is
   * the child (the contain<i>ee</i>) in the contains-a relationship that we
   * are querying.
   * @return a new List of the Nodes in this GraphPerspective that contain the
   * Edge with the given index, or null if the index is 0 or if there are none.
   */
  public List edgeMetaParentsList ( int edge_index ) {
    int[] parents = getEdgeMetaParentIndicesArray( edge_index );
    return intArray2CyEdgeList( parents );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  An Edge may have any number of MetaParents.
   * getEdgeMetaParentIndicesArray returns an array of the MetaParents (in this
   * GraphPerspective) of the Edge with the given index.  If there are no
   * MetaParents then the result will be null.
   * <br>
   * The result should be considered final; it <b>must not</b> be modified by
   * the receiver.
   * @param edge_index the index in this GraphPerspective of the Edge that is
   * the child (the contain<i>ee</i>) in the contains-a relationship that we
   * are querying.
   * @return an array of the indices of the Nodes in this GraphPerspective that
   * contain the Edge with the given index, or null if the index is 0 or if
   * there are none.
   */
  public int[] getEdgeMetaParentIndicesArray ( int edge_index ) {
    return gp.getEdgeMetaParentIndicesArray( edge_index );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * isMetaChild returns true iff the second argument (<tt>child</tt>) is an
   * MetaChild of the first argument (<tt>parent</tt>).  Calls {@link
   * #isEdgeMetaChild( int, int )}.
   * <br>
   * Note the inverse relationship between this method and {@link #isMetaParent(
   * Edge, Node )}: <tt>isMetaChild( parent, child ) == isMetaParent( child, parent
   * )</tt>.
   * @param parent the Node that is the parent (the contain<i>er</i>) in the
   * contains-a relationship that we are querying.
   * @param child the Edge that is the child (the contain<i>ee</i>) in the
   * contains-a relationship that we are querying.
   * @return true iff the latter argument is a MetaChild of the former argument
   * in this GraphPerspective.
   */
  public boolean isMetaChild ( Node parent, Edge child ) {
    return gp.isEdgeMetaChild( parent.getRootGraphIndex(), child.getRootGraphIndex() );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * isMetaChild returns true iff the Edge corresponding to the second argument
   * (<tt>child_index</tt>) is a MetaChild of the Node corresponding to the first
   * argument (<tt>parent_index</tt>) in this GraphPerspective.
   * <br>
   * Note the inverse relationship between this method and {@link
   * #isEdgeMetaParent( int, int )}: <tt>isEdgeMetaChild( parent_index, child_index )
   * == isEdgeMetaParent( child_index, parent_index )</tt>.
   * @param parent_index the index in this GraphPerspective of the Node that is
   * the parent (the contain<i>er</i>) in the contains-a relationship that we
   * are querying.
   * @param child_edge_index the index in this GraphPerspective of the Edge
   * that is the child (the contain<i>ee</i>) in the contains-a relationship
   * that we are querying.
   * @return true iff the Edge corresponding to the latter argument is an
   * MetaChild (in this GraphPerspective) of the Node corresponding to the former
   * argument.
    */
  public boolean isEdgeMetaChild ( int parent_index, int child_edge_index ) {
    return gp.isEdgeMetaChild( parent_index, child_edge_index );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * edgeMetaChildrenList returns a new List of the Edge MetaChildren (in this
   * GraphPerspective) of the given Node.  If there are no Edge MetaChildren then
   * the result will be null.  Calls {@link #edgeMetaChildrenList( int )}.
   * @param node the Node that is the parent (the contain<i>er</i>) in the
   * contains-a relationship that we are querying.
   * @return a new List of the Edges in this GraphPerspective that are
   * contained by the given Node, or null if that Node is not in this
   * GraphPerspective or if there are none.
   */
  public List edgeMetaChildrenList ( Node node ) {
    int[] children = getEdgeMetaChildIndicesArray( node.getRootGraphIndex() );
    return intArray2CyEdgeList( children );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * edgeMetaChildrenList returns a new List of the Edge MetaChildren (in this
   * GraphPerspective) of the Node in this GraphPerspective with the given
   * index.  If there are no Edge MetaChildren then the result will be null.
   * @param node_index the index in this GraphPerspective of the Node that is
   * the parent (the contain<i>er</i>) in the contains-a relationship that we
   * are querying.
   * @return a new List of the Edges in this GraphPerspective that are
   * contained by the Node with the given index, or null if the index is 0 or
   * if there are none.
   */
  public List edgeMetaChildrenList ( int node_index ) {
    int[] children = getEdgeMetaChildIndicesArray( node_index );
    return intArray2CyEdgeList( children );
  }

  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * getEdgeMetaChildIndicesArray returns an array of the MetaChildren (in this
   * GraphPerspective) of the Node with the given index.  If there are no
   * MetaChildren then the result will be null.
   * <br>
   * The result should be considered final; it <b>must not</b> be modified by
   * the receiver.
   * @param node_index the index in this GraphPerspective of the Node that is
   * the parent (the contain<i>ee</i>) in the contains-a relationship that we
   * are querying.
   * @return an array of the indices of the Edges in this GraphPerspective that
   * are contained by the Node with the given index, or null if the index is 0 or if
   * there are none.
   */
  public int[] getEdgeMetaChildIndicesArray ( int node_index ) {
    return gp.getEdgeMetaChildIndicesArray( node_index );
  }


   /**
   * Returns all Adjacent Edges to the given node.
   * @param node the  node
   * @param include_undirected_edges should we include undirected edges, 
   * if true will also return self-edges
   * @param incoming_edges Include incoming edges
   * @param outgoing_edges Include outgoing edges
   * @return a List of giny.model.Edge objects; an empty List is returned if
   *   no adjacent Edges are found; null is returned if the specified Node does not
   *   exist in this GraphPerspective.
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public List getAdjacentEdgesList ( Node node, boolean include_undirected_edges, boolean incoming_edges, boolean outgoing_edges ) {
    return intArray2CyEdgeList( gp.getAdjacentEdgeIndicesArray( node.getRootGraphIndex(),
                                                                include_undirected_edges,
                                                                incoming_edges,
                                                                outgoing_edges )
                                );
      }

  /**
   * Returns [RootGraph] indices of all Edges in this GraphPerspective
   * adjacent to the Node at specified [RootGraph] index.
   * See definitions of adjacency below.
   *
   * @param node_index the [RootGraph] index of the Node whose adjacent
   *   Edge information we're seeking.
   * @param undirected_edges Edge indices of all adjacent
   *   undirected Edges are included in the return value of this
   *   method if this value is true, otherwise not a single
   *   index corresponding to an undirected Edge is returned;
   *   undirected Edge E is an adjacent undirected Edge to Node N
   *   [definition:] if and only if E's source is N or E's target
   *   is N.
   * @param incoming_directed_edges Edge indices of all incoming
   *   directed Edges are included in the return value of this
   *   method if this value is true, otherwise not a single index
   *   corresponding to an incoming directed Edge is returned;
   *   directed Edge E is an incoming directed Edge to Node N
   *   [definition:] if and only if N is E's target.
   * @param outgoing_directed_edges Edge indices of all outgoing
   *   directed Edges are included in the return value of this
   *   method if this value is true, otherwise not a single index
   *   corresponding to an outgoing directed Edge is returned;
   *   directed Edge E is an outgoing directed Edge from Node N
   *   [definition:] if and only if N is E's source.
   * @return a set of Edge [RootGraph] indices corresponding to
   *   Edges matched by our query; if all three of the boolean
   *   query parameters are false, the empty array is returned;
   *   null is returned if and only if this GraphPerspective has no Node
   *   at the specified [RootGraph] index.
   */
  public int[] getAdjacentEdgeIndicesArray ( int node_index,
                                             boolean undirected_edges,
                                             boolean incoming_directed_edges,
                                             boolean outgoing_directed_edges ) {
    return gp.getAdjacentEdgeIndicesArray( node_index,
                                           undirected_edges,
                                           incoming_directed_edges,
                                           outgoing_directed_edges );
  }


  /**
   * This will return a List of giny.model.Edge objects that are the Edges between Nodes.
   */
  public List getConnectingEdges ( List nodes ) {
    return intArray2CyEdgeList( gp.getConnectingEdgeIndicesArray( cyNodeList2intArray( nodes ) ) );
  }

  /**
   * This will return an array of Edge indices that are the Edges between Nodes.
   */
  public int[] getConnectingEdgeIndicesArray ( int[] node_indices ) {
    return gp.getConnectingEdgeIndicesArray( node_indices );
  }
 
  /**
   * Return the Nodes that connect the given Edges in this GraphPerspective.
   * @deprecated Use getEdgeSourceIndex(int) and getEdgeTargetIndex(int) instead.
   * @see #getEdgeSourceIndex(int)
   * @see #getEdgeTargetIndex(int)
   */
  public int[] getConnectingNodeIndicesArray ( int[] edge_indices ) {
    return gp.getConnectingNodeIndicesArray( edge_indices );
  }

  /**
   * Create a new GraphPerspective given a list of Nodes.  This method
   * will automatically find all the interconnected Edges.
   * Returns null if any of the specified Nodes are not in this GraphPerspective.
   */
  public GraphPerspective createGraphPerspective( int[] node_indices ) {
    return gp.createGraphPerspective( node_indices );
  }

  private List intArray2CyNodeList ( int[] indices ) {
    List nodes = new ArrayList ( indices.length );
    for ( int i = 0; i < indices.length; ++i ) {
      nodes.add( getNode( indices[i] ) );
    }
    return nodes;
  }

  private int[] cyNodeList2intArray ( Collection nodes ) {
    int[] array = new int[ nodes.size() ];
    int i =0 ;
    for ( Iterator it  = nodes.iterator(); it.hasNext(); ) {
      array[i] = ( ( giny.model.Node )it.next() ).getRootGraphIndex();
      i++;
    }
    return array;
  }
     
  private List intArray2CyEdgeList ( int[] indices ) {
    List edges = new ArrayList ( indices.length );
    for ( int i = 0; i < indices.length; ++i ) {
      edges.add( getEdge( indices[i] ) );
    }
    return edges;
  }

  private int[] cyEdgeList2intArray ( Collection edges ) {
    int[] array = new int[ edges.size() ];
    int i =0 ;
    for ( Iterator it  = edges.iterator(); it.hasNext(); ) {
      array[i] = ( ( giny.model.Edge )it.next() ).getRootGraphIndex();
      i++;
    }
    return array;
  }
}



