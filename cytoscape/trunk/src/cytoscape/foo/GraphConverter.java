package cytoscape.foo;

import cytoscape.Cytoscape;
import cytoscape.graph.layout.GraphLayout;
import cytoscape.graph.layout.algorithm.MutableGraphLayout;
import cytoscape.graph.layout.algorithm.util.MutableGraphLayoutRepresentation;
import cytoscape.view.CyNetworkView;
import giny.model.Edge;
import giny.view.EdgeView;
import giny.view.NodeView;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/**
 * This class is very temporary.  It is in heavy flux until some other
 * APIs which are not part of core Cytoscape are finalized.
 * And yes, this class does belong in the core.  It's not a plugin, I feel.
 **/
public final class GraphConverter
{

  // No constructor.
  private GraphConverter() {}

  private static final class MyMutableGraphLayout
    extends MutableGraphLayoutRepresentation
  {
    // Definiition of m_nodeTranslation:
    // m_nodeTranslation[i] defines, for node at index i in our
    // GraphTopology object, the corresponding NodeView in Giny.
    final NodeView[] m_nodeTranslation;

    private MyMutableGraphLayout(int numNodes,
                                 int[] directedEdgeSourceNodeIndices,
                                 int[] directedEdgeTargetNodeIndices,
                                 int[] undirectedEdgeNode0Indices,
                                 int[] undirectedEdgeNode1Indices,
                                 double maxWidth,
                                 double maxHeight,
                                 double[] nodeXPositions,
                                 double[] nodeYPositions,
                                 boolean[] isMovableNode,
                                 NodeView[] nodeTranslation)
    {
      super(numNodes, directedEdgeSourceNodeIndices,
            directedEdgeTargetNodeIndices, undirectedEdgeNode0Indices,
            undirectedEdgeNode1Indices, maxWidth, maxHeight,
            nodeXPositions, nodeYPositions, isMovableNode);
      m_nodeTranslation = nodeTranslation;
    }
  }

