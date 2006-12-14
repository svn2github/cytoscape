package fing.model.test;

import fing.model.FingEdgeDepot;
import fing.model.FingExtensibleRootGraph;
import fing.model.FingNodeDepot;
import giny.model.Edge;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.RootGraph;

public class Bug646Test
{

  private static class MyNodeDepot implements FingNodeDepot
  {
    public Node getNode(RootGraph root, int index, String id) {
      return new MyNode(root, index, id); }
    public void recycleNode(Node node) { }
  }

  private static class MyNode implements Node
  {
    final RootGraph m_rootGraph;
    final int m_rootGraphIndex;
    String m_identifier = null;
    MyNode(RootGraph root, int index, String id) {
      m_rootGraph = root;
      m_rootGraphIndex = index;
      m_identifier = id; }
    public GraphPerspective getGraphPerspective() {
      return m_rootGraph.createGraphPerspective
        (m_rootGraph.getNodeMetaChildIndicesArray(m_rootGraphIndex),
         m_rootGraph.getEdgeMetaChildIndicesArray(m_rootGraphIndex)); }
    public boolean setGraphPerspective(GraphPerspective gp) {
      if (gp.getRootGraph() != m_rootGraph) return false;
      final int[] nodeInx = gp.getNodeIndicesArray();
      final int[] edgeInx = gp.getEdgeIndicesArray();
      for (int i = 0; i < nodeInx.length; i++)
        m_rootGraph.addNodeMetaChild(m_rootGraphIndex, nodeInx[i]);
      for (int i = 0; i < edgeInx.length; i++)
        m_rootGraph.addEdgeMetaChild(m_rootGraphIndex, edgeInx[i]);
      return true; }
    public RootGraph getRootGraph() { return m_rootGraph; }
    public int getRootGraphIndex() { return m_rootGraphIndex; }
    public String getIdentifier() { return m_identifier; }
    public boolean setIdentifier(String new_id) {
      m_identifier = new_id;
      return true; }
  }

  private static class MyEdgeDepot implements FingEdgeDepot
  {
    public Edge getEdge(RootGraph root, int index, String id) {
      return new MyEdge(root, index, id); }
    public void recycleEdge(Edge edge) { }
  }

  private static class MyEdge implements Edge
  {
    final RootGraph m_rootGraph;
    final int m_rootGraphIndex;
    String m_identifier = null;
    MyEdge(RootGraph root, int index, String id) {
      m_rootGraph = root;
      m_rootGraphIndex = index;
      m_identifier = id; }
    public Node getSource() {
      return m_rootGraph.getNode
        (m_rootGraph.getEdgeSourceIndex(m_rootGraphIndex)); }
    public Node getTarget() {
      return m_rootGraph.getNode
        (m_rootGraph.getEdgeTargetIndex(m_rootGraphIndex)); }
    public boolean isDirected() {
      return m_rootGraph.isEdgeDirected(m_rootGraphIndex); }
    public RootGraph getRootGraph() { return m_rootGraph; }
    public int getRootGraphIndex() { return m_rootGraphIndex; }
    public String getIdentifier() { return m_identifier; }
    public boolean setIdentifier(String new_id) {
      m_identifier = new_id;
      return true; }
  }

  public static void main(String[] args)
  {
    final RootGraph root =
      new FingExtensibleRootGraph(new MyNodeDepot(), new MyEdgeDepot());
    final Node n1 = root.getNode(root.createNode());
    final Node n2 = root.getNode(root.createNode());
    final Node n3 = root.getNode(root.createNode());
    final Node n4 = root.getNode(root.createNode());
    root.createEdge(n1, n2, false);
    int edge_idx = root.createEdge(n1, n3, false);
    final Edge del1 = root.getEdge(edge_idx);
    edge_idx = root.createEdge(n2, n3, false);
    final Edge del2 = root.getEdge(edge_idx);
    root.removeEdge(del1);
    root.removeEdge(del2);
    edge_idx = root.createEdge(n1, n4, false);
    final Edge subedge = root.getEdge(edge_idx);
    final Node src = subedge.getSource();
    final Node target = subedge.getTarget();
    if (src == null || target == null)
      throw new IllegalStateException("the bug is here");
  }

}
