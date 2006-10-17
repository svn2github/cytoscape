package cytoscape.graph.dynamic.util;


// Package visible.
final class EdgeDepot {
    /* private */ final Edge m_head; // Not private for serialization.

    EdgeDepot() {
        m_head = new Edge();
    }

    // Gimme an edge, darnit!
    // Don't forget to initialize the edge's member variables!
    // Edge.nextOutEdge is used internally and will point to some undefined
    // edge in the returned Edge.
    final Edge getEdge() {
        final Edge returnThis = m_head.nextOutEdge;

        if (returnThis == null) {
            return new Edge();
        }

        m_head.nextOutEdge = returnThis.nextOutEdge;

        return returnThis;
    }

    // edge.nextOutEdge is used internally and does not need to be deinitialized.
    final void recycleEdge(final Edge edge) {
        edge.nextOutEdge = m_head.nextOutEdge;
        m_head.nextOutEdge = edge;
    }
}
