package cytoscape.ding;

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

public class DingNetworkView extends DGraphView implements CyNetworkView
{

  private String title;

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
  }

  public CyNetworkView getView()
  {
    return null;
  }

  public VisualMappingManager getVizMapManager()
  {
    return null;
  }

  public VizMapUI getVizMapUI()
  {
    return null;
  }

  public void toggleVisualMapperEnabled()
  {
  }

  public void setVisualMapperEnabled(boolean state)
  {
  }

  public boolean getVisualMapperEnabled()
  {
    return true;
  }

  public void putClientData(String data_name, Object data)
  {
  }

  public Collection getClientDataNames()
  {
    return null;
  }

  public Object getClientData(String data_name)
  {
    return null;
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
