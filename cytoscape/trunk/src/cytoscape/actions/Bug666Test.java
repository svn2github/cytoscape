package cytoscape.actions;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import giny.model.Edge;
import giny.model.Node;

public class Bug666Test
{

  public static void testBug()
  {
    CyNetwork net1     = Cytoscape.createNetwork ("net1");
    Node      n1       = Cytoscape.getCyNode ("S", true);
    Node      target   = Cytoscape.getCyNode ("7789023", true);
    int       edge_idx = Cytoscape.getRootGraph ().createEdge (n1, target,
                                                               true);
    Edge      edge1 = Cytoscape.getRootGraph ().getEdge (edge_idx);
    String    uuid  = "12345678";
    edge1.setIdentifier (uuid);
    // net1.restoreNode (n1);
    // net1.restoreNode (target);
    net1.restoreEdge (edge1);
    Cytoscape.destroyNetwork (net1, true);
  }

}
