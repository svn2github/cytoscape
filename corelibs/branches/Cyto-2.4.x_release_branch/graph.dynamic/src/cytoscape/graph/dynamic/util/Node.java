package cytoscape.graph.dynamic.util;


// Package visible.
final class Node {
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

    Node() {
        nodeId = -1;
    }
}
