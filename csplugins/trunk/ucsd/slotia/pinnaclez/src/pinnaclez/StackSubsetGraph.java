package pinnaclez;

import oiler.Graph;
import oiler.util.IntStack;
import oiler.util.IntHashSet;
import oiler.util.IntIterator;

/**
 * A subset of a graph called the parent;
 * nodes in the subset are stored in a stack.
 *
 * <p>This implementation enforces an order
 * of all nodes in the module such that nodes
 * added first come before nodes added later.
 * When calling <code>nodes()</code>, this
 * class first returns nodes added
 * before nodes added later.</p>
 *
 * <p>This implementation only supports adding
 * nodes to the subset of a graph. It does
 * not support mutable operations like
 * <code>addNode()</code> or
 * <code>addEdge()</code>.</p>
 *
 * <p>This implementation places an emphasis
 * on quickly adding nodes to the subset.
 * It does not keep track of what edges are
 * part of the subset. Therefore, many of
 * the methods are very slow while others
 * are very fast.</p>
 */
public class StackSubsetGraph<N,E> implements Graph<N,E>
{
	protected final Graph<N,E> parent;
	protected final IntStack nodes;
	protected double score;

	public StackSubsetGraph(final Graph<N,E> parent, final int startNode)
	{
		this.parent = parent;
		nodes = new IntStack();
		nodes.push(startNode);
		score = -1.0;
	}

	public Graph<N,E> parent()
	{
		return parent;
	}

	public double score()
	{
		return score;
	}

	public void setScore(double score)
	{
		this.score = score;
	}

	/**
	 * This method is fairly slow; use sparingly.
	 */
	public boolean edgeExists(int edgeIndex)
	{
		if (!parent.edgeExists(edgeIndex))
			return false;
		else if (!nodeExists(parent.edgeSource(edgeIndex)))
			return false;
		else if (!nodeExists(parent.edgeTarget(edgeIndex)))
			return false;
		else
			return true;
	}

	public boolean nodeExists(int nodeIndex)
	{
		return nodes.contains(nodeIndex);
	}

	/**
	 * Not supported.
	 * @throws UnsupportedOperationException always
	 */
	public int addEdge(int sourceIndex, int targetIndex, E edgeObj, byte edgeType)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Not supported.
	 * @throws UnsupportedOperationException always
	 */
	public int addNode(N nodeObj)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Add a node to the subset graph.
	 */
	public void addNode(int nodeIndex)
	{
		nodes.push(nodeIndex);
	}

	/**
	 * Not supported.
	 * @throws UnsupportedOperationException always
	 */
	public void removeEdge(int edgeIndex)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Not supported.
	 * @throws UnsupportedOperationException always
	 */
	public void removeNode(int edgeIndex)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Removes a node from the subset graph;
	 * this does not affect the parent graph
	 * so the node is not really removed.
	 */
	public void removeNode()
	{
		nodes.pop();
	}

	public E edgeObject(int edgeIndex)
	{
		return parent.edgeObject(edgeIndex);
	}

	public N nodeObject(int nodeIndex)
	{
		return parent.nodeObject(nodeIndex);
	}

	/**
	 * Not supported.
	 * @throws UnsupportedOperationException always
	 */
	public void setEdgeObject(int edgeIndex, E edgeObj)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Not supported.
	 * @throws UnsupportedOperationException always
	 */
	public void setNodeObject(int nodeIndex, N nodeObj)
	{
		throw new UnsupportedOperationException();
	}

	public int edgeIndex(E edgeObj)
	{
		return parent.edgeIndex(edgeObj);
	}

	public int nodeIndex(N nodeObj)
	{
		return parent.nodeIndex(nodeObj);
	}

	public int maxNodeIndex()
	{
		return parent.maxNodeIndex();
	}

	/**
	 * This method is very slow; use sparingly.
	 */
	public IntIterator edges()
	{
		final IntHashSet edges = new IntHashSet();
		final IntIterator parentEdges = parent.edges();
		while (parentEdges.hasNext())
		{
			final int edge = parentEdges.next();
			if (edgeExists(edge))
				edges.add(edge);
		}
		return edges.iterator();
	}

	public IntIterator nodes()
	{
		return nodes.iterator();
	}

	public int nodeCount()
	{
		return nodes.size();
	}

	public int edgeCount()
	{
		int edgeCount = 0;
		final IntIterator parentEdges = parent.edges();
		while (parentEdges.hasNext())
		{
			final int edge = parentEdges.next();
			if (edgeExists(edge))
				edgeCount++;
		}
		return edgeCount;
	}

	public int edgeSource(int edgeIndex)
	{
		return parent.edgeSource(edgeIndex);
	}

	public int edgeTarget(int edgeIndex)
	{
		return parent.edgeTarget(edgeIndex);
	}

	public byte edgeType(int edgeIndex)
	{
		return parent.edgeType(edgeIndex);
	}

	public byte defaultEdgeType()
	{
		return parent.defaultEdgeType();
	}

	/**
	 * This method is very slow; use sparingly.
	 */
	public int degree(int nodeIndex)
	{
		return degree(nodeIndex, defaultEdgeType());
	}

	/**
	 * This method is very slow; use sparingly.
	 */
	public int degree(int nodeIndex, byte edgeType)
	{
		final IntHashSet adjEdges = new IntHashSet();
		IntIterator parentAdjEdges = parent.adjacentEdges(nodeIndex, edgeType);
		while (parentAdjEdges.hasNext())
		{
			final int adjEdge = parentAdjEdges.next();
			if (edgeExists(adjEdge))
				adjEdges.add(adjEdge);
		}
		return adjEdges.size();
	}

	/**
	 * This method is very slow; use sparingly.
	 */
	public IntIterator adjacentNodes(int nodeIndex)
	{
		return adjacentNodes(nodeIndex, defaultEdgeType());
	}

	/**
	 * This method is very slow; use sparingly.
	 */
	public IntIterator adjacentNodes(int nodeIndex, byte edgeType)
	{
		final IntHashSet adjNodes = new IntHashSet();
		IntIterator parentAdjNodes = parent.adjacentNodes(nodeIndex, edgeType);
		while (parentAdjNodes.hasNext())
		{
			final int adjNode = parentAdjNodes.next();
			if (nodes.contains(adjNode))
				adjNodes.add(adjNode);
		}
		return adjNodes.iterator();
	}

	/**
	 * This method is very slow; use sparingly.
	 */
	public IntIterator adjacentEdges(int edgeIndex)
	{
		return adjacentEdges(edgeIndex, defaultEdgeType());
	}

	/**
	 * This method is very slow; use sparingly.
	 */
	public IntIterator adjacentEdges(int edgeIndex, byte edgeType)
	{
		final IntHashSet adjEdges = new IntHashSet();
		IntIterator parentAdjEdges = parent.adjacentEdges(edgeIndex, edgeType);
		while (parentAdjEdges.hasNext())
		{
			final int adjEdge = parentAdjEdges.next();
			if (edgeExists(adjEdge))
				adjEdges.add(adjEdge);
		}
		return adjEdges.iterator();
	}

	public int firstEdge(int sourceIndex, int targetIndex)
	{
		return firstEdge(sourceIndex, targetIndex, defaultEdgeType());
	}

	public int firstEdge(int sourceIndex, int targetIndex, byte edgeType)
	{
		final int edge = parent.firstEdge(sourceIndex, targetIndex, edgeType);
		if (edgeExists(edge))
			return edge;
		else
			return Graph.INVALID_INDEX;
	}

	public int compareTo(final Graph<N,E> other)
	{
		return hashCode() - other.hashCode();
	}
}
