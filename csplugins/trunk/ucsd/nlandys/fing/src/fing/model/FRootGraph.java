package fing.model;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphFactory;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntHash;
import cytoscape.util.intr.IntIterator;
import cytoscape.util.intr.MinIntHeap;

import giny.model.Edge;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.RootGraph;
import giny.model.RootGraphChangeListener;

import java.util.Iterator;
import java.util.NoSuchElementException;

// Package visible class.  Use factory to get instance.
// This implementation of giny.model is safe to use with a single thread only.
class FRootGraph implements RootGraph
{

  // Not specified by giny.model.RootGraph.  GraphPerspective implementation
  // in this package relies on this method.
  void addRootGraphChangeListener(RootGraphChangeListener listener)
  { // This method is not thread safe; synchronize on an object to make it so.
    m_lis = RootGraphChangeListenerChain.add(m_lis, listener);
  }

  // Not specified by giny.model.RootGraph.  GraphPerspective implementation
  // in this package relies on this method.
  void removeRootGraphChangeListener(RootGraphChangeListener listener)
  { // This method is not thread safe; synchronize on an object to make it so.
    m_lis = RootGraphChangeListenerChain.remove(m_lis, listener);
  }

  public GraphPerspective createGraphPerspective(Node[] nodes, Edge[] edges)
  {
    return null;
  }

  public GraphPerspective createGraphPerspective(int[] nodeInx, int[] edgeInx)
  {
    return null;
  }

  public void ensureCapacity(int nodes, int edges)
  {
    System.out.println("The secret easter egg module has been activated.");
  }

  public int getNodeCount()
  {
    return m_graph.nodes().numRemaining();
  }

  public int getEdgeCount()
  {
    return m_graph.edges().numRemaining();
  }

  public Iterator nodesIterator()
  {
    final IntEnumerator nodes = m_graph.nodes();
    final FRootGraph rootGraph = this;
    return new Iterator() {
        public void remove() {
          throw new UnsupportedOperationException(); }
        public boolean hasNext() {
          return nodes.numRemaining() > 0; }
        public Object next() {
          if (!hasNext()) throw new NoSuchElementException();
          return rootGraph.getNode(~(nodes.nextInt())); } };
  }

  // This method has been marked deprecated in the Giny API.
  public java.util.List nodesList() {
    final int nodeCount = getNodeCount();
    final java.util.ArrayList returnThis = new java.util.ArrayList(nodeCount);
    Iterator iter = nodesIterator();
    for (int i = 0; i < nodeCount; i++) returnThis.add(iter.next());
    return returnThis; }

  // This method has been marked deprecated in the Giny API.
  public int[] getNodeIndicesArray()
  {
    IntEnumerator nodes = m_graph.nodes();
    final int[] returnThis = new int[nodes.numRemaining()];
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = ~(nodes.nextInt());
    return returnThis;
  }

  public Iterator edgesIterator()
  {
    final IntEnumerator edges = m_graph.edges();
    final FRootGraph rootGraph = this;
    return new Iterator() {
        public void remove() {
          throw new UnsupportedOperationException(); }
        public boolean hasNext() {
          return edges.numRemaining() > 0; }
        public Object next() {
          if (!hasNext()) throw new NoSuchElementException();
          return rootGraph.getEdge(~(edges.nextInt())); } };
  }

  // This method has been marked deprecated in the Giny API.
  public java.util.List edgesList() {
    final int edgeCount = getEdgeCount();
    final java.util.ArrayList returnThis = new java.util.ArrayList(edgeCount);
    Iterator iter = edgesIterator();
    for (int i = 0; i < edgeCount; i++) returnThis.add(iter.next());
    return returnThis; }

  // This method has been marked deprecated in the Giny API.
  public int[] getEdgeIndicesArray()
  {
    IntEnumerator edges = m_graph.edges();
    final int[] returnThis = new int[edges.numRemaining()];
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = ~(edges.nextInt());
    return returnThis;
  }

  public Node removeNode(Node node) {
    if (node.getRootGraph() == this &&
        removeNode(node.getRootGraphIndex()) < 0) return node;
    else return null; }

