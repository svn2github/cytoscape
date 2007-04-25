package cytoscape.graph.dynamic.util;


// Package visible.
final class NodeDepot {
    /* private */ final Node m_head; // Not private for serialization.

    NodeDepot() {
        m_head = new Node();
    }

    // Gimme a node, darnit!
    // Don't forget to initialize the node's member variables!
    // Node.nextNode is used internally and will point to some undefined node
    // in the returned Node.
    final Node getNode() {
        final Node returnThis = m_head.nextNode;

        if (returnThis == null) {
            return new Node();
        }

        m_head.nextNode = returnThis.nextNode;

        return returnThis;
    }

    // node.nextNode is used internally and does not need to be deinitialized.
    final void recycleNode(final Node node) {
        node.nextNode = m_head.nextNode;
        m_head.nextNode = node;
    }
}
