package cytoscape.graph.dynamic.util;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.util.intr.IntEnumerator;

class DynamicGraphRepresentation implements DynamicGraph
{

  DynamicGraphRepresentation() { }

  public IntEnumerator nodes()
  {
    return null;
  }

  public IntEnumerator edges()
  {
    return null;
  }

  public boolean removeNode(int node)
  {
    return false;
  }

  public int createNode()
  {
    return -1;
  }

  public boolean removeEdge(int edge)
  {
    return false;
  }

  public int createEdge(int sourceNode, int targetNode, boolean directed)
  {
    return -1;
  }

  public boolean containsNode(int node)
  {
    return false;
  }

  public boolean containsEdge(int edge)
  {
    return false;
  }

  public IntEnumerator adjacentEdges(int node, boolean undirected,
                                     boolean incoming, boolean outgoing)
  {
    return null;
  }

  public int sourceNode(int edge)
  {
    return -1;
  }

  public int targetNode(int edge)
  {
    return -1;
  }

  public byte isDirectedEdge(int edge)
  {
    return (byte) -1;
  }

}
