package cytoscape.graph.layout.util;

import cytoscape.graph.layout.PolyEdgeGraphLayout;

public class PolyEdgeGraphLayoutRepresentation
  extends GraphLayoutRepresentation
  implements PolyEdgeGraphLayout
{

  /**
   * Copies are made of all the array input parameters; modifying
   * the arrays after this constructor is called will have no effect on
   * an instance of this class.  An instance of this class
   * never modifies any of the arrays passed into the constructor.<p>
   * This constructor calls<blockquote><pre>
   *super(numNodes,
   *      directedEdgeSourceNodeIndices,
   *      directedEdgeTargetNodeIndices,
   *      undirectedEdgeNode0Indices,
   *      undirectedEdgeNode1Indices,
   *      maxWidth,
   *      maxHeight,
   *      nodeXPositions,
   *      nodeYPositions);</pre></blockquote>
   * - for the sake of preventing the same documentation from existing in two
   * different source code files, please refer to
   * <code>GraphLayoutRepresentation</code> for a definition of these first
   * nine input parameters.
   *
   * @param directedEdgeAnchorXPositions
   *   <blockquote>an array of length equal to the number of directed edges in
   *   this graph; the <code>double[]</code> array
   *   <code>directedEdgeAnchorXPositions[edgeIndex]</code> defines, in index
   *   order, the X positions of anchor points belonging to edge at index
   *   <code>edgeIndex</code>.</blockquote>
   * @param directedEdgeAnchorYPositions
   *   <blockquote>an array of length equal to the number of directed edges in
   *   this graph; the <code>double[]</code> array
   *   <code>directedEdgeAnchorYPositions[edgeIndex]</code> defines, in index
   *   order, the Y positions of anchor points belonging to edge at index
   *   <code>edgeIndex</code>.</blockquote>
   * @param undirectedEdgeAnchorXPositions
   *   <blockquote>an array of length equal to the number of undirected edges
   *   in this graph; the <code>double[]</code> array
   *   <nobr><code>undirectedEdgeAnchorXPositions[edgeIndex - numDirectedEdges]</code></nobr>
   *   defines, in index order, the X positions of anchor points belonging
   *   to edge at index <code>edgeIndex</code>, where
   *   <code>numDirectedEdges</code> is the number of directed edges in this
   *   graph.</blockquote>
   * @param undirectedEdgeAnchorYPositions
   *   <blockquote>an array of length equal to the number of undirected edges
   *   in this graph; the <code>double[]</code> array
   *   <nobr><code>undirectedEdgeAnchorYPositions[edgeIndex - numDirectedEdges]</code></nobr>
   *   defines, in index order, the Y positions of anchor points belonging
   *   to edge at index <code>edgeIndex</code>, where
   *   <code>numDirectedEdges</code> is the number of directed edges in this
   *   graph.</blockquote>
   *
   * @exception IllegalArgumentException if parameters are passed which
   *   don't agree with a possible graph definition.
   * @see GraphLayoutRepresentation#GraphLayoutRepresentation(int, int[], int[], int[], int[], double, double, double[], double[])
   * @see cytoscape.graph.util.GraphTopologyRepresentation#GraphTopologyRepresentation(int, int[], int[], int[], int[])
   **/
  public PolyEdgeGraphLayoutRepresentation
    (int numNodes,
     int[] directedEdgeSourceNodeIndices,
     int[] directedEdgeTargetNodeIndices,
     int[] undirectedEdgeNode0Indices,
     int[] undirectedEdgeNode1Indices,
     double maxWidth,
     double maxHeight,
     double[] nodeXPositions,
     double[] nodeYPositions,
     double[][] directedEdgeAnchorXPositions,
     double[][] directedEdgeAnchorYPositions,
     double[][] undirectedEdgeAnchorXPositions,
     double[][] undirectedEdgeAnchorYPositions)
  {
    super(numNodes, directedEdgeSourceNodeIndices,
          directedEdgeTargetNodeIndices, undirectedEdgeNode0Indices,
          undirectedEdgeNode1Indices, maxWidth, maxHeight,
          nodeXPositions, nodeYPositions);

    // Let's be anal and prove to ourselves that we no longer need any
    // of the parameters that are passed to our superclass' constructor.
    numNodes = -1;
    directedEdgeSourceNodeIndices = null; directedEdgeTargetNodeIndices = null;
    undirectedEdgeNode0Indices = null; undirectedEdgeNode1Indices = null;
    maxWidth = -1.0d; maxHeight = -1.0d;
    nodeXPositions = null; nodeYPositions = null;

    // Preliminary error checking.
    if (directedEdgeAnchorXPositions == null)
      directedEdgeAnchorXPositions = new double[getNumDirectedEdges()][];
    if (directedEdgeAnchorYPositions == null)
      directedEdgeAnchorYPositions = new double[getNumDirectedEdges()][];
    if (undirectedEdgeAnchorXPositions == null
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
