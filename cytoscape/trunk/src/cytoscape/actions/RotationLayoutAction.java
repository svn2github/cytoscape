package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.graph.layout.algorithm.util.MutableGraphLayoutRepresentation;
import cytoscape.graph.layout.impl.RotationLayouter;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;
import giny.model.Edge;
import giny.view.EdgeView;
import giny.view.NodeView;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class RotationLayoutAction extends CytoscapeAction
{

  public RotationLayoutAction()
  {
    super("Rotate Graph... Just a Little (Hee Hee!)");
    setPreferredMenu("Layout");
  }

  public void actionPerformed(ActionEvent e)
  {
    CyNetworkView graphView = Cytoscape.getCurrentNetworkView();
    final int numNodes = graphView.getNodeViewCount();

    // Definition of nodeTranslation:
    // nodeTranslation[i] defines, for node at index i in our
    // GraphTopology object, the corresponding NodeView in Giny.
    final NodeView[] nodeTranslation = new NodeView[numNodes];

    // Definiton of nodeIndexTranslation:
    // Both keys and values of this hashtable are java.lang.Integer objects.
    // There are exactly numNodes keys in this hashtable.
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
    boolean[] mobility = new boolean[numNodes];
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
    if (nodeIndex != numNodes)
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
    final double[] nodeXPositions = new double[numNodes];
    final double[] nodeYPositions = new double[numNodes];
    for (int i = 0; i < numNodes; i++) {
      nodeXPositions[i] = nodeTranslation[i].getXPosition() - minX +
        ((maxX - minX) / 2);
      nodeYPositions[i] = nodeTranslation[i].getYPosition() - minY +
        ((maxY - minY) / 2); }
    final MutableGraphLayoutRepresentation nativeGraph =
      new MutableGraphLayoutRepresentation(numNodes,
                                           directedEdgeSourceNodeIndices,
                                           directedEdgeTargetNodeIndices,
                                           undirectedEdgeSourceNodeIndices,
                                           undirectedEdgeTargetNodeIndices,
                                           (maxX - minX) * 2,
                                           (maxY - minY) * 2,
                                           nodeXPositions,
                                           nodeYPositions,
                                           mobility);
    RotationLayouter.rotateGraph(nativeGraph, 0.1d);
    for (int i = 0; i < nodeTranslation.length; i++) {
      nodeTranslation[i].setOffset
        (nativeGraph.getNodePosition(i).getX() + minX - ((maxX - minX) / 2),
         nativeGraph.getNodePosition(i).getY() + minY - ((maxY - minY) / 2)); }
  }

}
