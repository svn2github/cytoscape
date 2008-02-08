/*
 * Created on Oct 8, 2003
 */
package giny.view;

// java
import java.awt.Component;
import java.awt.Paint;
import java.util.Iterator;
import java.util.List;

// giny
import cytoscape.Edge;
import cytoscape.Node;
import cytoscape.RootGraph;
import cytoscape.GraphPerspective;
import giny.view.EdgeView;
import giny.view.GraphViewChangeListener;
import giny.view.NodeView;


/**
 * @author xmas
 */
public interface GraphView {

public static int NODE_X_POSITION = 0;
  public static int NODE_Y_POSITION = 1;
  public static int NODE_SHAPE = 2;
  public static int NODE_PAINT = 3;
  public static int NODE_SELECTION_PAINT = 4;
  public static int NODE_BORDER_PAINT = 5;
  public static int NODE_BORDER_WIDTH = 6;
  public static int NODE_WIDTH = 7;
  public static int NODE_HEIGHT = 8;
  public static int NODE_LABEL = 9;
  public static int NODE_Z_POSITION = 10;


  public static int SOURCE_INDEX = 0;
  public static int TARGET_INDEX = 1;
  public static int EDGE_WIDTH = 2;
  public static int EDGE_LINE_TYPE = 3;
  public static int EDGE_PAINT = 4;
  public static int EDGE_SELECTION_PAINT = 5;
  public static int EDGE_SOURCE_END_TYPE = 6;
  public static int EDGE_SOURCE_END_PAINT = 7;
  public static int EDGE_SOURCE_END_SELECTED_PAINT = 8;
  public static int EDGE_TARGET_END_TYPE = 9;
  public static int EDGE_TARGET_END_PAINT = 10;
  public static int EDGE_TARGET_END_SELECTED_PAINT = 11;



  public  GraphPerspective getGraphPerspective();


  //----------------------------------------//
  // Event Handlers
  //----------------------------------------//


  public boolean nodeSelectionEnabled();
  public boolean edgeSelectionEnabled(); 

  public void enableNodeSelection ();
   
  
  public void disableNodeSelection ();

  public void enableEdgeSelection ();
  
  public void disableEdgeSelection ();


  /**
   * @return an int array of the graph perspective indices of the selected nodes
   */
  public int[] getSelectedNodeIndices();

  /**
   * @return a list of the selected NodeView
   */
  public List<Node> getSelectedNodes();

  /**
   * @return an int array of the graph perspective indices of the selected edges
   */
  public int[] getSelectedEdgeIndices();

  /**
   * @return a list of the selected EdgeView
   */
  public List<Edge> getSelectedEdges();

  /**
   * Adds a new GraphViewChangeListener to this GraphViews list of listeners.
   */
  public void addGraphViewChangeListener(GraphViewChangeListener listener); 

  /**
   * Removes a  GraphViewChangeListener from this GraphViews list of listeners.
   */
  public void removeGraphViewChangeListener(GraphViewChangeListener listener);
   
  /**
   * @param paint the new Paint for the background
   */
  public void setBackgroundPaint(Paint paint);
      
  /**
   * @return the backgroundPaint 
   */
  public Paint getBackgroundPaint();
      
  /**
   * @return the java.awt.Component that can be added to most screen thingys
   */
  public Component getComponent();
      
  /**
   * @param node_index the index of a node to have a view created for it
   * @return a new NodeView based on the node with the given index
   */
  public  NodeView addNodeView(int node_index);
      
  /**
   * @param edge_index the index of an edge
   * @return the newly created edgeview
   */
  public  EdgeView addEdgeView(int edge_index);
      
  /**
   * To facilitate adding Custome EdgeViews
   * It is recomended that All Custom Edge Views follow the patterns outlined
   * in PEdgeView and BasicPEdgeView.  
   * @param class_name the name of the class that implements EdgeView and esnted PEdge
   * @param edge_index the index of the edge
   */
  public EdgeView addEdgeView(String class_name, int edge_index);
      
  /**
   * To facilitate adding Custome NodeViews
   * It is recomended that All Custom Node Views follow the patterns outlined
   * in PNodeView and BasicPNodeView.  
   * @param class_name the name of the class that implements NodeView and esnted PNode
   * @param node_index the index of the node
   */
  public NodeView addNodeView(String class_name, int node_index);
  
  /**
   * Add in a NodeView for a Node in the GraphPerspective.
   * Note that this means that if there already was a NodeView for this node,
   * the new NodeView will take its place.
   * @return If it is replacing, it returns the <B>old</B> NodeView.
   * @return If it is new, it returns the <B>new</b> NodeView.
   */
  public NodeView addNodeView(
                              int node_index,
                              NodeView node_view_replacement);
  