  public int removeNode(int nodeInx) {
    final Node removedNode = _removeNode(nodeInx);
    if (removedNode != null) {
      final RootGraphChangeListener listener = m_lis;
      if (listener != null)
        listener.rootGraphChanged
          (new RootGraphNodesRemovedEvent(this, new Node[] { removedNode }));
      return nodeInx; }
    else { return 0; } }

  // Returns the Node that was removed or null if unsuccessful.
  private Node _removeNode(int nodeInx)
  {
    final int positiveNodeIndex = ~nodeInx;
    final IntEnumerator edgeInxEnum;
    try { edgeInxEnum = m_graph.adjacentEdges
            (positiveNodeIndex, true, true, true); }
    catch (IllegalArgumentException e) { return null; }
    if (edgeInxEnum == null) return null;
    final int[] edgeRemoveArr = new int[edgeInxEnum.numRemaining()];
    for (int i = 0; i < edgeRemoveArr.length; i++)
      edgeRemoveArr[i] = ~(edgeInxEnum.nextInt());
    removeEdges(edgeRemoveArr);
    // positiveNodeIndex tested for validity with adjacentEdges() above.
    if (m_graph.removeNode(positiveNodeIndex)) {
      final FNode garbage = m_nodes.getNodeAtIndex(positiveNodeIndex);
      m_nodes.setNodeAtIndex(null, positiveNodeIndex);
      m_nodeDepot.recycleNode(garbage);
      return garbage; }
    else throw new IllegalStateException
           ("internal error - node didn't exist, its adjacent edges did");
  }

  // This method has been marked deprecated in the Giny API.
  public java.util.List removeNodes(java.util.List nodes) {
    final java.util.ArrayList returnThis = new java.util.ArrayList();
    for (int i = 0; i < nodes.size(); i++)
      if (removeNode((Node) nodes.get(i)) != null)
        returnThis.add(nodes.get(i));
    return returnThis; }

  public int[] removeNodes(int[] nodeIndices) {
    // Can't use m_heap because it's being used at each _removeNode(int).
    final MinIntHeap successes = new MinIntHeap();
    final Node[] removedNodes = new Node[nodeIndices.length];
    final int[] returnThis = new int[nodeIndices.length];
    for (int i = 0; i < nodeIndices.length; i++) {
      removedNodes[i] = _removeNode(nodeIndices[i]);
      if (removedNodes[i] == null) { returnThis[i] = 0; }
      else { returnThis[i] = nodeIndices[i]; successes.toss(i); } }
    if (successes.size() > 0) {
      final RootGraphChangeListener listener = m_lis;
      if (listener != null) {
        final Node[] successArr = new Node[successes.size()];
        final IntEnumerator enum = successes.elements();
        int index = -1;
        while (enum.numRemaining() > 0)
          successArr[++index] = removedNodes[enum.nextInt()];
        listener.rootGraphChanged
          (new RootGraphNodesRemovedEvent(this, successArr)); } }
    return returnThis; }

  public int createNode() {
    final int returnThis = _createNode();
    final RootGraphChangeListener listener = m_lis;
    if (listener != null)
      listener.rootGraphChanged
        (new RootGraphNodesCreatedEvent(this, new int[] { returnThis }));
    return returnThis; }

  private int _createNode()
  {
    final int positiveNodeIndex = m_graph.createNode();
    final int returnThis = ~positiveNodeIndex;
    // Theoretically I could postpone the creation of this object
    // and use a bit array to mark indices of nodes which aren't
    // instantiated yet.  This would complicate the code somewhat.
    FNode newNode = m_nodeDepot.getNode();
    newNode.m_rootGraph = this;
    newNode.m_rootGraphIndex = returnThis;
    newNode.m_identifier = null;
    m_nodes.setNodeAtIndex(newNode, positiveNodeIndex);
    return returnThis;
  }

  public int createNode(Node[] nodes, Edge[] edges) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public int createNode(GraphPerspective persoective) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public int createNode(int[] nodeIndices, int[] edgeindices) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public int[] createNodes(int numNewNodes) {
    final int[] returnThis = new int[numNewNodes];
    for (int i = 0; i < returnThis.length; i++) returnThis[i] = _createNode();
    final RootGraphChangeListener listener = m_lis;
    if (listener != null) {
      final int[] copyReturnThis = new int[returnThis.length];
      System.arraycopy(returnThis, 0, copyReturnThis, 0, returnThis.length);
      listener.rootGraphChanged
        (new RootGraphNodesCreatedEvent(this, copyReturnThis)); }
    return returnThis; }

