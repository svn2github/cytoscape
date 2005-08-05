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
    final int node1 = root.createNode();
    final int node2 = root.createNode();
    final int node3 = root.createNode();
    final int node4 = root.createNode();
    final int edge1 = root.createEdge(node1, node2, true);
    final int edge2 = root.createEdge(node1, node3, false);
    final int edge3 = root.createEdge(node3, node4, true);
    final int edge4 = root.createEdge(node2, node3, false);
    final int edge5 = root.createEdge(node4, node1, true);
    final int edge6 = root.createEdge(node4, node2, false);
    final int[] nodes = new int[] { node1, node2, node3, node4 };
    final int[] edges = new int[] { edge1, edge2, edge3, edge4, edge5, edge6 };
    final GraphPerspective persp = root.createGraphPerspective(nodes, edges);
    root.removeEdge(edge2);
    root.removeEdge(edge3);
    root.removeEdge(edge4);
    root.removeEdge(edge5);
    final int edge7 = root.createEdge(node4, node3, true);
    final int edge8 = root.createEdge(node3, node2, false);
    final Edge edge7Obj = root.getEdge(edge7);
    final Edge edge8Obj = root.getEdge(edge8);
    if (edge7 != edge7Obj.getRootGraphIndex() ||
        edge8 != edge8Obj.getRootGraphIndex()) {
      throw new IllegalStateException("the bug is here"); }
    if (edge7Obj.getSource() == null ||
        edge7Obj.getTarget() == null ||
        edge8Obj.getSource() == null ||
        edge8Obj.getTarget() == null) {
      throw new IllegalStateException("the bug is here"); }
  }

}
