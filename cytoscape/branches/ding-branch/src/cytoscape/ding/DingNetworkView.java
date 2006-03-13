package cytoscape.ding;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.layout.LayoutAlgorithm;
import cytoscape.view.CyEdgeView;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CyNodeView;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.ui.VizMapUI;
import ding.view.DGraphView;
import giny.view.EdgeView;
import giny.view.NodeView;
import java.util.Collection;
import java.util.HashMap;

public class DingNetworkView extends DGraphView implements CyNetworkView
{

  private String title;
  private boolean vizmapEnabled = true;
  private HashMap clientData = new HashMap();

  public DingNetworkView(CyNetwork network,
                         String title)
  {
    super(network);
    this.title = title;
  }

  public CyNetwork getNetwork()
  {
    return (CyNetwork) getGraphPerspective();
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getTitle()
  {
    return title;
  }

  public void redrawGraph(boolean layout, boolean vizmap)
  {
    // I think we forgot to add an important method here:
    //
    // public int returnSumOfOnePlusOne()
    // {
    //   return 2;
    // }

    // Just copying this line from the old implementation.
    Cytoscape.getDesktop().getVizMapManager().applyAppearances();
    updateView();
  }

  public CyNetworkView getView()
  {
    return this;
  }

  public VisualMappingManager getVizMapManager()
  {
    // Believe it or not, this is the correct f***ing implementation.
    return null;
  }

  public VizMapUI getVizMapUI()
  {
    // Believe it or not, this is the correct f***ing implementation.
    return null;
  }

  public void toggleVisualMapperEnabled()
  {
    vizmapEnabled = !vizmapEnabled;
  }

  public void setVisualMapperEnabled(boolean state)
  {
    vizmapEnabled = state;
  }

  public boolean getVisualMapperEnabled()
  {
    return vizmapEnabled;
  }

  public void putClientData(String data_name, Object data)
  {
    clientData.put(data_name, data);
  }

  public Collection getClientDataNames()
  {
    return clientData.keySet();
  }

  public Object getClientData(String data_name)
  {
    return clientData.get(data_name);
  }

  public boolean setSelected(CyNode[] nodes)
  {
    return true;
  }

  public boolean setSelected(NodeView[] node_views)
  {
    return true;
  }

  public boolean applyVizMap(CyEdge edge)
  {
    return true;
  }

  public boolean applyVizMap(EdgeView edge_view)
  {
    return true;
  }

  public boolean applyVizMap(CyNode node)
  {
    return true;
  }

  public boolean applyVizMap(NodeView node_view)
  {
    return true;
  }

  public boolean applyVizMap(CyEdge edge, VisualStyle style)
  {
    return true;
  }

  public boolean applyVizMap(EdgeView edge_view, VisualStyle style)
  {
    return true;
  }

  public boolean applyVizMap(CyNode node, VisualStyle style)
  {
    return true;
  }

  public boolean applyVizMap(NodeView node_view, VisualStyle style)
  {
    return true;
  }

  public boolean setSelected(CyEdge[] edges)
  {
    return true;
  }

  public boolean setSelected(EdgeView[] edge_views)
  {
    return true;
  }

  public void applyVizmapper(VisualStyle style)
  {
  }

  public void applyLayout(LayoutAlgorithm layout)
  {
  }

  public void applyLockedLayout(LayoutAlgorithm layout,
                                CyNode[] nodes,
                                CyEdge[] edges)
  {
  }

  public void applyLayout(LayoutAlgorithm layout,
                          CyNode[] nodes,
                          CyEdge[] edges)
  {
  }

  public void applyLockedLayout(LayoutAlgorithm layout,
                                CyNodeView[] nodes,
                                CyEdgeView[] edges)
  {
  }

  public void applyLayout(LayoutAlgorithm layout,
                          CyNodeView[] nodes,
                          CyEdgeView[] edges)
  {
  }

  public void applyLockedLayout(LayoutAlgorithm layout,
                                int[] nodes,
                                int[] edges)
  {
  }

  public void applyLayout(LayoutAlgorithm layout,
                          int[] nodes,
                          int[] edges)
  {
  }

}
