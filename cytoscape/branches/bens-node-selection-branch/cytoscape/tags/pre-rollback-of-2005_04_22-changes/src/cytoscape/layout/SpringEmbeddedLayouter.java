/*
 * VERY STRONG WARNING!!!
 * AVOID USING OR LOOKING AT THIS CODE!!  IT IS GOING TO GO AWAY VERY SOON!!!
 */

package cytoscape.layout;

import cytoscape.graph.legacy.layout.algorithm.util.MutableGraphLayoutRepresentation;
import cytoscape.graph.legacy.layout.impl.SpringEmbeddedLayouter2;
import cytoscape.view.CyNetworkView;
import giny.model.Edge;
import giny.view.EdgeView;
import giny.view.GraphView;
import giny.view.NodeView;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/**
 * VERY STRONG WARNING!!!
 * AVOID USING OR LOOKING AT THIS CODE!!  IT IS GOING TO GO AWAY VERY SOON!!!
 *
 * @deprecated Please avoid looking at or using this code -- this code
 *   is going away in the next Cytoscape release (the one after 2.1).
 **/
public class SpringEmbeddedLayouter extends AbstractLayout  {

  // I hope nobody complains about the missing public static fields.

  protected CyNetworkView graphView;

  public SpringEmbeddedLayouter(CyNetworkView graphView)
  {
    super(graphView);
    setGraphView(graphView);
  }

  public void setGraphView (CyNetworkView newGraphView)
  {
    graphView = newGraphView;
  }

  public GraphView getGraphView ()
  {
    return graphView;
  }

  public void lockNodes (NodeView[] nodes)
  {
    // WTF?
  }

  public void lockNode (NodeView v)
  {
    // WTF is going on here?
  }

  public void unlockNode(NodeView v)
  {
    // WTF is this?
  }

  public Object construct ()
  {
    final int numNodesInTopology = graphView.getNodeViewCount();
    final double maxLayoutDimension = 400.0d +
      Math.sqrt(((double) (numNodesInTopology * numNodesInTopology)) * 100.0d);

    // Definiition of nodeTranslation:
    // nodeindexTranslation[i] defines, for node at index i in our
    // GraphTopology object, the corresponding NodeView in Giny.  We just
    // need this to be able to call NodeView.setOffset() at the end - this
    // is what the legacy layout used to do.
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
    while (nodeIterator.hasNext())
    {
      NodeView currentNodeView = (NodeView) nodeIterator.next();
      nodeTranslation[nodeIndex] = currentNodeView;
      if (nodeIndexTranslation.put
          (new Integer(currentNodeView.getNode().getRootGraphIndex()),
           new Integer(nodeIndex++)) != null)
        throw new IllegalStateException("Giny farted and someone lit a match");
      minX = Math.min(minX, currentNodeView.getXPosition());
      maxX = Math.max(maxX, currentNodeView.getXPosition());
      minY = Math.min(minY, currentNodeView.getYPosition());
      maxY = Math.max(maxY, currentNodeView.getYPosition());
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
    final double xScaleFactor;
    if (((float) (maxX - minX)) == 0.0) xScaleFactor = 1.0d;
    else xScaleFactor = maxLayoutDimension / (maxX - minX);
    final double yScaleFactor;
    if (((float) (maxY - minY)) == 0.0) yScaleFactor = 1.0d;
    else yScaleFactor = maxLayoutDimension / (maxY - minY);
    for (int i = 0; i < numNodesInTopology; i++) {
       nodeXPositions[i] =
         Math.min(maxLayoutDimension,
                  Math.max(0.0d, (nodeTranslation[i].getXPosition() - minX) *
                           xScaleFactor));
       nodeYPositions[i] =
         Math.min(maxLayoutDimension,
                  Math.max(0.0d, (nodeTranslation[i].getYPosition() - minY) *
                           yScaleFactor)); }
    final MutableGraphLayoutRepresentation nativeGraph =
      new MutableGraphLayoutRepresentation(numNodesInTopology,
                                           directedEdgeSourceNodeIndices,
                                           directedEdgeTargetNodeIndices,
                                           undirectedEdgeSourceNodeIndices,
                                           undirectedEdgeTargetNodeIndices,
                                           maxLayoutDimension,
                                           maxLayoutDimension,
                                           nodeXPositions,
                                           nodeYPositions,
                                           null);
    (new SpringEmbeddedLayouter2(nativeGraph)).run();
    for (int i = 0; i < nodeTranslation.length; i++) {
      nodeTranslation[i].setOffset(nativeGraph.getNodePosition(i, true),
                                   nativeGraph.getNodePosition(i, false)); }
    return null;
  }

}