  public Edge removeEdge(Edge edge) {
    if (edge.getRootGraph() == this &&
        removeEdge(edge.getRootGraphIndex()) < 0) return edge;
    else return null; }

  public int removeEdge(int edgeInx) {
    final Edge removedEdge = _removeEdge(edgeInx);
    if (removedEdge != null) {
      final RootGraphChangeListener listener = m_lis;
      if (listener != null)
        listener.rootGraphChanged
          (new RootGraphEdgesRemovedEvent(this, new Edge[] { removedEdge }));
      return edgeInx; }
    else { return 0; } }

  // Returns the Edge that was removed or null if unsuccessful.
  private Edge _removeEdge(int edgeInx)
  {
    final int positiveEdgeIndex = ~edgeInx;
    try {
      if (m_graph.removeEdge(positiveEdgeIndex)) {
        final FEdge garbage = m_edges.getEdgeAtIndex(positiveEdgeIndex);
        m_edges.setEdgeAtIndex(null, positiveEdgeIndex);
        m_edgeDepot.recycleEdge(garbage);
        return garbage; } }
    catch (IllegalArgumentException e) { }
    return null;
  }

  // This method has been marked deprecated in the Giny API.
  public java.util.List removeEdges(java.util.List edges) {
    final java.util.ArrayList returnThis = new java.util.ArrayList();
    for (int i = 0; i < edges.size(); i++)
      if (removeEdge((Edge) edges.get(i)) != null)
        returnThis.add(edges.get(i));
    return returnThis; }

  public int[] removeEdges(int[] edgeIndices) {
    m_heap.empty();
    final MinIntHeap successes = m_heap;
    final Edge[] removedEdges = new Edge[edgeIndices.length];
    final int[] returnThis = new int[edgeIndices.length];
    for (int i = 0; i < edgeIndices.length; i++) {
      removedEdges[i] = _removeEdge(edgeIndices[i]);
      if (removedEdges[i] == null) { returnThis[i] = 0; }
      else { returnThis[i] = edgeIndices[i]; successes.toss(i); } }
    if (successes.size() > 0) {
      final RootGraphChangeListener listener = m_lis;
      if (listener != null) {
        final Edge[] successArr = new Edge[successes.size()];
        final IntEnumerator enum = successes.elements();
        int index = -1;
        while (enum.numRemaining() > 0)
          successArr[++index] = removedEdges[enum.nextInt()];
        listener.rootGraphChanged
          (new RootGraphEdgesRemovedEvent(this, successArr)); } }
    return returnThis; }

  public int createEdge(Node source, Node target) {
    return createEdge
      (source, target,
       source.getRootGraphIndex() != target.getRootGraphIndex()); }

  public int createEdge(Node source, Node target, boolean directed) {
    if (source.getRootGraph() == this && target.getRootGraph() == this)
      return createEdge(source.getRootGraphIndex(),
                        target.getRootGraphIndex(), directed);
    else return 0; }

  public int createEdge(int sourceNodeIndex, int targetNodeIndex) {
    return createEdge(sourceNodeIndex, targetNodeIndex,
                      sourceNodeIndex != targetNodeIndex); }

  public int createEdge(int sourceNodeIndex, int targetNodeIndex,
                        boolean directed)
  {
    final int returnThis =
      _createEdge(sourceNodeIndex, targetNodeIndex, directed);
    if (returnThis != 0) {
      final RootGraphChangeListener listener = m_lis;
      if (listener != null)
        listener.rootGraphChanged
          (new RootGraphEdgesCreatedEvent(this, new int[] { returnThis })); }
    return returnThis;
  }

