package cytoscape.foo;

import cytoscape.Cytoscape;
import cytoscape.graph.fixed.FixedGraph;
import cytoscape.graph.layout.algorithm.MutablePolyEdgeGraphLayout;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;
import cytoscape.view.CyNetworkView;

public final class GraphConverter2
{

  private GraphConverter2() {}

  /**
   * Returns a representation of Cytoscape's current network view.
   * Returns a MutablePolyEdgeGraphLayout, which, when mutated,
   * has a direct effect on the underlying Cytoscape network view.  You'd
   * sure as heck better be using the returned object from the AWT event
   * dispatch thread!  Better yet, lock the Cytoscape desktop somehow
   * (with a modal dialog for example) while using this returned object.
   * Movable nodes are defined to be selected nodes in Cytoscape - if no
   * nodes are selected then all nodes are movable.  If selected node
   * information changes while we have a reference to this return object,
   * then the movability of corresponding node also changes.  This is one
   * reason why it's important to lock the Cytoscape desktop while operating
   * on this return object.
   **/
  public static MutablePolyEdgeGraphLayout getGraphReference
    (double percentBorder,
     boolean preserveEdgeAnchors,
     boolean onlySelectedNodesMovable)
  {
    if (percentBorder < 0.0d)
      throw new IllegalArgumentException("percentBorder < 0.0");

    final CyNetworkView graphView = Cytoscape.getCurrentNetworkView();
    final FixedGraph fixedGraph = (FixedGraph) (graphView.getNetwork());

    return new MutablePolyEdgeGraphLayout()
      {
        // FixedGraph methods.
        public IntEnumerator nodes() { return fixedGraph.nodes(); }
        public IntEnumerator edges() { return fixedGraph.edges(); }
        public boolean nodeExists(int node) {
          return fixedGraph.nodeExists(node); }
        public byte edgeType(int edge) { return fixedGraph.edgeType(edge); }
        public int edgeSource(int edge) { return fixedGraph.edgeSource(edge); }
        public int edgeTarget(int edge) { return fixedGraph.edgeTarget(edge); }
        public IntEnumerator edgesAdjacent(int node, boolean out,
                                           boolean in, boolean undir) {
          return fixedGraph.edgesAdjacent(node, out, in, undir); }
        public IntIterator edgesConnecting(int node0, int node1,
                                           boolean out, boolean in,
                                           boolean undir) {
          return fixedGraph.edgesConnecting(node0, node1, out, in, undir); }

        // GraphLayout methods.
        public double getMaxWidth() { return 0.0; }
        public double getMaxHeight() { return 0.0; }
        public double getNodePosition(int node, boolean xPosition) {
          return 0.0; }

        // MutableGraphLayout methods.
        public boolean isMovableNode(int node) { return true; }
        public void setNodePosition(int node, double xPos, double yPos) {}

        // PolyEdgeGraphLayout methods.
        public int getNumAnchors(int edge) { return 0; }
        public double getAnchorPosition(int edge, int anchorIndex,
                                        boolean xPosition) {
          return 0.0; }

        // MutablePolyEdgeGraphLayout methods.
        public void deleteAnchor(int edge, int anchorIndex) {}
        public void createAnchor(int edge, int anchorIndex) {}
        public void setAnchorPosition(int edge, int anchorIndex,
                                      double xPos, double yPos) {}
      };
  }

}
