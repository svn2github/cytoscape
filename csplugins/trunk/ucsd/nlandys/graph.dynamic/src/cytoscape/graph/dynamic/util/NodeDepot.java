package cytoscape.graph.dynamic.util;

// Package visible.
final class NodeDepot implements java.io.Externalizable
{

  // Externalizable.  Docs say instantiated using public no-arg constructor.
  // No other references to the nodes in this depot exist.
  public final void writeExternal(final java.io.ObjectOutput out)
    throws java.io.IOException {
    for (Node currNode = m_head.nextNode; currNode != null;
         currNode = currNode.nextNode) out.writeInt(currNode.nodeId);
    out.writeInt(-1); }
  public final void readExternal(final java.io.ObjectInput in)
    throws java.io.IOException {
    Node currNode = m_head;
    while (true) {
      final int id = in.readInt();
      if (id < 0) break;
      currNode.nextNode = new Node();
      currNode = currNode.nextNode;
      currNode.nodeId = id; } }

  private final Node m_head;

  NodeDepot()
  {
    m_head = new Node();
  }

  // Gimme a node, darnit!
  // Don't forget to initialize the node's member variables!
  // Node.nextNode is used internally and will point to some undefined node
  // in the returned Node.
  Node getNode()
  {
    final Node returnThis = m_head.nextNode;
    if (returnThis == null) { return new Node(); }
    m_head.nextNode = returnThis.nextNode;
    return returnThis;
  }

  // node.nextNode is used internally and does not need to be deinitialized.
  void recycleNode(Node node)
  {
    node.nextNode = m_head.nextNode;
    m_head.nextNode = node;
  }

}
