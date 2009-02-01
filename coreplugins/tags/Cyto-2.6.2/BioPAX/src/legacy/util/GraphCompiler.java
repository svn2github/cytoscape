
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package legacy.util;

import legacy.GraphTopology;
import legacy.IndexIterator;
import legacy.NodeNeighbors;

import java.util.Enumeration;
import java.util.Hashtable;


/**
 * <b>This class is in a very unfinished state; development effort on this
 * class has been suspended until further notice; don't use this class
 * unless you're the author.
 * </b><p>
 * An instance of this class &quot;compiles&quot; a <code>GraphTopology</code>
 * and provides several bits of useful information about the graph.<p>
 * An instance of this class is meant to be used by a single thread only.<p>
 */
public final class GraphCompiler implements NodeNeighbors {
	/**
	 * No compiler hints.
	 */
	public static final long NO_COMPILER_HINTS = 0x0000000000000000;

	/**
	 * Hints to the compiler that <code>getNeighboringNodeIndices()</code>
	 * will be used so that node neighbors lists should be compiled.
	 * Compiling node neighbors information takes O(e) time where e is the
	 * number of edges in a graph.  Node neighbor information takes up
	 * O(e) memory in compiled form.
	 */
	public static final long COMPILE_NODE_NEIGHBORS = 0x0000000000000002;

	/**
	 * 
	 */
	public static final long COMPILE_ADJACENT_EDGES = 0x0000000000000004;

	/**
	 * Hints to the compiler that <code>getShortestPaths()</code> will be
	 * used so that shortest path information should be compiled.
	 */
	public static final long COMPILE_SHORTEST_PATHS = 0x0000000000000008;

	/**
	 * Hints to the compiler that pretty much all functionality provided by this
	 * class will be used.
	 */
	public static final long COMPILE_ALL = COMPILE_NODE_NEIGHBORS | COMPILE_SHORTEST_PATHS;

