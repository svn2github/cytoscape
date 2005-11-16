package cytoscape.giny;

import giny.model.Node;
import giny.model.RootGraph;
import fing.model.*;
import cytoscape.CyNode;

final class CyNodeDepot implements FingNodeDepot
{

  CyNodeDepot () { }

  public Node getNode(RootGraph root, int index, String id)
  {
    final CyNode returnThis = new CyNode(root, index);
//     returnThis.setIdentifier(id);
    return returnThis;
  }

  public void recycleNode(Node node)
  {
    node.setIdentifier(null);
  }

}