  /**
   * Returns a representation of Cytoscape's current network view.
   * Returns a <code>MutableGraphLayout</code> which, when mutated, has no
   * effect on the underlying Cytoscape network view.  Use the return value
   * of this method when moving positions of nodes in a thread that is
   * not the AWT dispatch thread.  Use <code>updateCytoscapeLayout()</code>,
   * passing as an argument the return value of <code>getGraphCopy()</code>,
   * to move nodes in the underlying Cytoscape network view.
   * <code>getMaxWidth()</code> of the return object is the product of
   * {the width of the Cytoscape graph (as measured from node with minimum X
   * value to node with maximum X value)} and {<code>percentBorder</code>
   * plus one}.  Likewise, <code>getMaxHeight()</code> of the return object
   * is the product of {the height of the Cytoscape graph (as measured from
   * node with minimum Y value to node with maximum Y value)} and
   * {<code>percentBorder</code> plus one}.  All distances are preserved from
   * to Cytoscape graph to the return object.  In the return object, the
   * node with minimum X position will have an X position of
   * {<code>percentBorder</code> times half of the width of the Cytoscape
   * graph}, and the node with minimum Y position will have a Y position of
   * {<code>percentBorder</code> times half of the height of the Cytoscape
   * graph}.
   **/
  public static MutableGraphLayout getGraphCopy(double percentBorder)
  {
    if (percentBorder < 0.0d)
      throw new IllegalArgumentException("percentBorder < 0.0");

    CyNetworkView graphView = Cytoscape.getCurrentNetworkView();
    final int numNodesInTopology = graphView.getNodeViewCount();

    // Definiition of nodeTranslation:
    // nodeTranslation[i] defines, for node at index i in our
    // GraphTopology object, the corresponding NodeView in Giny.
    final NodeView[] nodeTranslation = new NodeView[numNodesInTopology];

    // Definiton of nodeIndexTranslation:
    // Both keys and values of this hashtable are java.lang.Integer objects.
    // There are exactly numNodesInTopology keys in this hashtable.
    // Key-to-value mappings define index-of-node-in-Giny to
    // index-of-node-in-GraphTopology mappings.  When I say
    // "index-of-node-in-Giny", I mean giny.model.Node.getRootGraphIndex().
    final Hashtable nodeIndexTranslation = new Hashtable();

    Iterator nodeIterator = graphView.getNodeViewsIterator();
    int nodeIndex = 0;
    double minX = Double.MAX_VALUE;
    double maxX = Double.MIN_VALUE;
    double minY = Double.MAX_VALUE;
    double maxY = Double.MIN_VALUE;
    boolean[] mobility = new boolean[numNodesInTopology];
    final boolean noNodesSelected =
      (graphView.getSelectedNodeIndices().length == 0);

    while (nodeIterator.hasNext())
    {
      NodeView currentNodeView = (NodeView) nodeIterator.next();
      nodeTranslation[nodeIndex] = currentNodeView;
      if (nodeIndexTranslation.put
          (new Integer(currentNodeView.getNode().getRootGraphIndex()),
           new Integer(nodeIndex)) != null)
        throw new IllegalStateException("Giny farted and someone lit a match");
      minX = Math.min(minX, currentNodeView.getXPosition());
      maxX = Math.max(maxX, currentNodeView.getXPosition());
      minY = Math.min(minY, currentNodeView.getYPosition());
      maxY = Math.max(maxY, currentNodeView.getYPosition());
      if (noNodesSelected) mobility[nodeIndex] = true;
      else mobility[nodeIndex] = currentNodeView.isSelected();
      nodeIndex++;
    }
    if (nodeIndex != numNodesInTopology)
      throw new IllegalStateException("something smells really bad here");
    Vector directedEdgeVector = new Vector();
    Vector undirectedEdgeVector = new Vector();
    Iterator edgeIterator = graphView.getEdgeViewsIterator();
    while (edgeIterator.hasNext())
    {
      Edge currentEdge = ((EdgeView) edgeIterator.next()).getEdge();
      int ginySourceNodeIndex = currentEdge.getSource().getRootGraphIndex();
      int ginyTargetNodeIndex = currentEdge.getTarget().getRootGraphIndex();
      int nativeSourceNodeIndex =
        ((Integer) nodeIndexTranslation.get
         (new Integer(ginySourceNodeIndex))).intValue();
      int nativeTargetNodeIndex =
        ((Integer) nodeIndexTranslation.get
         (new Integer(ginyTargetNodeIndex))).intValue();
      Vector chosenEdgeVector = undirectedEdgeVector;
      if (currentEdge.isDirected()) chosenEdgeVector = directedEdgeVector;
      chosenEdgeVector.add(new int[] { nativeSourceNodeIndex,
                                       nativeTargetNodeIndex });
    }
    final int[] directedEdgeSourceNodeIndices =
      new int[directedEdgeVector.size()];
    final int[] directedEdgeTargetNodeIndices =
      new int[directedEdgeVector.size()];
    for (int i = 0; i < directedEdgeVector.size(); i++) {
      int[] edge = (int[]) directedEdgeVector.get(i);
      directedEdgeSourceNodeIndices[i] = edge[0];
      directedEdgeTargetNodeIndices[i] = edge[1]; }
    final int[] undirectedEdgeSourceNodeIndices =
      new int[undirectedEdgeVector.size()];
    final int[] undirectedEdgeTargetNodeIndices =
      new int[undirectedEdgeVector.size()];
    for (int i = 0; i < undirectedEdgeVector.size(); i++) {
      int[] edge = (int[]) undirectedEdgeVector.get(i);
      undirectedEdgeSourceNodeIndices[i] = edge[0];
      undirectedEdgeTargetNodeIndices[i] = edge[1]; }
    final double[] nodeXPositions = new double[numNodesInTopology];
    final double[] nodeYPositions = new double[numNodesInTopology];
    for (int i = 0; i < numNodesInTopology; i++) {
      nodeXPositions[i] = nodeTranslation[i].getXPosition() - minX +
        ((maxX - minX) * percentBorder * 0.5d);
      nodeYPositions[i] = nodeTranslation[i].getYPosition() - minY +
        ((maxY - minY) * percentBorder * 0.5d); }
    return new MyMutableGraphLayout(numNodesInTopology,
                                    directedEdgeSourceNodeIndices,
                                    directedEdgeTargetNodeIndices,
                                    undirectedEdgeSourceNodeIndices,
                                    undirectedEdgeTargetNodeIndices,
                                    (maxX - minX) * (percentBorder + 1.0d),
                                    (maxY - minY) * (percentBorder + 1.0d),
                                    nodeXPositions,
                                    nodeYPositions,
                                    mobility,
                                    nodeTranslation);
  }

  /**
   * <code>layout</code> must be an object previously returned
   * by <code>GraphConverter.getGraphCopy()</code>.  During the time
   * that passes between setting <code>layout</code> as the return value
   * of <code>getGraphCopy()</code> and calling this method with the same
   * <code>layout</code>, Cytoscape's current network view topology
   * cannot change, otherwise this method will barf.
   *
   * @exception IllegalArgumentException if <code>layout</code> is not
   *   a value that was previously returned by <code>getGraphCopy()</code>.
   **/
  public static void updateCytoscapeLayout(GraphLayout layout)
  {
    MyMutableGraphLayout myLayout;
    try { myLayout = (MyMutableGraphLayout) layout; }
    catch (RuntimeException e) {
      throw new IllegalArgumentException
        ("layout is not a previous return value of getGraphCopy()"); }
  }

  public static MutableGraphLayout getGraphReference()
  {
    return null;
  }

}
