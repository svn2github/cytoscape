package cytoscape.graph.layout.util;

import cytoscape.graph.layout.PolyEdgeGraphLayout;

public class PolyEdgeGraphLayoutRepresentation
  extends GraphLayoutRepresentation
  implements PolyEdgeGraphLayout
{

  public PolyEdgeGraphLayoutRepresentation(int numNodes,
                                           int[] directedEdgeSourceNodeIndices,
                                           int[] directedEdgeTargetNodeIndices,
                                           int[] undirectedEdgeNode0Indices,
                                           int[] undirectedEdgeNode1Indices,
                                           double maxWidth,
                                           double maxHeight,
                                           double[] nodeXPositions,
                                           double[] nodeYPositions)
  {
    super(numNodes, directedEdgeSourceNodeIndices,
          directedEdgeTargetNodeIndices, undirectedEdgeNode0Indices,
          undirectedEdgeNode1Indices, maxWidth, maxHeight,
          nodeXPositions, nodeYPositions);
  }

  public final int getNumAnchors(int edgeIndex)
  {
    return 0;
  }

  public final double getAnchorPosition(int edgeIndex, int anchorIndex,
                                        boolean xPosition)
  {
    return 0.0d;
  }

}
