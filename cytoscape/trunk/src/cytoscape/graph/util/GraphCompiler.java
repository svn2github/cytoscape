package cytoscape.graph.util;

import cytoscape.graph.GraphTopology;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * An instance of this class &quot;compiles&quot; a <code>GraphTopology</code>
 * and provides several bits of useful information about the graph.<p>
 * An instance of this class is meant to be used by a single thread only.<p>
 **/
public final class GraphCompiler
{

  /**
   * No compiler hints.
   **/
  public static final long NO_COMPILER_HINTS =              0x0000000000000000;

  /**
   * The compiler compiles flagged functionalality in the constructor if this
   * flag is set; otherwise compilation is triggered only the first time
   * corresponding functionality is asked for.
   **/
  public static final long IMMEDIATE_COMPILE =              0x0000000000000001;

  /**
   * Hints to the compiler that <code>getNeighboringNodeIndices()</code>
   * will be used so that node neighbors lists should be compiled.
   * Compiling node neighbors information takes O(e) time where e is the
   * number of edges in a graph.  Node neighbor information takes up
   * O(e) memory in compiled form.
   **/
  public static final long COMPILE_NODE_NEIGHBORS =         0x0000000000000002;

  /**
   * Hints to the compiler that <code>getShortestPaths()</code> will be
   * used so that shortest path information should be compiled.
   **/
  public static final long COMPILE_SHORTEST_PATHS =         0x0000000000000004;

  /**
   * Hints to the compiler that pretty much all functionality provided by this
   * class will be used.
   **/
  public static final long COMPILE_ALL =
    COMPILE_NODE_NEIGHBORS |
    COMPILE_SHORTEST_PATHS;
    

  /**
   * I'm not sure whether or not having this method is a good idea.  If it is
   * I'm not sure whether or not having it <i>here</i> is a good idea.
   *
   * @exception IllegalArgumentException
   *   if <code>graph</code> does not pass the integrity check; in other
   *   words, some parts of <code>graph</code>'s definition are
   *   erroneous - this would happen, for example, if an edge's source
   *   node had an invalid index.
   **/
  public static void verifyTopologicalIntegrity(GraphTopology graph)
  {
    throw new RuntimeException("not yet implemented");
  }

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

  /**
   * <font color="#ff0000">IMPORTANT:</font> The <code>GraphTopology</code>
   * object passed to this constructor must have a non-mutable topology.  The
   * implementation of <code>GraphCompiler</code> may do incremental
   * compilation
   * of the graph; if graph topology changes over time, bad things could
   * happen.<p>
   * The <code>hints</code> value should be bitwise or-ed together static
   * member variables whose names contain <code>'COMPILE'</code>.
   * For example, if
   * you plan to use <code>getNeighboringNodeIndices()</code> and
   * <code>getShortestPaths()</code>, you could set the <code>hints</code>
   * parameter to
   * <nobr><code>COMPILE_NODE_NEIGHBORS | COMPILE_SHORTEST_PATHS</code></nobr>.
   **/
  public GraphCompiler(GraphTopology graph, long hints)
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
    compileNodeNeighbors();

    if (nodeIndex < 0 || nodeIndex >= graph.getNumNodes())
      throw new IndexOutOfBoundsException
        ("nodeIndex is out of range with value " + nodeIndex);

    if (honorDirectedEdges)
    {
      Hashtable specificNeighbors = m_nodeNeighbors[nodeIndex];
      if (specificNeighbors == null)
        specificNeighbors = m_dummyEmptyHashtable;
      final int[] returnThis = new int[specificNeighbors.size()];
      Enumeration values = specificNeighbors.elements();
      for (int i = 0; i < returnThis.length; i++)
        returnThis[i] = ((Integer) values.nextElement()).intValue();
      return returnThis;
    }
    else
    {
      Hashtable specificRealNeighbors = m_nodeNeighbors[nodeIndex];
      Hashtable specificFakeNeighbors = m_fakeNodeNeighbors[nodeIndex];
      if (specificRealNeighbors == null)
        specificRealNeighbors = m_dummyEmptyHashtable;
      if (specificFakeNeighbors == null)
        specificFakeNeighbors = m_dummyEmptyHashtable;

      // We're going to use the following hashtable to filter duplicates.
      Hashtable returnValues = new Hashtable();
      Enumeration values = specificRealNeighbors.elements();
      for (int i = 0; i < specificRealNeighbors.size(); i++) {
        Object o = values.nextElement();
        returnValues.put(o, o); }
      values = specificFakeNeighbors.elements();
      for (int i = 0; i < specificFakeNeighbors.size(); i++) {
        Object o = values.nextElement();
        returnValues.put(o, o); }
      final int[] returnThis = new int[returnValues.size()];
      values = returnValues.elements();
      for (int i = 0; i < returnThis.length; i++)
        returnThis[i] = ((Integer) values.nextElement()).intValue();
      return returnThis;
    }
  }

  private Hashtable[] m_nodeNeighbors = null;
  private Hashtable[] m_fakeNodeNeighbors = null;
  private final Hashtable m_dummyEmptyHashtable = new Hashtable();
  private boolean m_nodeNeighborsCompiled = false;

  private void compileNodeNeighbors()
  {
    if (m_nodeNeighborsCompiled) return;
    m_nodeNeighborsCompiled = true;

    m_nodeNeighbors = new Hashtable[graph.getNumNodes()];
    if (!(graph.areAllEdgesSimilar() && graph.getNumEdges() > 0 &&
          !graph.isDirectedEdge(0))) // At least one directed edge exists.
      m_fakeNodeNeighbors = new Hashtable[graph.getNumNodes()];
    for (int edgeIndex = 0; edgeIndex < graph.getNumEdges(); edgeIndex++)
    {
      Integer sourceNode =
        new Integer(graph.getEdgeNodeIndex(edgeIndex, true));
      Integer targetNode =
        new Integer(graph.getEdgeNodeIndex(edgeIndex, false));
      Hashtable specificNeighbors = m_nodeNeighbors[sourceNode.intValue()];
      if (specificNeighbors == null) specificNeighbors = new Hashtable();
      specificNeighbors.put(targetNode, targetNode);
      m_nodeNeighbors[sourceNode.intValue()] = specificNeighbors;
      Hashtable[] oppositePut;
      if (graph.isDirectedEdge(edgeIndex)) oppositePut = m_fakeNodeNeighbors;
      else oppositePut = m_nodeNeighbors;
      specificNeighbors = oppositePut[targetNode.intValue()];
      if (specificNeighbors == null) specificNeighbors = new Hashtable();
      specificNeighbors.put(sourceNode, sourceNode);
      oppositePut[targetNode.intValue()] = specificNeighbors;
    }
  }

}
