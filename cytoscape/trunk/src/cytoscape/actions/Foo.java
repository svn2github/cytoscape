package cytoscape.actions;

import cytoscape.CyEdge;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import cytoscape.view.CyNetworkView;
import giny.model.Node;
import giny.view.NodeView;
import java.util.Iterator;

public class Foo
{

  public static void test()
  {
    CyNetworkView graphView = Cytoscape.getCurrentNetworkView();
    Iterator nodesIterator = graphView.getNodeViewsIterator();
    Node node1 = ((NodeView) nodesIterator.next()).getNode();
    Node node2 = ((NodeView) nodesIterator.next()).getNode();
    CyEdge edge1 = Cytoscape.getCyEdge
      (node1, node2, Semantics.INTERACTION, "pp", true);
    CyEdge edge2 = Cytoscape.getCyEdge
      (node1, node2, Semantics.INTERACTION, "pp", true);
    if (edge1 != edge2)
      throw new IllegalStateException("foobarf");
    else System.out.println("bug passed");
  }

}
