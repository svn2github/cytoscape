package cytoscape.graph.util;

import cytoscape.graph.GraphTopology;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * This class &quot;compiles&quot; a <code>GraphTopology</code> and provides
 * several computations on the graph.<p>
 * An instance of this class is meant to be used by a single thread only.<p>
 * This class is very tentative at this point - it may go away soon.
 **/
public final class GraphUtils
{

  /**
   * <code>graph</code> stores a reference to the underlying graph
   * topology - this happens to be exactly the <code>GraphTopology</code>
   * that was passed to the contructor.<p>
   * The programmer who wrote this class prefers to have a
   * <nobr><code>final public</code></nobr>
   * non-mutable member variable as opposed to a having a
   * <code>getXXX()</code> method.
   **/
  public final GraphTopology graph;

  private boolean m_compiled = false;

  /**
   * <font color="#ff0000">IMPORTANT:</font> The <code>GraphTopology</code>
   * object passed to this constructor must have a non-mutable topology.  The
   * implementation of <code>GraphUtils</code> may do incremental compilation
   * of the graph; if graph topology changes over time, bad things could
   * happen.
   **/
  public GraphUtils(GraphTopology graph)
  {
    if (graph == null) throw new NullPointerException("graph is null");
    this.graph = graph;
  }

  /**
   * Returns a neighboring nodes list.<p>
   * Let's define a binary relation on nodes in a graph, called
   * <i>directed neighbor</i>: Node B is a <i>directed neighbor</i> of node
   * A if and only if at least one of the following is true:
   * <ol><li>There exists a directed edge whose target node is B and whose
   *         source node is A.</li>
   *     <li>There exists an undirected edge whose end nodes are A and B.</li>
   * </ol><p>
   * If node N is the node at index <code>nodeIndex</code> then this method
   * returns indices of all nodes Q such that Q is a directed neighbor of
   * node N.<p>
   * Let's now look at some examples so that we get a feeling for how
   * this method behaves (referring back to the definition).
   * <ul><li>A graph has exactly 2 [unique] nodes A and B and exactly one edge
   *         E which is undirected; A and B are end-nodes of E.  if we ask
   *         to get neighbors of A with this method, it should (and will)
   *         return a list of length 1, containing only node B.  Let's
   *         prove why A is not returned as a neighboring node of A.
   *         Assume A is a directed neighbor of A.  There exist no directed
   *         edges in this graph, therefore condition 2 in the definition
   *         of <i>directed neighbor</i> must hold true.  This implies that
   *         there exists an undirected edge whose end nodes are A and A.
   *         But this is incorrect.  Therefore our assumption is false - that
   *         is, A is <i>not</i> a directed neighbor of A.  Therefore A
   *         will not be returned in the list of neighboring nodes of A.</li>
   * </ul>
   *
   * @param nodeIndex the index of the node whose neighbors we're trying
   *   to find.
   * @param honorDirectedEdges we treat directed edges as undirected if
   *   and only if this is <code>false</code>.
   * @return a non-repeating list of indices of all nodes B such that
   *   <i>B is a directed neighbor of node at index
   *   <code>nodeIndex</code></i>; every entry in the returned array will
   *   lie in the interval
   *   <nobr><code>[0, graph.getNumNodex() - 1]</code></nobr>; this method
   *   never returns <code>null</code>.
   * @exception IndexOutOfBoundsException if <code>nodeIndex</code> is not
   *   in the interval <nobr><code>[0, graph.getNumNodes() - 1]</code></nobr>.
   **/
  public int[] getNeighboringNodeIndices(int nodeIndex,
                                         boolean honorDirectedEdges)
  {
    ensureCompileGraph();

    // Compute using the brute-force method; optimize when this class
    // evolves more.  We could optimize A LOT if we were required to
    // compute the neighbors of every node in the graph.
    if (nodeIndex < 0 || nodeIndex >= graph.getNumNodes())
      throw new IndexOutOfBoundsException
        ("nodeIndex is out of range with value " + nodeIndex);
    final Hashtable nodeNeighInx = new Hashtable();
    for (int edgeIndex = 0; edgeIndex < graph.getNumEdges(); edgeIndex++)
    {
      boolean nodeIsSource = false;
      if (((nodeIndex == graph.getEdgeNodeIndex(edgeIndex, true)) &&
           (nodeIsSource = true)) ||
          (((!graph.isDirectedEdge(edgeIndex)) || (!honorDirectedEdges)) &&
           (nodeIndex == graph.getEdgeNodeIndex(edgeIndex, false))))
      {
        Integer neighborToPut =
          new Integer(graph.getEdgeNodeIndex(edgeIndex, !nodeIsSource));
        // We're using Hashtable to automatically filter duplicates for us.
        nodeNeighInx.put(neighborToPut, neighborToPut);
      }
    }
    final int[] returnThis = new int[nodeNeighInx.size()];
    Enumeration values = nodeNeighInx.elements();
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = ((Integer) values.nextElement()).intValue();
    return returnThis;
  }

  private void ensureCompileGraph()
  {
    // As this class matures more we should implement a single compilation
    // to avoid overhead on every method call.
  }

}
