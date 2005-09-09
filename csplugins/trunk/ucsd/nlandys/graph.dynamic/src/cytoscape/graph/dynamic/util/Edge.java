package cytoscape.graph.dynamic.util;

// Package visible.
final class Edge implements java.io.Externalizable
{

  public final void writeExternal(final java.io.ObjectOutput out)
    throws java.io.IOException {
    out.writeInt(edgeId);
    out.writeBoolean(directed);
    out.writeInt(sourceNode);
    out.writeInt(targetNode); }
  public final void readExternal(final java.io.ObjectInput in)
    throws java.io.IOException {
    edgeId = in.readInt();
    directed = in.readBoolean();
    sourceNode = in.readInt();
    targetNode = in.readInt(); }

  int edgeId;
  Edge nextOutEdge;
  Edge prevOutEdge;
  Edge nextInEdge;
  Edge prevInEdge;
  boolean directed;
  int sourceNode;
  int targetNode;

  Edge() { edgeId = -1; }

}
