package cytoscape.graph.fixed;

import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;

public interface FixedGraph
{

  public IntEnumerator nodes();
  public IntEnumerator edges();
  public boolean nodeExists(int node);
  public byte edgeType(int edge);
  public int edgeSource(int edge);
  public int edgeTarget(int edge);
  public IntEnumerator edgesAdjacent(int node, boolean outgoing,
                                     boolean incoming, boolean undirected);
  public IntIterator edgesConnecting(int node0, int node1,
                                     boolean outgoing, boolean incoming,
                                     boolean undirected);

}
