package cytoscape.graph.layout.algorithm.util;

import cytoscape.graph.layout.algorithm.MutableGraphLayout;
import cytoscape.graph.layout.util.GraphLayoutRepresentation;

/**
 * This class provides an implementation of
 * <code>MutableGraphLayout</code> whose only purpose is to
 * represent a mutable graph layout based on structure defined in arrays of
 * integers and floating-point numbers.
 * Methods on an instance of this class have no hooks into outside code.
 **/
public class MutableGraphLayoutRepresentation
  extends GraphLayoutRepresentation
  implements MutableGraphLayout
{

  /**
   * Mobility of nodes for subclasses that implement mutable functionality.
   **/
  protected final boolean[] m_isMovableNode;

  /**
   * Member variable defining <code>areAllNodesMovable()</code> to be
   * used by subclasses if necessary.  Subclasses should take care to keep
   * <code>m_areAllNodesMovable</code> consistent with
   * the values in <code>m_isMovableNode</code>.
   **/
  protected boolean m_areAllNodesMovable;

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
   * @param isMovableNode
   *   <blockquote>an array of length <code>numNodes</code> such that
   *   <code>isMovableNode[nodeIndex]</code> defines
   *   <code>isMovableNode(nodeIndex)</code>; if <code>isMovableNode</code>
   *   is <code>null</code>, all
   *   nodes in this graph are defined to be movable.</blockquote>
   * @exception IllegalArgumentException if parameters are passed which
   *   don't agree with a possible graph definition.
   * @see GraphLayoutRepresentation#GraphLayoutRepresentation(int, int[], int[], int[], int[], double, double, double[], double[])
   **/
  public MutableGraphLayoutRepresentation(int numNodes,
                                          int[] directedEdgeSourceNodeIndices,
                                          int[] directedEdgeTargetNodeIndices,
                                          int[] undirectedEdgeNode0Indices,
                                          int[] undirectedEdgeNode1Indices,
                                          double maxWidth,
                                          double maxHeight,
                                          double[] nodeXPositions,
                                          double[] nodeYPositions,
                                          boolean[] isMovableNode)
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
    if (isMovableNode == null) {
      isMovableNode = new boolean[getNumNodes()];
      for (int i = 0; i < isMovableNode.length; i++) {
        isMovableNode[i] = true; } }

    // Real parameter checking.  Set member variables.
    if (isMovableNode.length != getNumNodes())
      throw new IllegalArgumentException
        ("is movable node array does not have length numNodes");
    m_isMovableNode = new boolean[isMovableNode.length];
    System.arraycopy(isMovableNode, 0, m_isMovableNode, 0,
                     isMovableNode.length);
    m_areAllNodesMovable = true;
    for (int i = 0; i < m_isMovableNode.length; i++) {
      if (!m_isMovableNode[i]) { m_areAllNodesMovable = false; break; } }
  }

  public final boolean areAllNodesMovable() { return m_areAllNodesMovable; }

  public final boolean isMovableNode(int nodeIndex) {
    // This will automatically throw an ArrayIndexOutOfBoundsException,
    // which is a subclass of IndexOutOfBoundsException, if nodeIndex
    // is not a valid index.
    return m_isMovableNode[nodeIndex]; }

  public final void setNodePosition(int nodeIndex, double xPos, double yPos) {
    if (!isMovableNode(nodeIndex)) // Will throw IndexOutOfBoundsException
                                   // if nodeIndex is out of bounds.
      throw new UnsupportedOperationException
        ("trying to move node at index " + nodeIndex + " - non-movable node");
    if (xPos < 0.0d || xPos > getMaxWidth() ||
        yPos < 0.0d || yPos > getMaxHeight())
      throw new IllegalArgumentException
        ("trying to set node position outside of allowable rectangle");
    m_nodeXPositions[nodeIndex] = xPos;
    m_nodeYPositions[nodeIndex] = yPos; }

}