	/**
	 * I'm not sure whether or not having this method is a good idea.  If it is
	 * I'm not sure whether or not having it <i>here</i> is a good idea.
	 *
	 * @throws IllegalArgumentException if <code>graph</code> does not pass the integrity check; in other
	 *                                  words, some parts of <code>graph</code>'s definition are
	 *                                  erroneous - this would happen, for example, if an edge's source
	 *                                  node had an invalid index.
	 */
	public static void verifyTopologicalIntegrity(GraphTopology graph) {
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
	 */
	public final GraphTopology graph;

	// The least restrictive definition.
	private final static NodeNeighborDefinition s_defaultNeighDef = new NodeNeighborDefinition() {
		public boolean isNodeNeighbor(int edgeIndex, int nodeAIndex, int nodeBIndex) {
			return true;
		}
	};

	private NodeNeighborDefinition m_neighDef = s_defaultNeighDef;

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
	 */
	public GraphCompiler(GraphTopology graph, long hints) {
		if (graph == null) {
			throw new NullPointerException("graph is null");
		}

		this.graph = graph;
	}

	/**
	 * Sets node neighbor definition to the specified definition, or to the
	 * default definition if <code>neighDef</code> is <code>null</code>.
	 * This method must be called before node neighbors are compiled.
	 * Your safest bet is to call this right after construction of this object.
	 *
	 * @throws IllegalStateException if node neighbors have already been
	 *                               compiled.
	 */
	public void setNodeNeighborDefinition(NodeNeighborDefinition neighDef) {
		if (m_nodeNeighborsCompiled) {
			throw new IllegalStateException("node neighbors already compiled");
		}

		m_neighDef = ((neighDef == null) ? s_defaultNeighDef : neighDef);
	}

	/**
	 * Returns a neighboring nodes list.<p>
	 * Let's define a binary relation on nodes in a graph, called
	 * <i>neighbor</i>: Node A is a <i>neighbor</i> of node
	 * B if and only if there exists an edge E such that A and B are
	 * endpoints of E.<p>
	 * What I mean by A and B being <i>endpoints</i>
	 * of an edge E: Node A is an <i>endpoint</i> of edge E if and only if
	 * at least one of the following is true:
	 * <ol><li>E is directed and A is the source node of E.</li>
	 * <li>E is directed and B is the target node of E.</li>
	 * <li>E is undirected and A is &quot;node 0&quot; of E.</li>
	 * <li>E is undirected and B is &quot;node 1&quot; of E.</li></ol><p>
	 * It can be proven that the <i>neighbors</i> relation on the set of nodes
	 * in a graph is <i>symmetric</i>; that is, if A is a neighbor of B then
	 * B is a neighbor of A.<p>
	 * If node N is the node at index <code>nodeIndex</code> then this method
	 * returns indices of all nodes Q such that Q is a neighbor of
	 * node N.<p>
	 * Let's now look at some examples so that we get a feeling for how
	 * this method behaves (referring back to the definition).
	 * <ul><li>A graph has exactly 2 [unique] nodes G and H and exactly one edge
	 * W which is undirected; G is &quot;node 0&quot; of W and H is
	 * &quot;node 1&quot; of W.  if we ask
	 * to get neighbors of G with this method, it should (and will)
	 * return a list of length 1, containing only node H.  Let's
	 * prove why G is not returned as a neighboring node of G.
	 * Assume G is a neighbor of G.  By definition, it follows that
	 * there exists an edge E such that E has endpoint nodes G and G.
	 * There exists only a single edge, and so W must have endpoint nodes
	 * G and G.  Contradiction.
	 * Therefore our assumption is false - that
	 * is, G is <i>not</i> a neighbor of G.  Therefore G
	 * will not be returned in the list of neighboring nodes of G.</li>
	 * </ul>
	 *
	 * @param nodeIndex the index of the node whose neighbors we're trying
	 *                  to find.
	 * @return a non-repeating list of indices of all nodes B such that
	 *         <i>B is a neighbor of node at index
	 *         <code>nodeIndex</code></i>; every entry in the returned iterator will
	 *         lie in the interval
	 *         <nobr><code>[0, graph.getNumNodex() - 1]</code></nobr>; this method
	 *         never returns <code>null</code>.
	 * @throws IndexOutOfBoundsException if <code>nodeIndex</code> is not
	 *                                   in the interval <nobr><code>[0, graph.getNumNodes() - 1]</code></nobr>.
	 */
	public IndexIterator getNeighboringNodeIndices(int nodeIndex) {
		compileNodeNeighbors();

		if ((nodeIndex < 0) || (nodeIndex >= graph.getNumNodes())) {
			throw new IndexOutOfBoundsException("nodeIndex is out of range with value " + nodeIndex);
		}

		return new ArrayIterator(m_nodeNeighbors[nodeIndex]);
	}

	private int[][] m_nodeNeighbors = null;
	private boolean m_nodeNeighborsCompiled = false;

	private void compileNodeNeighbors() {
		if (m_nodeNeighborsCompiled) {
			return;
		}

		m_nodeNeighborsCompiled = true;

		final Hashtable[] nodeNeighbors = new Hashtable[graph.getNumNodes()];

		for (int i = 0; i < nodeNeighbors.length; i++) {
			nodeNeighbors[i] = new Hashtable();
		}

		for (int edgeIndex = 0; edgeIndex < graph.getNumEdges(); edgeIndex++) {
			Integer nodeA = new Integer(graph.getEdgeNodeIndex(edgeIndex, true));
			Integer nodeB = new Integer(graph.getEdgeNodeIndex(edgeIndex, false));

			for (int i = 0; i < 2; i++) {
				if ((nodeNeighbors[nodeA.intValue()].get(nodeB) == null)
				    && m_neighDef.isNodeNeighbor(edgeIndex, nodeA.intValue(), nodeB.intValue())) {
					nodeNeighbors[nodeA.intValue()].put(nodeB, nodeB);
				}

				Integer temp = nodeB;
				nodeB = nodeA;
				nodeA = temp;
			}
		}

		m_neighDef = null; // Dereference because we're done using this.
		m_nodeNeighbors = new int[graph.getNumNodes()][];

		for (int i = 0; i < m_nodeNeighbors.length; i++) {
			Hashtable neighborsHash = nodeNeighbors[i];
			m_nodeNeighbors[i] = new int[neighborsHash.size()];

			Enumeration values = neighborsHash.elements();

			for (int j = 0; j < m_nodeNeighbors[i].length; j++) {
				m_nodeNeighbors[i][j] = ((Integer) values.nextElement()).intValue();
			}
		}
	}
}