  private int _createEdge(int sourceNodeIndex, int targetNodeIndex,
                          boolean directed)
  {
    final int positiveSourceNodeIndex = ~sourceNodeIndex;
    final int positiveTargetNodeIndex = ~targetNodeIndex;
    final int positiveEdgeIndex;
    try { positiveEdgeIndex = m_graph.createEdge
            (positiveSourceNodeIndex, positiveTargetNodeIndex, directed); }
    catch (IllegalArgumentException e) { return 0; }
    final int returnThis;
    if (positiveEdgeIndex < 0) return 0;
    else returnThis = ~positiveEdgeIndex;
    // Theoretically I could postpone the creation of this object
    // and use a bit array to mark indices of edges which aren't
    // instantiated yet.  This would complicate the code somewhat.
    FEdge newEdge = m_edgeDepot.getEdge();
    newEdge.m_rootGraph = this;
    newEdge.m_rootGraphIndex = returnThis;
    newEdge.m_identifier = null;
    m_edges.setEdgeAtIndex(newEdge, positiveEdgeIndex);
    return returnThis;
  }

  public int[] createEdges(int[] sourceNodeIndices, int[] targetNodeIndices,
                           boolean directed) {
    int foo = targetNodeIndices[sourceNodeIndices.length - 1];
    foo = sourceNodeIndices[targetNodeIndices.length - 1];
    foo = 0;
    m_heap.empty();
    final MinIntHeap successes = m_heap;
    final int[] returnThis = new int[sourceNodeIndices.length];
    for (int i = 0; i < returnThis.length; i++) {
      returnThis[i] = createEdge(sourceNodeIndices[i],
                                 targetNodeIndices[i], directed);
      if (returnThis[i] != 0) successes.toss(returnThis[i]); }
    if (successes.size() > 0) {
      final RootGraphChangeListener listener = m_lis;
      if (listener != null) {
        final int[] successArr = new int[successes.size()];
        successes.copyInto(successArr, 0);
        listener.rootGraphChanged
          (new RootGraphEdgesCreatedEvent(this, successArr)); } }
    return returnThis; }

  public boolean containsNode(Node node) {
    return node.getRootGraph() == this &&
      getNode(node.getRootGraphIndex()) != null; }

  public boolean containsEdge(Edge edge) {
    return edge.getRootGraph() == this
      && getEdge(edge.getRootGraphIndex()) != null; }

  // This method has been marked deprecated in the Giny API.
  public java.util.List neighborsList(Node node) {
    if (node.getRootGraph() == this) {
      final int nodeIndex = node.getRootGraphIndex();
      int[] adjacentEdgeIndices =
        getAdjacentEdgeIndicesArray(nodeIndex, true, true, true);
      if (adjacentEdgeIndices == null) return null;
      m_hash.empty();
      final IntHash neighbors = m_hash;
      for (int i = 0; i < adjacentEdgeIndices.length; i++) {
        int neighborIndex = (nodeIndex ^
                             getEdgeSourceIndex(adjacentEdgeIndices[i]) ^
                             getEdgeTargetIndex(adjacentEdgeIndices[i]));
        neighbors.put(~neighborIndex); }
      IntEnumerator enum = neighbors.elements();
      java.util.ArrayList list = new java.util.ArrayList(enum.numRemaining());
      while (enum.numRemaining() > 0)
        list.add(getNode(~(enum.nextInt())));
      return list; }
    else { return null; } }

  // This method has been marked deprecated in the Giny API.
  public boolean isNeighbor(Node a, Node b) {
    if (a.getRootGraph() == this && b.getRootGraph() == this)
      return isNeighbor(a.getRootGraphIndex(), b.getRootGraphIndex());
    else return false; }

  // This method has been marked deprecated in the Giny API.
  public boolean isNeighbor(int nodeInxA, int nodeInxB)
  {
    final int positiveNodeInxA = ~nodeInxA;
    final int positiveNodeInxB = ~nodeInxB;
    final IntIterator connectingEdges;
    try {
      connectingEdges = m_graph.connectingEdges
        (positiveNodeInxA, positiveNodeInxB, true, true, true); }
    catch (IllegalArgumentException e) { return false; }
    if (connectingEdges == null) return false;
    return connectingEdges.hasNext();
  }

  // This method has been marked deprecated in the Giny API.
  public boolean edgeExists(Node from, Node to) {
    if (from.getRootGraph() == this && to.getRootGraph() == this)
      return edgeExists(from.getRootGraphIndex(),
                        to.getRootGraphIndex());
    else return false; }

