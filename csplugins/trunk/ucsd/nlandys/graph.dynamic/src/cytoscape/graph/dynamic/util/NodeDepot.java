package cytoscape.graph.dynamic.util;

final class NodeDepot
{

  private final Node m_head;

  NodeDepot()
  {
    m_head = new Node();
  }

  // Gimme a node, darnit!
  // Don't forget to initialize the node's member variables!
  Node getNode()
  {
    final Node returnThis = m_head.nextNode;
    if (returnThis == null) { return new Node(); }
    m_head.nextNode = returnThis.nextNode;
    return returnThis;
  }

  // Deinitialize the object's members yourself if you need or want to.
  void recycleNode(Node node)
  {
    node.nextNode = m_head.nextNode;
    m_head.nextNode = node;
  }

}