  /**
   * This will entirely remove a NodeView/EdgeView from the GraphView.  This is different than
   * @see #hideGraphObject as that method simply stops showing the node/edge.  This method 
   * will destroy the object.  It will be returned though, so that a reference can be kept 
   * for undo purposes.
   */
  public NodeView removeNodeView ( NodeView node_view );


  /**
   * This will entirely remove a NodeView/EdgeView from the GraphView.  This is different than
   * @see #hideGraphObject as that method simply stops showing the node/edge.  This method 
   * will destroy the object.  It will be returned though, so that a reference can be kept 
   * for undo purposes.
   */
  public NodeView removeNodeView ( Node node );

   /**
   * This will entirely remove a NodeView/EdgeView from the GraphView.  This is different than
   * @see #hideGraphObject as that method simply stops showing the node/edge.  This method 
   * will destroy the object.  It will be returned though, so that a reference can be kept 
   * for undo purposes.
   */
  public NodeView removeNodeView ( int node );

  /**
   * This will entirely remove a NodeView/EdgeView from the GraphView.  This is different than
   * @see #hideGraphObject as that method simply stops showing the node/edge.  This method 
   * will destroy the object.  It will be returned though, so that a reference can be kept 
   * for undo purposes.
   */
  public EdgeView removeEdgeView ( EdgeView edge_view );

  /**
   * This will entirely remove a NodeView/EdgeView from the GraphView.  This is different than
   * @see #hideGraphObject as that method simply stops showing the node/edge.  This method 
   * will destroy the object.  It will be returned though, so that a reference can be kept 
   * for undo purposes.
   */
  public EdgeView removeEdgeView ( Edge edge );

  /**
   * This will entirely remove a NodeView/EdgeView from the GraphView.  This is different than
   * @see #hideGraphObject as that method simply stops showing the node/edge.  This method 
   * will destroy the object.  It will be returned though, so that a reference can be kept 
   * for undo purposes.
   */
  public EdgeView removeEdgeView ( int edge );


  /**
   * @return The Unique Identifier of this GraphView
   */
  public String getIdentifier();
    
  /**
   * @param new_identifier The New Identifier for this GraphView
   */
  public void setIdentifier(String new_identifier);
   
  /**
   * @return The Current Zoom Level
   */
  public double getZoom();
        
  /**
   * @param zoom The New ZoomLevel
   */
  public void setZoom(double zoom);
  
  /**
   * Fits all Viewable elements onto the Graph
   */
  public void fitContent();
        
  /**
   * Do a global redraw of the entire canvas
   */
  public void updateView();
        
  /**
   * @return the root graph of the GraphPerspective that we are actually a view on
   */
  public RootGraph getRootGraph();
  
  /**
   * nodeViewsIterator only returns the NodeViews that are explicitly
   * associated with this GraphView
   */
  public Iterator getNodeViewsIterator(); 
  
  /**
   * @return the number of node views present
   */
  public int getNodeViewCount(); 

  /**
   * @return the number of EdgeViews present
   */
  public int getEdgeViewCount();
        
  /**
   * @param node The Node whose view is requested
   *
   * @return The NodeView of the given Node
   */
  public NodeView getNodeView(Node node);
        
  /**
   * @param index the index of the node whose view is requested
   * @return The NodeView of the given Node
   */
  public NodeView getNodeView(int index);
        
  /**
   * Return all of the EdgeViews in this GraphView
   */
  public java.util.List<EdgeView> getEdgeViewsList();
  
  /**
   * Note that this will return a list of Edge objects, the other one will return indices
   * @return The list of EdgeViews connecting these two nodes. Possibly null.
   */
  public  java.util.List<EdgeView> getEdgeViewsList(
                                          Node oneNode,
                                          Node otherNode);
  
  /**
   * @return a List of indicies
   */
  public  java.util.List<EdgeView> getEdgeViewsList(
                                          int from_node_index,
                                          int to_node_index,
                                          boolean include_undirected_edges);
  
  /**
   * @return the EdgeView that corresponds to the given index
   */
  public  EdgeView getEdgeView(int edge_index);
  
  /**
   * Return all of the EdgeViews in this GraphView
   */
  public Iterator<EdgeView> getEdgeViewsIterator();
  
  /**
   * @return the EdgeView that corresponds to the given Edge
   */
  public EdgeView getEdgeView(Edge edge);
  
  /**
   * @return the number of edges
   */
  public int edgeCount();
  
  /**
   * @return The number of Nodes, same number as the perspective
   */
  public int nodeCount();
  
  /**
   * use this to hide a node or edge
   */
  public boolean hideGraphObject(Object object);
  
  /**
   * use this to show a node or edge
   */
  public boolean showGraphObject(Object object);
  
  /**
   * <B> Warning!!!!!!!</B><BR>
   * Only to be used for homogenous groups!!!!
   */
  public boolean hideGraphObjects(List<? extends GraphViewObject> objects);
  