  // This method has been marked deprecated in the Giny API.
  public boolean edgeExists(int fromNodeInx, int toNodeInx)
  {
    final int positiveFromNodeInx = ~fromNodeInx;
    final int positiveToNodeInx = ~toNodeInx;
    final IntIterator connectingEdges;
    try {
      connectingEdges = m_graph.connectingEdges
        (positiveFromNodeInx, positiveToNodeInx, true, false, true); }
    catch (IllegalArgumentException e) { return false; }
    if (connectingEdges == null) return false;
    return connectingEdges.hasNext();
  }

  // This method has been marked deprecated in the Giny API.
  public int getEdgeCount(Node from, Node to, boolean countUndirectedEdges) {
    if (from.getRootGraph() == this && to.getRootGraph() == this)
      return getEdgeCount(from.getRootGraphIndex(),
                          to.getRootGraphIndex(),
                          countUndirectedEdges);
    else return -1; }

  // This method has been marked deprecated in the Giny API.
  public int getEdgeCount(int fromNodeInx, int toNodeInx,
                          boolean countUndirectedEdges) {
    try { return getEdgeIndicesArray(fromNodeInx, toNodeInx,
                                     countUndirectedEdges).length; }
    catch (NullPointerException e) { return -1; } }

  public int[] getAdjacentEdgeIndicesArray(int nodeInx,
                                           boolean undirected,
                                           boolean incomingDirected,
                                           boolean outgoingDirected)
  {
    final int positiveNodeInx = ~nodeInx;
    final IntEnumerator adj;
    try { adj = m_graph.adjacentEdges(positiveNodeInx, outgoingDirected,
                                       incomingDirected, undirected); }
    catch (IllegalArgumentException e) { return null; }
    if (adj == null) return null;
    final int[] returnThis = new int[adj.numRemaining()];
    for (int i = 0; i < returnThis.length; i++) returnThis[i] = ~adj.nextInt();
    return returnThis;
  }

  public int[] getConnectingEdgeIndicesArray(int[] nodeInx)
  {
    // There are more edges than nodes so we'll use m_hash for the edges.
    final IntHash nodeBucket = new IntHash();
    for (int i = 0; i < nodeInx.length; i++) {
      final int positiveNodeIndex = ~nodeInx[i];
      try {
        if (m_graph.containsNode(positiveNodeIndex))
          nodeBucket.put(positiveNodeIndex); }
      catch (IllegalArgumentException e) { } }
    m_hash.empty();
    final IntHash edgeBucket = m_hash;
    final IntEnumerator nodeIter = nodeBucket.elements();
    while (nodeIter.numRemaining() > 0)
    {
      final int thePositiveNode = nodeIter.nextInt();
      final IntEnumerator edgeIter =
        m_graph.adjacentEdges(thePositiveNode, true, false, true);
      while (edgeIter.numRemaining() > 0)
      {
        final int candidateEdge = edgeIter.nextInt();
        final int otherEdgeNode = (thePositiveNode ^
                                   m_graph.sourceNode(candidateEdge) ^
                                   m_graph.targetNode(candidateEdge));
        if (otherEdgeNode == nodeBucket.get(otherEdgeNode))
          edgeBucket.put(candidateEdge);
      }
    }
    final IntEnumerator returnEdges = edgeBucket.elements();
    final int[] returnThis = new int[returnEdges.numRemaining()];
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = ~(returnEdges.nextInt());
    return returnThis;
  }

  // This method has been marked deprecated in the Giny API.
  public int[] getConnectingNodeIndicesArray(int[] edgeInx)
  {
    m_hash.empty();
    final IntHash nodeBucket = m_hash;
    for (int i = 0; i < edgeInx.length; i++)
    {
      final int positiveEdge = ~edgeInx[i];
      if (m_graph.containsEdge(positiveEdge))
      {
        nodeBucket.put(m_graph.sourceNode(positiveEdge));
        nodeBucket.put(m_graph.targetNode(positiveEdge));
      }
    }
    final IntEnumerator nodes = nodeBucket.elements();
    final int[] returnThis = new int[nodes.numRemaining()];
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = ~(nodes.nextInt());
    return returnThis;
  }

