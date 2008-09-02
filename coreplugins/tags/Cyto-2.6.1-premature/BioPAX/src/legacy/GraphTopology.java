
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

package legacy;


/**
 * A bare-minimum definition of a graph.  A given edge in a graph can
 * be either <i>directed</i> or <i>undirected</i>.  Directedness of edges
 * is an important part of graph topology for calculations such as
 * finding a path <i>from</i> node A <i>to</i> node B.  A number of edges
 * between two nodes may exist; this number may have significance in some
 * applications, and so we don't want to represent an undirected edge
 * simply as two directed edges which are exact opposites of each other.<p>
 * Some extreme cases of graphs representable with this object are
 * graphs containing only nodes and no edges.  Another strange graph is one
 * with just one node and a million edges.  An erroneous graph is one
 * with one edge and no nodes, because the presence of one edge implies that
 * at least one node exists (an edge may in fact start and end at the same
 * node).<p>
 * Topologists claim that they can't tell a donut apart from a coffee cup.
 * In fact, a coffee cup with a handle can be &quot;smoothly morphed&quot;
 * into a donut shape.  In topology, distances do not matter but connectedness
 * does.  Therefore, a topological definition of a graph should not
 * contain any notion of a &quot;distance along edge&quot;.<p>
 * <!--
 * If the graph definition defined in this object were translated into
 * a language suitable for an elementary mathematics textbook on set theory,
 * it would read something like this (our version of set theory treats
 * {a,a} and {a} as not equal - that is, repeated elements in a set
 * are honored):
 * <blockquote>A <i>graph</i> is an ordered set of exactly 3 elements:
 * <ol><li>a set N of pairwise unique elements</li>
 * <li>a set of ordered pairs of elements in N, where an
 * <i>ordereed pair</i> is an ordered set of 2 elements</li>
 * <li>a set of pairs of elements in N, where a <i>pair</i>
 * is a set of 2 elements</li></ol>
 * To help our intuition, we can nickname N &quot;the set of nodes&quot;,
 * we can nickname the second set &quot;the set of directed edges&quot;,
 * and we can nickname the third set &quot;the set of undirected
 * edges&quot;.</blockquote>
 * An example set G defining a graph would be:
 * <blockquote><nobr>&lt; {a,b,c}, {&lt;a,b&gt;,&lt;b,c&gt;,&lt;c,a&gt;}, {} &gt;</nobr></blockquote>
 * Another example:
 * <blockquote><nobr>&lt; {d,e}, {&lt;d,e&gt;}, {{d,d},{d,e},{d.e},{e,d}} &gt;</nobr></blockquote>
 * Notice that
 * in the second example above, {d,e} is equal to {e,d}; this graph
 * has 3 &quot;undirected edges&quot; &quot;between nodes&quot; d and e.<p>
 * -->
 * The methods on this interface do not expose any mutable behavior; this does
 * not mean, however, that instances of this interface are not mutable.  A
 * sub-interface extending <code>GraphTopology</code> could be defined which
 * exposes mutable functionality.
 */
public interface GraphTopology {
	/**
	 * Returns the number of nodes in this graph.  In other methods of this
	 * interface a node is referenced by its index.  Indexes of nodes start
	 * at <code>0</code> and end at <nobr><code>getNumNodes() - 1</code></nobr>,
	 * inclusive.<p>
	 * Note: a graph which contains an edge must also contain at least one
	 * node; therefore, there are certain constraints on allowable return values.
	 * For example, if <code>getNumEdges()</code> returns <code>1</code>
	 * then <code>getNumNodes()</code> must return a value greater than zero.
	 *
	 * @return number of nodes in this graph.
	 */
	public int getNumNodes();

	/**
	 * Returns the number of edges in this graph.  In other methods of this
	 * interface an edge is referenced by its index.  Indexes of edges start
	 * at <code>0</code> and end at <nobr><code>getNumEdges() - 1</code></nobr>,
	 * inclusive.<p>
	 * Note: a graph which contains an edge must also contain at least one
	 * node; therefore, there are certain constraints on allowable return values.
	 * For example, if <code>getNumEdges()</code> returns <code>1</code>
	 * then <code>getNumNodes()</code> must return a value greater than zero.
	 *
	 * @return number of edges in this graph.
	 */
	public int getNumEdges();

	/**
	 * Returns the directedness of edge at index <code>edgeIndex</code>.
	 *
	 * @param edgeIndex index of edge whose directedness we're seeking.
	 * @return <code>true</code> if directed edge, <code>false</code> if
	 *         undirected edge.
	 * @throws IndexOutOfBoundsException if <code>edgeIndex</code> is not
	 *                                   in the interval <nobr><code>[0, getNumEdges() - 1]</code></nobr>.
	 */
	public boolean isDirectedEdge(int edgeIndex);

	/*
	* Returns <code>true</code> if and only if edges in this graph are
	* either all directed or all undirected.

	public boolean areAllEdgesSimilar();
	*/

	/**
	 * Returns an index to a node which is either the source node or the
	 * target node of edge at index <code>edgeIndex</code>.
	 *
	 * @param edgeIndex the index of the edge whose end nodes we are seeking.
	 * @param sourceNode if <code>true</code>, returns the source node of this
	 *   edge; if <code>false</code>, returns the target node of this edge -
	 *   for undirected edges &quot;source node&quot; should be interpreted
	 *   as &quot;node 0&quot; and &quot;target node&quot; should be
	 *   interpreted as &quot;node 1&quot;.
	 * @return the index of the node we are asking for; the return value
	 *   shall lie in the interval
	 *   <nobr><code>[0, getNumNodes() - 1]</code></nobr>.
	 * @exception IndexOutOfBoundsException if <code>edgeIndex</code> is not
	 *   in the interval <nobr><code>[0, getNumEdges() - 1]</code></nobr>.
	 **/
	public int getEdgeNodeIndex(int edgeIndex, boolean sourceNode);
}
