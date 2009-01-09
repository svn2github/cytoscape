package cytoscape.giny;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.visual.*;
import cytoscape.layout.*;

import phoebe.*;
import giny.model.*;
import giny.view.*;


import java.awt.*;
import javax.swing.*;

import java.util.*;
import java.util.List;




public class PhoebeNetworkView 
  extends 
    PGraphView
  implements
    CyNetworkView {

  boolean vizmapEnabled = true;

  /**
   * This is the title of the NetworkView, it will be 
   * dispalyed in a Tab, or in a Window.
   */
  protected String title;
  
  /**
   * This is the label that tells how many node/edges are
   * in a CyNetworkView and how many are selected/hidden
   */
  protected JLabel statusLabel;

  /**
   * The GraphViewController keeps the CyNetworkView nsync with
   * the CyNetwork it is a View on.
   */
  protected GraphViewController graphViewController;
  
  /**
   * The ClientData map
   */
  protected Map clientData;


  public PhoebeNetworkView ( CyNetwork network,
                             String title ) {
    super( (GraphPerspective)network  );
    this.title = title;
    initialize();
  }

  protected void initialize () {
    
    //setup the StatusLabel
    this.statusLabel = new JLabel();
    ( ( JComponent )getComponent() ).add(statusLabel, BorderLayout.SOUTH);
    updateStatusLabel();
    addViewContextMenus();
    clientData = new HashMap();

    enableNodeSelection();
    disableEdgeSelection();
    
    //TODO:
    //     Add NetworkView specific ToolBars
    
  }
  
  public CyNetworkView getView () {
    return ( CyNetworkView )this;
  }

  public CyNetwork getNetwork () {
    return ( CyNetwork )getGraphPerspective();
  }

  public String getTitle () {
    return title;
  }

  public void setTitle ( String new_title) {
    this.title = new_title;
  }
  
  //TODO: set up the proper focus
  public void redrawGraph( boolean layout, boolean vizmap ) { 
    
    Cytoscape.getDesktop().getVizMapManager().applyAppearances();

  //   if (getVizMapManager() != null && this.visualMapperEnabled) {
//         getVizMapManager().applyAppearances();
//     }
  }

  public void toggleVisualMapperEnabled () {
    vizmapEnabled = !vizmapEnabled;
  }

  public void setVisualMapperEnabled ( boolean state ) {
    vizmapEnabled = state;
  }

  public boolean getVisualMapperEnabled () {
    return vizmapEnabled;
  }

  public cytoscape.visual.VisualMappingManager getVizMapManager() {
    return null;
  }

  public cytoscape.visual.ui.VizMapUI getVizMapUI() {
    return null;
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
  // Event Handling and Response


  /**
   * Overwritten version of fireGraphViewChanged so that
   * the label can be updated
   */
  protected void fireGraphViewChanged ( ChangeEvent event ) {
    updateStatusLabel();
    // fire the event to everyone else.
    super.fireGraphViewChanged( event );
  }

  /**
   * Resets the info label status bar text with the current number of
   * nodes, edges, selected nodes, and selected edges.
   */
  public void updateStatusLabel() {
  
    int nodeCount = getNodeViewCount();
    int edgeCount = getEdgeViewCount();
    int selectedNodes = getSelectedNodes().size();
    int selectedEdges = getSelectedEdges().size();

    statusLabel.setText("  Nodes: " + nodeCount
                      + " ("+selectedNodes+" selected)"
                      + " Edges: " + edgeCount
                      + " ("+selectedEdges+" selected)" );
  }

  //-------------------------------//
  // Layouts and VizMaps
  


  /**
   * Applies the given edge to the given vizmapper
   */
  public boolean applyVizMap ( CyEdge edge ) {
    return applyVizMap( ( EdgeView )getEdgeView( edge ) );
  }

  /**
   * Applies the given edge to the given vizmapper
   */
  public boolean applyVizMap ( EdgeView edge_view ) {
    return applyVizMap( edge_view, ( VisualStyle )getClientData( CytoscapeDesktop.VISUAL_STYLE ) );
  }
                        
  /**
   * Applies the given edge to the given vizmapper
   */
  public boolean applyVizMap ( CyEdge edge, VisualStyle style ) {
    return applyVizMap( ( EdgeView )getEdgeView( edge ), style );
  }
  /**
   * Applies the given edge to the given vizmapper
   */
  public boolean applyVizMap ( EdgeView edge_view, VisualStyle style ) {
    VisualStyle old_style = Cytoscape.getDesktop().setVisualStyle( style );
    Cytoscape.getDesktop().getVizMapManager().vizmapEdge( edge_view, this );
    Cytoscape.getDesktop().setVisualStyle( old_style );
    return true;
  }
      
  /**
   * Applies the given node to the given vizmapper
   */
  public boolean applyVizMap ( CyNode node ) {
    return applyVizMap( ( NodeView )getNodeView( node ) );
  }

  /**
   * Applies the given node to the given vizmapper
   */
  public boolean applyVizMap ( NodeView node_view ) {
    return applyVizMap( node_view, ( VisualStyle )getClientData( CytoscapeDesktop.VISUAL_STYLE ) );
  }

  /**
   * Applies the given node to the given vizmapper
   */
  public boolean applyVizMap ( CyNode node, VisualStyle style ) {
    return applyVizMap( ( NodeView )getNodeView( node ), style );
  }
  
  /**
   * Applies the given node to the given vizmapper
   */
  public boolean applyVizMap ( NodeView node_view, VisualStyle style ) {
    VisualStyle old_style = Cytoscape.getDesktop().setVisualStyle( style );
    Cytoscape.getDesktop().getVizMapManager().vizmapNode( node_view, this );
    Cytoscape.getDesktop().setVisualStyle( old_style );
    return true;
  }

  /**
   * @param style the visual style
   */
  public void applyVizmapper ( VisualStyle style ) {
    VisualStyle old_style = Cytoscape.getDesktop().setVisualStyle( style );
    redrawGraph( false, true );
  }
    
  /**
   * Applies the given layout to the entire CyNetworkView
   */
  public void applyLayout ( LayoutAlgorithm layout ) {
    layout.doLayout();
  }

  /**
   * Applies the given layout to the entire CyNetworkView,
   * but locks the given Nodes and Edges in place
   */
  public void applyLockedLayout ( LayoutAlgorithm layout, CyNode[] nodes, CyEdge[] edges ) {
    layout.lockNodes( convertToViews( nodes ) );
    layout.doLayout();
  }

  /**
   * Applies the  given layout to only the given Nodes and Edges
   */
  public void applyLayout ( LayoutAlgorithm layout, CyNode[] nodes, CyEdge[] edges) {
    layout.lockNodes( getInverseViews( convertToViews( nodes ) ) );
    layout.doLayout();
  }


  /**
   * Applies the given layout to the entire CyNetworkView,
   * but locks the given NodeViews and EdgeViews in place
   */
  public void applyLockedLayout ( LayoutAlgorithm layout, CyNodeView[] nodes, CyEdgeView[] edges ) {
    layout.lockNodes(  nodes );
    layout.doLayout();
  }

  /**
   * Applies the  given layout to only the given NodeViews and EdgeViews
   */
  public void applyLayout ( LayoutAlgorithm layout, CyNodeView[] nodes, CyEdgeView[] edges ) {
    layout.lockNodes( getInverseViews( nodes ) );
    layout.doLayout();
  }

  /**
   * Applies the given layout to the entire CyNetworkView,
   * but locks the given Nodes and Edges in place
   */
  public void applyLockedLayout ( LayoutAlgorithm layout, int[] nodes, int[] edges ) {
    layout.lockNodes( convertToNodeViews( nodes ) );
    layout.doLayout();
  }

  /**
   * Applies the  given layout to only the given Nodes and Edges
   */
  public void applyLayout ( LayoutAlgorithm layout, int[] nodes, int[] edges ) {

    layout.lockNodes( getInverseViews( convertToNodeViews( nodes ) ) );
    layout.doLayout();
  }

  //--------------------//
  // Convience Methods

   /**
   * Sets the Given nodes Selected
   */
  public boolean setSelected ( CyNode[] nodes ) {
    return setSelected( convertToViews( nodes ) );
  }

  /**
   * Sets the Given nodes Selected
   */
  public boolean setSelected ( NodeView[] node_views ) {
    for ( int i = 0; i < node_views.length; ++i ) {
      node_views[i].select();
    }
    return true;
  }

   /**
   * Sets the Given edges Selected
   */
  public boolean setSelected ( CyEdge[] edges ) {
     return setSelected( convertToViews( edges ) );
  }

  /**
   * Sets the Given edges Selected
   */
  public boolean setSelected ( EdgeView[] edge_views ) {
    for ( int i = 0; i < edge_views.length; ++i ) {
      edge_views[i].select();
    }
    return true;
  }


  protected NodeView[] convertToViews ( CyNode[] nodes ) {
    NodeView[] views = new NodeView[ nodes.length ];
    for ( int i = 0; i < nodes.length; ++i ) {
      views[i] = getNodeView( nodes[i] );
    }
    return views;    
  }

  protected EdgeView[] convertToViews ( CyEdge[] edges ) {
    EdgeView[] views = new EdgeView[ edges.length ];
    for ( int i = 0; i < edges.length; ++i ) {
      views[i] = getEdgeView( edges[i] );
    }
    return views;    
  }


  protected NodeView[] convertToNodeViews ( int[] nodes ) {
    NodeView[] views = new NodeView[ nodes.length ];
    for ( int i = 0; i < nodes.length; ++i ) {
      views[i] = getNodeView( nodes[i] );
    }
    return views;    
  }

  protected EdgeView[] convertToEdgeViews ( int[] edges ) {
    EdgeView[] views = new EdgeView[ edges.length ];
    for ( int i = 0; i < edges.length; ++i ) {
      views[i] = getEdgeView( edges[i] );
    }
    return views;    
  }



  protected NodeView[] getInverseViews ( NodeView[] given ) {
    NodeView[] inverse = new NodeView[ getNodeViewCount() - given.length ];
    List node_views = getNodeViewsList();
    int count = 0;
    Iterator i = node_views.iterator();
    Arrays.sort( given );
    while ( i.hasNext() ) {
      NodeView view = ( NodeView )i.next();
      if ( Arrays.binarySearch( given, view ) < 0 ) {
        // not a given, add
        inverse[count] = view;
        count++;
      }
    }
    return inverse;
  }

  protected EdgeView[] getInverseViews ( EdgeView[] given ) {
    EdgeView[] inverse = new EdgeView[ getEdgeViewCount() - given.length ];
    List edge_views = getEdgeViewsList();
    int count = 0;
    Iterator i = edge_views.iterator();
    Arrays.sort( given );
    while ( i.hasNext() ) {
      EdgeView view = ( EdgeView )i.next();
      if ( Arrays.binarySearch( given, view ) < 0 ) {
        // not a given, add
        inverse[count] = view;
        count++;
      }
    }
    return inverse;
  }

  //-------------------------------//
  // Misc Startup


  /**
   * Adds some useful context menus to the graph view.
   */
  protected void addViewContextMenus() {
    // Add some Node Context Menu Items
    
    addContextMethod( "class phoebe.PNodeView",
                           "cytoscape.graphutil.NodeAction",
                           "getTitle",
                           new Object[] { ( CyNetworkView )this } );
    
    addContextMethod( "class phoebe.PNodeView",
                           "cytoscape.graphutil.NodeAction",
                           "openWebInfo",
                           new Object[] { ( CyNetworkView )this } );


    addContextMethod( "class phoebe.PNodeView",
                           "cytoscape.graphutil.NodeAction",
                           "viewNodeAttributeBrowser",
                           new Object[] { ( CyNetworkView )this } );

    addContextMethod( "class phoebe.PNodeView",
                           "cytoscape.graphutil.NodeAction",
                           "editNode",
                           new Object[] { ( CyNetworkView )this } );

    addContextMethod( "class phoebe.PNodeView",
                           "cytoscape.graphutil.NodeAction",
                           "changeFirstNeighbors",
                           new Object[] {  ( CyNetworkView )this } );

    addContextMethod( "edu.umd.cs.piccolo.PNodeView",
                           "cytoscape.graphutil.NodeAction",
                           "zoomToNode",
                           new Object[] { ( CyNetworkView )this } );

    // Add some Edge Context Menus
    addContextMethod( "class phoebe.PEdgeView",
                           "cytoscape.graphutil.EdgeAction",
                           "getTitle",
                           new Object[] { ( CyNetworkView )this } );
    addContextMethod( "class phoebe.PEdgeView",
                           "cytoscape.graphutil.EdgeAction",
                           "editEdge",
                           new Object[] { ( CyNetworkView )this } );
    addContextMethod( "class phoebe.PEdgeView",
                           "cytoscape.graphutil.EdgeAction",
                           "viewEdgeAttributeBrowser",
                           new Object[] { ( CyNetworkView )this } );
    addContextMethod( "class phoebe.PEdgeView",
                           "cytoscape.graphutil.EdgeAction",
                           "openWebInfo",
                           new Object[] { ( CyNetworkView )this } );


    // Add some Edge-end Context menus
    addContextMethod( "class phoebe.util.PEdgeEndIcon",
                           "cytoscape.graphutil.EdgeAction",
                           "edgeEndColor",
                           new Object[] { ( CyNetworkView )this } );
    addContextMethod( "class phoebe.util.PEdgeEndIcon",
                           "cytoscape.graphutil.EdgeAction",
                           "edgeEndBorderColor",
                           new Object[] { ( CyNetworkView )this } );

  }




}