  // This method has been marked deprecated in the Giny API.
  public int[] getEdgeIndicesArray(int fromNodeInx,
                                   int toNodeInx,
                                   boolean undirectedEdges,
                                   boolean bothDirections)
  {
    final int positiveFromNodeInx = ~fromNodeInx;
    final int positiveToNodeInx = ~toNodeInx;
    final IntIterator connectingEdges;
    try {
      connectingEdges = m_graph.connectingEdges
        (positiveFromNodeInx, positiveToNodeInx, true, bothDirections,
         undirectedEdges); }
    catch (IllegalArgumentException e) { return null; }
    if (connectingEdges == null) return null;
    m_heap.empty();
    final MinIntHeap edgeBucket = m_heap;
    while (connectingEdges.hasNext())
      edgeBucket.toss(~connectingEdges.nextInt());
    final int[] returnThis = new int[edgeBucket.size()];
    edgeBucket.copyInto(returnThis, 0);
    return returnThis;
  }

  // This method has been marked deprecated in the Giny API.
  public java.util.List edgesList(Node from, Node to) {
    if (from.getRootGraph() == this && to.getRootGraph() == this)
      return edgesList(from.getRootGraphIndex(),
                       to.getRootGraphIndex(), true);
    else return null; }

  // This method has been marked deprecated in the Giny API.
  public java.util.List edgesList(int fromNodeInx, int toNodeInx,
                                  boolean includeUndirectedEdges) {
    final int[] edgeInx = getEdgeIndicesArray(fromNodeInx, toNodeInx,
                                              includeUndirectedEdges);
    java.util.ArrayList returnList = new java.util.ArrayList(edgeInx.length);
    for (int i = 0; i < edgeInx.length; i++)
      returnList.add(getEdge(edgeInx[i]));
    return returnList; }

  // This method has been marked deprecated in the Giny API.
  public int[] getEdgeIndicesArray(int fromNodeInx, int toNodeInx,
                                   boolean includeUndirectedEdges) {
    return getEdgeIndicesArray(fromNodeInx, toNodeInx,
                               includeUndirectedEdges, false); }

  public int getInDegree(Node node) {
    if (node.getRootGraph() == this)
      return getInDegree(node.getRootGraphIndex());
    else return -1; }

  public int getInDegree(int nodeInx) {
    return getInDegree(nodeInx, true); }

  public int getInDegree(Node node, boolean countUndirectedEdges) {
    if (node.getRootGraph() == this)
      return getInDegree(node.getRootGraphIndex(), countUndirectedEdges);
    else return -1; }

  public int getInDegree(int nodeInx, boolean countUndirectedEdges)
  {
    final int positiveNodeInx = ~nodeInx;
    final IntEnumerator adj;
    try { adj = m_graph.adjacentEdges
            (positiveNodeInx, false, true, countUndirectedEdges); }
    catch (IllegalArgumentException e) { return -1; }
    if (adj == null) return -1;
    return adj.numRemaining();
  }

  public int getOutDegree(Node node) {
    if (node.getRootGraph() == this)
      return getOutDegree(node.getRootGraphIndex());
    else return -1; }

  public int getOutDegree(int nodeInx) {
    return getOutDegree(nodeInx, true); }

  public int getOutDegree(Node node, boolean countUndirectedEdges) {
    if (node.getRootGraph() == this)
      return getOutDegree(node.getRootGraphIndex(), countUndirectedEdges);
    else return -1; }

  public int getOutDegree(int nodeInx, boolean countUndirectedEdges)
  {
    final int positiveNodeInx = ~nodeInx;
    final IntEnumerator adj;
    try { adj = m_graph.adjacentEdges
            (positiveNodeInx, true, false, countUndirectedEdges); }
    catch (IllegalArgumentException e) { return -1; }
    if (adj == null) return -1;
    return adj.numRemaining();
  }

  public int getDegree(Node node) {
    if (node.getRootGraph() == this)
      return getDegree(node.getRootGraphIndex());
    else return -1; }

  public int getDegree(int nodeInx)
  {
    final int positiveNodeInx = ~nodeInx;
    final IntEnumerator adj;
    try { adj = m_graph.adjacentEdges(positiveNodeInx, true, true, true); }
    catch (IllegalArgumentException e) { return -1; }
    if (adj == null) return -1;
    return adj.numRemaining();
  }

  public int getIndex(Node node) {
    if (node.getRootGraph() == this) return node.getRootGraphIndex();
    else return 0; }

  public Node getNode(int nodeInx) {
    if (nodeInx < 0) return m_nodes.getNodeAtIndex(~nodeInx);
    else return null; }

