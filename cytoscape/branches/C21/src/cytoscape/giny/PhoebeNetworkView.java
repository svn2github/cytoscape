package cytoscape.giny;

import cytoscape.*;
import cytoscape.view.*;

import phoebe.*;
import giny.model.*;
import giny.view.*;

import java.awt.*;
import javax.swing.*;

import java.util.*;

public class PhoebeNetworkView 
  extends 
    PGraphView
  implements
    CyNetworkView {

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
    // addViewContextMenus();
    clientData = new HashMap();


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
  
  //TODO: make the vizmapper more self-contained
  public void redrawGraph( boolean layout, boolean vizmap ) { 
    
    Cytoscape.getDesktop().getVizMapManager().applyAppearances();

  //   if (getVizMapManager() != null && this.visualMapperEnabled) {
//         getVizMapManager().applyAppearances();
//     }
  }

  public void toggleVisualMapperEnabled() {}

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
  public void setClientData ( String data_name, Object data ) {
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
                           new Object[] {this } );

    addContextMethod( "edu.umd.cs.piccolo.PNodeView",
                           "cytoscape.graphutil.NodeAction",
                           "zoomToNode",
                           new Object[] {this } );

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
                           new Object[] {this } );
    addContextMethod( "class phoebe.util.PEdgeEndIcon",
                           "cytoscape.graphutil.EdgeAction",
                           "edgeEndBorderColor",
                           new Object[] {this } );

  }




}
