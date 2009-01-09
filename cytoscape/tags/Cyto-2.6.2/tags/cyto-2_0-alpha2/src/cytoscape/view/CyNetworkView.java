package cytoscape.view;

import cytoscape.*;
import giny.view.GraphView;
import giny.util.AbstractLayout;
import java.util.*;
import cytoscape.visual.*;

public interface CyNetworkView extends GraphView {

 
  /**
   * Returns the network displayed by this object.
   */
  public CyNetwork getNetwork ();
     
  /**
   * Sets the Title of this View
   */
  public void setTitle ( String title );

  /**
   * Returns the Title of this View
   */
  public String getTitle ();
  
 

  /**
   * Sets the Given nodes Selected
   */
  public boolean setSelected ( CyNode[] nodes );

  /**
   * Sets the Given nodes Selected
   */
  public boolean setSelected ( CyNodeView[] node_views );

  /**
   * Returns the selection as CyNodes
   */
  public List getSelectedNodes ();
  
  /**
   * Returns the selection as ints
   */
  public int[] getSelectedNodeIndices ();

  /**
   * Returns the selection as CyNodeViews
   */
  public List getSelectedNodeViews ();
  
  /**
   * Applies the given edge to the given vizmapper
   */
  public boolean applyVizMap ( CyEdge edge );

  /**
   * Applies the given edge to the given vizmapper
   */
  public boolean applyVizMap ( CyEdgeView edge_view );


  /**
   * Applies the given node to the given vizmapper
   */
  public boolean applyVizMap ( CyNode node );

  /**
   * Applies the given node to the given vizmapper
   */
  public boolean applyVizMap ( CyNodeView node_view );

   /**
   * Applies the given edge to the given vizmapper
   */
  public boolean applyVizMap ( CyEdge edge, VisualStyle style );

  /**
   * Applies the given edge to the given vizmapper
   */
  public boolean applyVizMap ( CyEdgeView edge_view, VisualStyle style );


  /**
   * Applies the given node to the given vizmapper
   */
  public boolean applyVizMap ( CyNode node, VisualStyle style );

  /**
   * Applies the given node to the given vizmapper
   */
  public boolean applyVizMap ( CyNodeView node_view, VisualStyle style );


  /**
   * Sets the Given edges Selected
   */
  public boolean setSelected ( CyEdge[] edges );

  /**
   * Sets the Given edges Selected
   */
  public boolean setSelected ( CyEdgeView[] edge_views );

  /**
   * Returns the selection as CyEdges
   */
  public List getSelectedEdges ();
  
  /**
   * Returns the selection as ints
   */
  public int[] getSelectedEdgeIndices ();

  /**
   * Returns the selection as CyEdgeViews
   */
  public List getSelectedEdgeViews ();

  /**
   * @param applyAppearances  if true, the vizmapper will recalculate
   *                          the node and edge appearances
   */
  public void applyVizmapper ( VisualStyle style );
    
  /**
   * Applies the given layout to the entire CyNetworkView
   */
  public void applyLayout ( AbstractLayout layout );

  /**
   * Applies the given layout to the entire CyNetworkView,
   * but locks the given Nodes and Edges in place
   */
  public void applyLockedLayout ( AbstractLayout layout, CyNode[] nodes, CyEdge[] edges );

  /**
   * Applies the  given layout to only the given Nodes and Edges
   */
  public void applyLayout ( AbstractLayout layout, CyNode[] nodes, CyEdge[] edges );

  /**
   * Applies the given layout to the entire CyNetworkView,
   * but locks the given NodeViews and EdgeViews in place
   */
  public void applyLockedLayout ( AbstractLayout layout, CyNodeView[] nodes, CyEdgeView[] edges );

  /**
   * Applies the  given layout to only the given NodeViews and EdgeViews
   */
  public void applyLayout ( AbstractLayout layout, CyNodeView[] nodes, CyEdgeView[] edges );

  /**
   * Applies the given layout to the entire CyNetworkView,
   * but locks the given Nodes and Edges in place
   */
  public void applyLockedLayout ( AbstractLayout layout, int[] nodes, int[] edges );

  /**
   * Applies the  given layout to only the given Nodes and Edges
   */
  public void applyLayout ( AbstractLayout layout, int[] nodes, int[] edges );
}