  public int getIndex(Edge edge) {
    if (edge.getRootGraph() == this) return edge.getRootGraphIndex();
    else return 0; }

  public Edge getEdge(int edgeInx) {
    if (edgeInx < 0) return m_edges.getEdgeAtIndex(~edgeInx);
    else return null; }

  public int getEdgeSourceIndex(int edgeInx)
  {
    try { return ~(m_graph.sourceNode(~edgeInx)); }
    catch (IllegalArgumentException e) { return 0; }
  }

  public int getEdgeTargetIndex(int edgeInx)
  {
    try { return ~(m_graph.targetNode(~edgeInx)); }
    catch (IllegalArgumentException e) { return 0; }
  }

  // Throws IllegalArgumentException.
  public boolean isEdgeDirected(int edgeInx)
  {
    return m_graph.isDirectedEdge(~edgeInx) == 1;
  }

  public boolean addMetaChild(Node parent, Node child) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public boolean addNodeMetaChild(int parentNodeInx, int childNodeInx) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public boolean removeNodeMetaChild(int parentNodeInx, int childNodeInx) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public boolean isMetaParent(Node child, Node parent) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public boolean isNodeMetaParent(int childNodeInx, int parentNodeInx) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public java.util.List metaParentsList(Node node) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public java.util.List nodeMetaParentsList(int nodeInx) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public int[] getNodeMetaParentIndicesArray(int nodeInx) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public boolean isMetaChild(Node parent, Node child) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public boolean isNodeMetaChild(int parentNodeInx, int childNodeInx) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public boolean isNodeMetaChild(int parentNodeInx, int childNodeInx,
                                 boolean recursive) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public java.util.List nodeMetaChildrenList(Node node) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public java.util.List nodeMetaChildrenList(int parentNodeInx) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public int[] getNodeMetaChildIndicesArray(int parentNodeInx) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public int[] getNodeMetaChildIndicesArray(int parentNodeInx,
                                            boolean recursive) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public int[] getChildlessMetaDescendants(int nodeInx) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public boolean addMetaChild(Node parent, Edge child) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public boolean addEdgeMetaChild(int parentNodeInx, int childEdgeInx) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public boolean removeEdgeMetaChild(int parentNodeInx, int childEdgeInx) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public boolean isMetaParent(Edge child, Node parent) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public boolean isEdgeMetaParent(int childEdgeInx, int parentNodeInx) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public java.util.List metaParentsList(Edge edge) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public java.util.List edgeMetaParentsList(int edgeInx) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public int[] getEdgeMetaParentIndicesArray(int edgeInx) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public boolean isMetaChild(Node parent, Edge child) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public boolean isEdgeMetaChild(int parentNodeInx, int childEdgeInx) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public java.util.List edgeMetaChildrenList(Node node) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public java.util.List edgeMetaChildrenList(int parentNodeInx) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  public int[] getEdgeMetaChildIndicesArray(int parentNodeInx) {
    throw new UnsupportedOperationException("meta nodes not yet supported"); }

  // The relationship between indices (both node and edge) in this
  // RootGraph and in the DynamicGraph is "flip the bits":
  // rootGraphIndex == ~(dynamicGraphIndex)
  private final DynamicGraph m_graph =
    DynamicGraphFactory.instantiateDynamicGraph();

  private RootGraphChangeListener m_lis = null;

  // This hash is re-used by many methods.  Make sure to empty() it before
  // using it.  You can use it as a bag of integers or to filter integer
  // duplicates.  You don't need to empty() it after usage.
  private final IntHash m_hash = new IntHash();

  // This heap is re-used by several methods.  It's used primarily as a bucket
  // of integers; sorting with this heap is [probably] not done at all.
  // Make sure to empty() it before using it.
  private final MinIntHeap m_heap = new MinIntHeap();

  // This is our "node factory" and "node recyclery".
  private final NodeDepository m_nodeDepot = new NodeDepository();

  // This is our "edge factory" and "edge recyclery".
  private final EdgeDepository m_edgeDepot = new EdgeDepository();

  // This is our index-to-node mapping.
  private final NodeArray m_nodes = new NodeArray();

  // This is our index-to-edge mapping.
  private final EdgeArray m_edges = new EdgeArray();

  // Package visible constructor.
  FRootGraph() { }

}
