//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import cytoscape.Cytoscape;
import cytoscape.graph.layout.algorithm.util.MutableGraphLayoutRepresentation;
import cytoscape.graph.layout.impl.SpringEmbeddedLayouter2;
import cytoscape.process.PercentCompletedCallback;
import cytoscape.process.RunStoppable;
import cytoscape.process.Stoppable;
import cytoscape.process.Task;
import cytoscape.process.ui.ProgressUI;
import cytoscape.process.ui.ProgressUIControl;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;
import giny.model.Edge;
import giny.view.EdgeView;
import giny.view.NodeView;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Vector;

public class SpringEmbeddedLayoutAction extends CytoscapeAction
{

  public SpringEmbeddedLayoutAction ()
  {
    super("Apply Spring Embedded Layout");
    setPreferredMenu("Layout");
  }

  public void actionPerformed (ActionEvent e)
  {
    // For the most part this is code copied and pasted from
    // cytoscape.layout.SpringEmbeddedLayouter, which has to stick around
    // because it's part of the "public API".
    CyNetworkView graphView = Cytoscape.getCurrentNetworkView();
    final int numNodesInTopology = graphView.getNodeViewCount();

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
      nodeXPositions[i] = nodeTranslation[i].getXPosition() - minX;
      nodeYPositions[i] = nodeTranslation[i].getYPosition() - minY; }
    final MutableGraphLayoutRepresentation nativeGraph =
      new MutableGraphLayoutRepresentation(numNodesInTopology,
                                           directedEdgeSourceNodeIndices,
                                           directedEdgeTargetNodeIndices,
                                           undirectedEdgeSourceNodeIndices,
                                           undirectedEdgeTargetNodeIndices,
                                           maxX - minX,
                                           maxY - minY,
                                           nodeXPositions,
                                           nodeYPositions,
                                           mobility);
    SpringEmbeddedLayouter2 layoutAlg =
      new SpringEmbeddedLayouter2(nativeGraph);

    //////////////////////////////////////////////////////////
    // BEGIN: The thread and process related code starts here.
    //////////////////////////////////////////////////////////

    final RunStoppable runStop = new RunStoppable((Task) layoutAlg);
    final boolean[] stoppd = new boolean[] { false }; // Monitor "Stop" button.
    final ProgressUIControl progCtrl = ProgressUI.startProgress
      (Cytoscape.getDesktop(),
       "Graph Layout",
       "Laying out graph; please wait...",
       new Stoppable() {
         public void stop() {
           stoppd[0] = true; // Use this to detect a pushed "Stop" button.
           ((Stoppable) runStop).stop(); } });
    layoutAlg.setPercentCompletedCallback((PercentCompletedCallback) progCtrl);
    Runnable runAndDispose = new Runnable() {
        public void run() {
          runStop.run(); // It's important that we call run() on the
                         // RunStoppable and NOT on the LayoutAlgorithm -
                         // otherwise the RunStoppable won't block on stop().
          // If our LayoutAlgorithm throws a RuntimeException in its run()
          // method, the modal dialog will remain forever.  If we don't want
          // this behavior, we should use 'try' block for run() and
          // 'finally' block for dispose().
          progCtrl.dispose(); } };
    (new Thread(runAndDispose)).start();
    progCtrl.show(); // This blocks until progCtrl.dispose() is called.
    if (stoppd[0]) return; // Return without laying out graph if stopped.

    //////////////////////////////////////////////////////
    // END: The thread and process related code ends here.
    //////////////////////////////////////////////////////

    for (int i = 0; i < nodeTranslation.length; i++)
      nodeTranslation[i].setOffset
        (nativeGraph.getNodePosition(i).getX() + minX,
         nativeGraph.getNodePosition(i).getY() + minY);
  }

}
