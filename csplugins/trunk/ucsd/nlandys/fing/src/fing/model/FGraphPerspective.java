package fing.model;

import giny.filter.Filter;
import giny.model.Edge;
import giny.model.GraphPerspective;
import giny.model.GraphPerspectiveChangeListener;
import giny.model.Node;
import giny.model.RootGraph;

import java.util.Iterator;

// Package visible class.
class FGraphPerspective implements GraphPerspective
{

  public void addGraphPerspectiveChangeListener
    (GraphPerspectiveChangeListener listener)
  {
    m_lis = GraphPerspectiveChangeListenerChain.add(m_lis, listener);
  }

  public void removeGraphPerspectiveChangeListener
    (GraphPerspectiveChangeListener listener)
  {
    m_lis = GraphPerspectiveChangeListenerChain.remove(m_lis, listener);
  }

  public Object clone()
  {
    throw new IllegalStateException("not implemented yet");
  }

  public RootGraph getRootGraph()
  {
    return m_root;
  }

  public int getNodeCount()
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getEdgeCount()
  {
    throw new IllegalStateException("not implemented yet");
  }

  public Iterator nodesIterator()
  {
    throw new IllegalStateException("not implemented yet");
  }

  public java.util.List nodesList()
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] getNodeIndicesArray()
  {
    throw new IllegalStateException("not implemented yet");
  }

  public Iterator edgesIterator()
  {
    throw new IllegalStateException("not implemented yet");
  }

  public java.util.List edgesList()
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] getEdgeIndicesArray()
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] getEdgeIndicesArray(int perspFromNodeInx,
                                   int perspToNodeInx,
                                   boolean includeUndirected,
                                   boolean includeBothDirections)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public Node hideNode(Node node)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int hideNode(int perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public java.util.List hideNodes(java.util.List nodes)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] hideNodes(int[] perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public Node restoreNode(Node node)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int restoreNode(int perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public java.util.List restoreNodes(java.util.List nodes)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public java.util.List restoreNodes(java.util.List nodes,
                                     boolean restoreIncidentEdges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] restoreNodes(int[] perspNodeInx,
                            boolean restoreIncidentEdges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] restoreNodes(int[] perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public Edge hideEdge(Edge edge)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int hideEdge(int perspEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public java.util.List hideEdges(java.util.List edges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] hideEdges(int[] perspEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public Edge restoreEdge(Edge edge)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int restoreEdge(int perspEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public java.util.List restoreEdges(java.util.List edges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] restoreEdges(int[] perspEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public boolean containsNode(Node node)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public boolean containsNode(Node node, boolean recurse)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public boolean containsEdge(Edge edge)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public boolean containsEdge(Edge edge, boolean recurse)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public GraphPerspective join(GraphPerspective persp)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public GraphPerspective createGraphPerspective(Node[] nodes, Edge[] edges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public GraphPerspective createGraphPerspective(int[] perspNodeInx,
                                                 int[] perspEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public GraphPerspective createGraphPerspective(Filter filter)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public java.util.List neighborsList(Node node)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] neighborsArray(int perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public boolean isNeighbor(Node aNodel, Node anotherNode)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public boolean isNeighbor(int perspNodeInx, int perspAnotherNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public boolean edgeExists(Node from, Node to)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public boolean edgeExists(int perspFromNodeInx, int perspToNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getEdgeCount(Node from, Node to, boolean countUndirectedEdges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getEdgeCount(int perspFromNodeInx, int perspToNodeInx,
                          boolean countUndirectedEdges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public java.util.List edgesList(Node from, Node to)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public java.util.List edgesList(int perspFromNodeInx,
                                  int perspToNodeInx,
                                  boolean includeUndirectedEdges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] getEdgeIndicesArray(int perspFromNodeInx,
                                   int perspToNodeInx,
                                   boolean includeUndirectedEdges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getInDegree(Node node)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getInDegree(int perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getInDegree(Node node, boolean countUndirectedEdges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getInDegree(int perspNodeInx, boolean countUndirectedEdges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getOutDegree(Node node)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getOutDegree(int perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getOutDegree(Node node, boolean countUndirectedEdges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getOutDegree(int perspNodeInx, boolean countUndirectedEdges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getDegree(Node node)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getDegree(int perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getIndex(Node node)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getNodeIndex(int rootGraphNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getRootGraphNodeIndex(int perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public Node getNode(int perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getIndex(Edge edge)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getEdgeIndex(int rootGraphEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getRootGraphEdgeIndex(int perspEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public Edge getEdge(int perspEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getEdgeSourceIndex(int perspEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getEdgeTargetIndex(int perspEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public boolean isEdgeDirected(int perspEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public boolean isMetaParent(Node child, Node parent)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public boolean isNodeMetaParent(int perspChildNodeInx,
                                  int perspParentNodeInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public java.util.List metaParentsList(Node node)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public java.util.List nodeMetaParentsList(int perspNodeInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public int[] getNodeMetaParentIndicesArray(int perspNodeInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public boolean isMetaChild(Node parent, Node child)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public boolean isNodeMetaChild(int perspNodeInx, int perspChildInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public java.util.List nodeMetaChildrenList(Node node)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public java.util.List nodeMetaChildrenList(int perspParentInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public int[] getNodeMetaChildIndicesArray(int perspNodeInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public boolean isMetaParent(Edge child, Node parent)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public boolean isEdgeMetaParent(int perspChildEdgeInx,
                                  int perspParentNodeInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public java.util.List metaParentsList(Edge edge)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public java.util.List edgeMetaParentsList(int perspEdgeInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public int[] getEdgeMetaParentIndicesArray(int perspEdgeInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public boolean isMetaChild(Node parent, Edge child)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public boolean isEdgeMetaChild(int perspParentNodeInx,
                                 int perspChildEdgeInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public java.util.List edgeMetaChildrenList(Node node)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public java.util.List edgeMetaChildrenList(int perspParentNodeInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public int[] getEdgeMetaChildIndicesArray(int perspParentNodeInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public java.util.List getAdjacentEdgesList(Node node,
                                             boolean undirected,
                                             boolean incoming,
                                             boolean outgoing)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] getAdjacentEdgeIndicesArray(int perspNodeInx,
                                           boolean undirected,
                                           boolean incoming,
                                           boolean outgoing)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public java.util.List getConnectingEdges(java.util.List nodes)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] getConnectingEdgeIndicesArray(int[] perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] getConnectingNodeIndicesArray(int[] perspEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public GraphPerspective createGraphPerspective(int[] perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  private RootGraph m_root;
  private GraphPerspectiveChangeListener m_lis;

  // Package visible constructor.
  FGraphPerspective(RootGraph root)
  {
    m_root = root;
    m_lis = null;
  }

}
