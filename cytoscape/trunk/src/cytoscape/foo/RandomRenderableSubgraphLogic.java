package cytoscape.foo;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.util.CyNetworkNaming;
import cytoscape.view.CyNetworkView;
import java.util.Random;
import java.util.Vector;

public class RandomRenderableSubgraphLogic
{

  public static void justDoIt(CyNetwork foo)
  {
    final int threshold = CytoscapeInit.getViewThreshold();
    final CyNetwork currentNetwork = foo;
    final int currentNodeCount = currentNetwork.getNodeCount();
//     if (currentNodeCount < threshold)
//       throw new IllegalStateException
//         ("misusage of this class - only use it if the number of nodes " +
//          "in the current network is equal to or exceeds the threshold");
    Vector inx = new Vector();
    for (int i = 0; i < currentNodeCount; i++)
      inx.addElement(new Integer(i));
    Random r = new Random();
    while (inx.size() >= threshold)
      inx.removeElementAt(r.nextInt(inx.size()));
    final int[] nodeInx = new int[inx.size()];
    for (int i = 0; i < nodeInx.length; i++)
      nodeInx[i] = ((Integer) inx.elementAt(i)).intValue();
    CyNetwork newNetwork = Cytoscape.createNetwork
      (nodeInx, currentNetwork.getConnectingEdgeIndicesArray(nodeInx),
       CyNetworkNaming.getSuggestedSubnetworkTitle(currentNetwork),
       currentNetwork);
    newNetwork.setExpressionData(currentNetwork.getExpressionData());
    CyNetworkView newView =
      Cytoscape.getNetworkView(newNetwork.getIdentifier());
  }

}
