package cytoscape.graph.layout.util;

import cytoscape.graph.layout.PolyEdgeGraphLayout;

public class PolyEdgeGraphLayoutRepresentation
  extends GraphLayoutRepresentation
  implements PolyEdgeGraphLayout
{

  private final double[][] m_edgeAnchorXPositions;
  private final double[][] m_edgeAnchorYPositions;

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
   * @param edgeAnchorXPositions
   *   <blockquote>an array of length equal to the number of edges in
   *   this graph; the <code>double[]</code> array
   *   <code>edgeAnchorXPositions[edgeIndex]</code> defines, in anchor point
   *   index order, the X positions of anchor points belonging to edge at index
   *   <code>edgeIndex</code>.</blockquote>
   * @param edgeAnchorYPositions
   *   <blockquote>an array of length equal to the number of edges in
   *   this graph; the <code>double[]</code> array
   *   <code>edgeAnchorYPositions[edgeIndex]</code> defines, in anchor point
   *   index order, the Y positions of anchor points belonging to edge at index
   *   <code>edgeIndex</code>.</blockquote>
   *
   * @exception IllegalArgumentException if parameters are passed which
   *   don't agree with a possible graph definition.
   * @see GraphLayoutRepresentation#GraphLayoutRepresentation(int, int[], int[], int[], int[], double, double, double[], double[])
   * @see cytoscape.graph.util.GraphTopologyRepresentation#GraphTopologyRepresentation(int, int[], int[], int[], int[])
   **/
  public PolyEdgeGraphLayoutRepresentation(int numNodes,
                                           int[] directedEdgeSourceNodeIndices,
                                           int[] directedEdgeTargetNodeIndices,
                                           int[] undirectedEdgeNode0Indices,
                                           int[] undirectedEdgeNode1Indices,
                                           double maxWidth,
                                           double maxHeight,
                                           double[] nodeXPositions,
                                           double[] nodeYPositions,
                                           double[][] edgeAnchorXPositions,
                                           double[][] edgeAnchorYPositions)
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
    if (edgeAnchorXPositions == null)
      edgeAnchorXPositions = new double[getNumEdges()][];
    if (edgeAnchorYPositions == null)
      edgeAnchorYPositions = new double[getNumEdges()][];

    // Real parameter checking.  Set member variables;
    final int numEdges = getNumEdges();
    if (edgeAnchorXPositions.length != numEdges)
      throw new IllegalArgumentException
        ("edge anchor points X array does not have length numEdges");
    if (edgeAnchorYPositions.length != numEdges)
      throw new IllegalArgumentException
        ("edge anchor points Y array does not have length numEdges");
    m_edgeAnchorXPositions = new double[edgeAnchorXPositions.length][];
    m_edgeAnchorYPositions = new double[edgeAnchorYPositions.length][];
    for (int i = 0; i < numEdges; i++) {
      m_edgeAnchorXPositions[i] =
        new double[((edgeAnchorXPositions[i] == null) ?
                    0 : edgeAnchorXPositions[i].length)];
      m_edgeAnchorYPositions[i] =
        new double[((edgeAnchorYPositions[i] == null) ?
                    0 : edgeAnchorYPositions[i].length)];
      if (m_edgeAnchorXPositions[i].length != m_edgeAnchorYPositions[i].length)
        throw new IllegalArgumentException
          ("for anchor points belonging to edge at index " + i +
           ", the number of X positions is not the same as the number of " +
           "Y positions");
      System.arraycopy(edgeAnchorXPositions[i], 0, m_edgeAnchorXPositions[i],
                       0, edgeAnchorXPositions[i].length);
      System.arraycopy(edgeAnchorYPositions[i], 0, m_edgeAnchorYPositions[i],
                       0, edgeAnchorYPositions[i].length);
      for (int j = 0; j < m_edgeAnchorXPositions[i].length; j++) {
        if (m_edgeAnchorXPositions[i][j] < 0.0d ||
            m_edgeAnchorXPositions[i][j] > getMaxWidth() ||
            m_edgeAnchorYPositions[i][j] < 0.0d ||
            m_edgeAnchorYPositions[i][j] > getMaxHeight())
          throw new IllegalArgumentException
            ("an anchor position falls outside of allowable rectangle"); } }
  }

  public final int getNumAnchors(int edgeIndex)
  {
    return m_edgeAnchorXPositions[edgeIndex].length;
  }

  public final double getAnchorPosition(int edgeIndex, int anchorIndex,
                                        boolean xPosition)
  {
    double[] chosenArr = (xPosition ?
                          m_edgeAnchorXPositions[edgeIndex] :
                          m_edgeAnchorYPositions[edgeIndex]);
    return chosenArr[anchorIndex];
  }

}
