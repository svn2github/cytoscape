package nct;

import java.util.Iterator;
import java.util.Set;

/**
 * The base interface for all graph classes and interfaces.
 * <tt>Graph</tt> defines basic functionality for a graph.
 *
 * <p>Each node and edge in a graph have unique int values. When creating
 * a node or an edge, its associated int value is returned. The returned node
 * int value is called the <i>node-index</i>; the returned edge int value is
 * called the <i>edge-index</i>.</p>
 *
 * <p><tt>Graph</tt> is generic. <tt>N</tt> is the node-type; <tt>E</tt> is the
 * edge-type. Each node-index is associated with a node-type object; the same
 * is true for edges. This allows generic definition of what can be stored
 * with nodes and edges.</p>
 *
 * <p><tt>Graph</tt> does not provide means for adding, modifying, or removing
 * edges and nodes. This functionality is provided by classes that implement
 * the <tt>MutableGraph</tt> interface. Having <tt>Graph</tt> provides an
 * interface for cheap, low-overhead classes.</p>
 *
 * @author Samad Lotia
 */
public interface Graph<N,E>
{
	/**
	 * @param edgeIndex An edge-index
	 * @return <tt>true</tt> if the edge-index is valid.
	 */
	public boolean edgeExists(int edgeIndex);
	
	/**
	 * @param nodeIndex An node-index
	 * @return <tt>true</tt> if the node-index is valid.
	 */
	public boolean nodeExists(int nodeIndex);

	/**
	 * @return edge object associated with an edge-index, or
	 *         <tt>null</tt> if the edge-index does not exist.
	 */
	public E edgeObject(int edgeIndex);
	
	/**
	 * @return node object associated with a node-index, or
	 *         <tt>null</tt> if the node-index does not exist.
	 */
	public N nodeObject(int nodeIndex);
	
	/**
	 * @return A set containing all valid node-indices in the graph.
	 */
	public Set<Integer> nodeSet();

	/**
	 * @return A set containing all valid edge-indices in the graph.
	 */
	public Set<Integer> edgeSet();

	/**
	 * @return An iterator for all valid node indices in the graph.
	 */
	public Iterator<Integer> nodeIterator();

	/**
	 * @return An iterator for all valid edge indices in the graph.
	 */
	public Iterator<Integer> edgeIterator();

	/**
	 * @return The number of nodes in the graph.
	 */
	public int nodeCount();

	/**
	 * @return The number of edges in the graph.
	 */
	public int edgeCount();
}