  /**
   * <B> Warning!!!!!!!</B><BR>
   * Only to be used for homogenous groups!!!!
   */
  public boolean showGraphObjects(List<? extends GraphViewObject> objects);
  

  /**
   * Context Menu Support
   */  
  public Object[] getContextMethods(
                                    String class_name,
                                    boolean plus_superclass);
  
  /**
   * Context Menu Support
   */  
  public Object[] getContextMethods(
                                    String class_name,
                                    Object[] methods);
  
  /**
   * Context Menu Support
   */  
  public boolean addContextMethod(
                                  String class_name,
                                  String method_class_name,
                                  String method_name,
                                  Object[] args,
                                  ClassLoader loader );

   /**
   * Set All Data For a NOde
   * <B>Big Bold Faced Warning</B> <BR>
   * Talk to rowan before using.
   */
  public  void setAllNodePropertyData(int node_index, Object[] data);

  /*
   * <B>Big Bold Faced Warning</B> <BR>
   * Talk to rowan before using.
   */
  public  Object[] getAllNodePropertyData(int node_index);

  /**
   * Set All Data For an Edge <BR>
   * <B>Big Bold Faced Warning</B> <BR>
   * Talk to rowan before using.
   */
  public  void setAllEdgePropertyData(int edge_index, Object[] data);

  /*
   * <B>Big Bold Faced Warning</B> <BR>
   * Talk to rowan before using.
   */
  public  Object[] getAllEdgePropertyData(int edge_index);

  //----------------------------------------//
  // Object Methods
  //----------------------------------------//

  /**
   * Return the stored value for the node for the given property
   * @param node_index The Node Index to be queried
   * @param property the property to be accessed
   */
  public  Object getNodeObjectProperty(int node_index, int property);

  /**
   * @param node_index The Node Index to be queried
   * @param property the property to be accessed
   * @param value the new value for this property
   */
  public  boolean setNodeObjectProperty( int node_index, int property, Object value);

  /**
   * Return the stored value for the edge for the given property
   * @param edge_index The Edge Index to be queried
   * @param property the property to be accessed
   */
  public  Object getEdgeObjectProperty(int edge_index, int property);

  /**
   * @param edge_index The Edge Index to be queried
   * @param property the property to be accessed
   * @param value the new value for this property
   */
  public  boolean setEdgeObjectProperty(
    int edge_index,
    int property,
    Object value);

  //----------------------------------------//
  // Double Methods
  //----------------------------------------//

  /**
   * @param node_index The Node Index to be queried
   * @param property the property to be accessed
   */
  public  double getNodeDoubleProperty(int node_index, int property);

  /**
   * @param node_index The Node Index to be queried
   * @param property the property to be accessed
   * @param value the new value for this property
   */
  public  boolean setNodeDoubleProperty(
    int node_index,
    int property,
    double value);

  public  double getEdgeDoubleProperty(int edge_index, int property);

  /**
   * @param edge_index The Edge Index to be queried
   * @param property the property to be accessed
   * @param value the new value for this property
   */
  public  boolean setEdgeDoubleProperty(
    int edge_index,
    int property,
    double value);

  //----------------------------------------//
  // Float Methods
  //----------------------------------------//
  
  public  float getNodeFloatProperty(int node_index, int property);
  
  /**
   * @param node_index The Node Index to be queried
   * @param property the property to be accessed
   * @param value the new value for this property
   */
  public  boolean setNodeFloatProperty(
    int node_index,
    int property,
    float value);
  
  public  float getEdgeFloatProperty(int edge_index, int property);
  
  /**
   * @param edge_index The Edge Index to be queried
   * @param property the property to be accessed
   * @param value the new value for this property
   */
  public  boolean setEdgeFloatProperty(
    int edge_index,
    int property,
    float value);
  
  
  public  boolean getNodeBooleanProperty(int node_index, int property);
  
  /**
   * @param node_index The Node Index to be queried
   * @param property the property to be accessed
   * @param value the new value for this property
   */
  public  boolean setNodeBooleanProperty(
    int node_index,
    int property,
    boolean value);
  
  public  boolean getEdgeBooleanProperty(int edge_index, int property);
  
  /**
   * @param edge_index The Edge Index to be queried
   * @param property the property to be accessed
   * @param value the new value for this property
   */
  public  boolean setEdgeBooleanProperty(
    int edge_index,
    int property,
    boolean value);
  
  public  int getNodeIntProperty(int node_index, int property);
  
  /**
   * @param node_index The Node Index to be queried
   * @param property the property to be accessed
   * @param value the new value for this property
   */
  public  boolean setNodeIntProperty(
    int node_index,
    int property,
    int value);
  
  public  int getEdgeIntProperty(int edge_index, int property);
  
  /**
   * @param edge_index The Edge Index to be queried
   * @param property the property to be accessed
   * @param value the new value for this property
   */
  public  boolean setEdgeIntProperty(
    int edge_index,
    int property,
    int value);

}
