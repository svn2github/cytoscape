package cytoscape.graph.dynamic.util;

// Package visible.
final class Node implements java.io.Externalizable
{

  public final void writeExternal(final java.io.ObjectOutput out)
    throws java.io.IOException {
    out.writeInt(nodeId);
    out.writeInt(outDegree);
    out.writeInt(inDegree);
    out.writeInt(undDegree);
    out.writeInt(selfEdges); }
  public final void readExternal(final java.io.ObjectInput in)
    throws java.io.IOException {
    nodeId = in.readInt();
    outDegree = in.readInt();
    inDegree = in.readInt();
    undDegree = in.readInt();
    selfEdges = in.readInt(); }

  int nodeId;
  Node nextNode;
  Node prevNode;
  Edge firstOutEdge;
  Edge firstInEdge;

  // The number of directed edges whose source is this node.
  int outDegree;

  // The number of directed edges whose target is this node.
  int inDegree;

  // The number of undirected edges which touch this node.
  int undDegree;

  // The number of directed self-edges on this node.
  int selfEdges;

  Node() { nodeId = -1; }

}
