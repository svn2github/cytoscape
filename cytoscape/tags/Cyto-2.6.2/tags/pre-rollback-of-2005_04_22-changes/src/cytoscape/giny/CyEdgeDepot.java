package cytoscape.giny;

import giny.model.Edge;
import giny.model.RootGraph;
import cytoscape.CyEdge;
import fing.model.*;

final class CyEdgeDepot implements FingEdgeDepot
{

  CyEdgeDepot() { }

  public Edge getEdge(RootGraph root, int index, String id)
  {
    final CyEdge returnThis = new CyEdge(root, index);
//     returnThis.setIdentifier(id);
    return returnThis;
  }

  public void recycleEdge(Edge edge) { }

}
