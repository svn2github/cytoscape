package oiler;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

import oiler.util.IntIterator;
import oiler.util.IntIntHashMap;

/**
 * A mutable linked-list implementation of <tt>Graph</tt>.
 * This class is <i>not</i> thread-safe.
 *
 * <p>
 * <b>Note:</b> Time complexities are given in Big-Oh notation.
 * <tt>n</tt> represents the number of nodes, and <tt>m</tt>
 * represents the number of edges.
 * </p>
 *
 * @author Samad Lotia
 */
public class LinkedListGraph<N,E> implements Graph<N,E>, Cloneable
{
	protected final ArrayList<LinkedListNode<N,E>> nodes = new ArrayList<LinkedListNode<N,E>>();
	protected final ArrayList<LinkedListEdge<E>>   edges = new ArrayList<LinkedListEdge<E>>();
	protected int nodeCount = 0;
	protected int edgeCount = 0;
	protected double score = 0.0;
	protected int modifications = 0; // number of times the graph has been modified

	/**
	 * Construct an empty graph
	 */
	public LinkedListGraph()
	{
	}

	/**
	 * Construct a graph so that it is a copy of <code>other</code>.
	 * @param other the graph to copy
	 */
	public LinkedListGraph(final Graph<N,E> other)
	{
		this(other, new TypeConverter<N,E,N,E>()
		{
			public N convertNodeObject(N original)
			{
				return original;
			}

			public E convertEdgeObject(E original)
			{
				return original;
			}
		});
	}

	/**
	 * Copies <code>other</code> and converts its node and edge objects
	 * by using a <code>TypeConverter</code>.
	 * See <code>TypeConverter</code> for more details.
	 */
	public<NO,EO> LinkedListGraph(final Graph<NO,EO> other, TypeConverter<NO,EO,N,E> converter)
	{
		if (other == null)
			throw new IllegalArgumentException("other == null");

		final IntIntHashMap otherToThisNodeMap = new IntIntHashMap();
		
		final IntIterator otherNodes = other.nodes();
		while (otherNodes.hasNext())
		{
			final int otherNodeIndex = otherNodes.next();
			final N nodeObj = converter.convertNodeObject(other.nodeObject(otherNodeIndex));
			final int thisNodeIndex = addNode(nodeObj);
			otherToThisNodeMap.put(otherNodeIndex, thisNodeIndex);
		}

		final IntIterator otherEdges = other.edges();
		while (otherEdges.hasNext())
		{
			final int otherEdgeIndex = otherEdges.next();
			final E edgeObj = converter.convertEdgeObject(other.edgeObject(otherEdgeIndex));
			final int thisSourceIndex = otherToThisNodeMap.get(other.edgeSource(otherEdgeIndex));
			final int thisTargetIndex = otherToThisNodeMap.get(other.edgeTarget(otherEdgeIndex));
			addEdge(thisSourceIndex, thisTargetIndex, edgeObj, other.edgeType(otherEdgeIndex));
		}
	}

	/**
	 * Returns a copy of the graph. It does not copy
	 * the node or edge objects.
	 */
	public LinkedListGraph<N,E> clone()
	{
		return new LinkedListGraph<N,E>(this);
	}

	public double score()
	{
		return score;
	}

	public void setScore(double score)
	{
		this.score = score;
		modifications++;
	}

	protected final LinkedListEdge<E> edgeAtIndex(final int edgeIndex)
	{
		if (edgeIndex < 0 || edgeIndex >= edges.size())
			return null;
		return edges.get(edgeIndex);
	}

	protected final LinkedListNode<N,E> nodeAtIndex(final int nodeIndex)
	{
		if (nodeIndex < 0 || nodeIndex >= nodes.size())
			return null;
		return nodes.get(nodeIndex);
	}

	/**
	 * Time complexity: O(1)
	 */
	public boolean edgeExists(final int edgeIndex)
	{
		return (edgeAtIndex(edgeIndex) != null);
	}

	/**
	 * Time complexity: O(1)
	 */
	public boolean nodeExists(final int nodeIndex)
	{
		return (nodeAtIndex(nodeIndex) != null);
	}

	/**
	 * Time complexity: O(m)
	 */
	public int addEdge(final int sourceIndex, final int targetIndex, final E edgeObj, final byte edgeType)
	{
		final LinkedListNode<N,E> source = nodeAtIndex(sourceIndex);
		final LinkedListNode<N,E> target = nodeAtIndex(targetIndex);

		if (source == null)
			throw new InvalidIndexException("sourceIndex", sourceIndex);
		if (target == null)
			throw new InvalidIndexException("targetIndex", targetIndex);
		if ((edgeType != Graph.DIRECTED_EDGE) && (edgeType != Graph.UNDIRECTED_EDGE))
			throw new IllegalArgumentException("edgeType is not Graph.DIRECTED_EDGE and edgeType is not Graph.UNDIRECTED_EDGE");

		final int newEdgeIndex = edges.size();

		// Create the edge
		final LinkedListEdge<E> newEdge		= new LinkedListEdge<E>();
		newEdge.edgeIndex		= newEdgeIndex;
		newEdge.edgeObj			= edgeObj;
		newEdge.edgeType		= edgeType;
		newEdge.sourceIndex		= sourceIndex;
		newEdge.targetIndex		= targetIndex;

		if (edgeType == Graph.DIRECTED_EDGE)
		{
			newEdge.nextOutgoingEdge = source.firstDirectedOutgoingEdge;
			source.firstDirectedOutgoingEdge = newEdge;
			source.directedOutgoingCount++;

			newEdge.nextIncomingEdge = target.firstDirectedIncomingEdge;
			target.firstDirectedIncomingEdge = newEdge;
			target.directedIncomingCount++;
		}
		else // (edgeType == Graph.UNDIRECTED_EDGE)
		{
			newEdge.nextOutgoingEdge = source.firstUndirectedOutgoingEdge;
			source.firstUndirectedOutgoingEdge = newEdge;
			source.undirectedCount++;

			if (source != target)
			{
				newEdge.nextIncomingEdge = target.firstUndirectedIncomingEdge;
				target.firstUndirectedIncomingEdge = newEdge;
				target.undirectedCount++;
			}
		}
		
		// Add the edge to the graph
		edges.add(newEdge);
		edgeCount++;
		modifications++;

		return newEdgeIndex;
	}

	/**
	 * Time complexity: O(1)
	 */
	public int addNode(final N nodeObj)
	{
		final LinkedListNode<N,E> node	= new LinkedListNode<N,E>();
		node.nodeObj		= nodeObj;

		// Add the node to the graph
		final int nodeIndex = nodes.size();
		nodes.add(node);
		nodeCount++;
		modifications++;
		
		return nodeIndex;
	}

	protected void removeDirectedEdge(final LinkedListEdge<E> edge, final boolean updateSource, final boolean updateTarget)
	{
		if (updateSource)
		{
			final LinkedListNode<N,E> source = nodes.get(edge.sourceIndex);
			if (source.firstDirectedOutgoingEdge == edge)
				source.firstDirectedOutgoingEdge = edge.nextOutgoingEdge;
			else if (source.firstDirectedOutgoingEdge.nextOutgoingEdge == edge)
				source.firstDirectedOutgoingEdge.nextOutgoingEdge = edge.nextOutgoingEdge;
			else
			{
				LinkedListEdge<E> prevEdge = source.firstDirectedOutgoingEdge.nextOutgoingEdge;
				LinkedListEdge<E> currentEdge = prevEdge.nextOutgoingEdge;
				while (currentEdge != edge)
				{
					prevEdge = currentEdge;
					currentEdge = currentEdge.nextOutgoingEdge;
				}
				prevEdge.nextOutgoingEdge = currentEdge.nextOutgoingEdge;
			}
			source.directedOutgoingCount--;
		}

		if (updateTarget)
		{
			final LinkedListNode<N,E> target = nodes.get(edge.targetIndex);
			if (target.firstDirectedIncomingEdge == edge)
				target.firstDirectedIncomingEdge = edge.nextIncomingEdge;
			else if (target.firstDirectedIncomingEdge.nextIncomingEdge == edge)
				target.firstDirectedIncomingEdge.nextIncomingEdge = edge.nextIncomingEdge;
			else
			{
				LinkedListEdge<E> prevEdge = target.firstDirectedIncomingEdge.nextIncomingEdge;
				LinkedListEdge<E> currentEdge = prevEdge.nextIncomingEdge;
				while (currentEdge != edge)
				{
					prevEdge = currentEdge;
					currentEdge = currentEdge.nextIncomingEdge;
				}
				prevEdge.nextIncomingEdge = currentEdge.nextIncomingEdge;
			}
			target.directedIncomingCount--;
		}

		edges.set(edge.edgeIndex, null);
		edgeCount--;
	}
	
	protected void removeUndirectedEdge(final LinkedListEdge<E> edge, final boolean updateSource, final boolean updateTarget)
	{
		if (updateSource)
		{
			final LinkedListNode<N,E> source = nodes.get(edge.sourceIndex);
			if (source.firstUndirectedOutgoingEdge == edge)
				source.firstUndirectedOutgoingEdge = edge.nextOutgoingEdge;
			else if (source.firstUndirectedOutgoingEdge.nextOutgoingEdge == edge)
				source.firstUndirectedOutgoingEdge.nextOutgoingEdge = edge.nextOutgoingEdge;
			else
			{
				LinkedListEdge<E> prevEdge = source.firstUndirectedOutgoingEdge.nextOutgoingEdge;
				LinkedListEdge<E> currentEdge = prevEdge.nextOutgoingEdge;
				while (currentEdge != edge)
				{
					prevEdge = currentEdge;
					currentEdge = currentEdge.nextOutgoingEdge;
				}
				prevEdge.nextOutgoingEdge = currentEdge.nextOutgoingEdge;
			}
			source.undirectedCount--;
		}

		if (updateTarget && (edge.sourceIndex != edge.targetIndex))
		{
			final LinkedListNode<N,E> target = nodes.get(edge.targetIndex);
			if (target.firstUndirectedIncomingEdge == edge)
				target.firstUndirectedIncomingEdge = edge.nextIncomingEdge;
			else if (target.firstUndirectedIncomingEdge.nextIncomingEdge == edge)
				target.firstUndirectedIncomingEdge.nextIncomingEdge = edge.nextIncomingEdge;
			else
			{
				LinkedListEdge<E> prevEdge = target.firstUndirectedIncomingEdge.nextIncomingEdge;
				LinkedListEdge<E> currentEdge = prevEdge.nextIncomingEdge;
				while (currentEdge != edge)
				{
					prevEdge = currentEdge;
					currentEdge = currentEdge.nextIncomingEdge;
				}
				prevEdge.nextIncomingEdge = currentEdge.nextIncomingEdge;
			}
			target.undirectedCount--;
		}

		edges.set(edge.edgeIndex, null);
		edgeCount--;
	}
	
	/**
	 * Time complexity: O(m)
	 */
	public void removeEdge(final int edgeIndex)
	{	
		final LinkedListEdge<E> edge = edgeAtIndex(edgeIndex);
		if (edge == null)
			throw new InvalidIndexException("edgeIndex", edgeIndex);

		if (edge.edgeType == Graph.DIRECTED_EDGE)
			removeDirectedEdge(edge, true, true);
		else
			removeUndirectedEdge(edge, true, true);
		modifications++;
	}

	/**
	 * Time complexity: O(n+m)
	 */
	public void removeNode(final int nodeIndex)
	{
		final LinkedListNode<N,E> node = nodeAtIndex(nodeIndex);
		if (node == null)
			throw new InvalidIndexException("nodeIndex", nodeIndex);

		LinkedListEdge<E> edge = node.firstDirectedOutgoingEdge;
		while (edge != null)
		{
			removeDirectedEdge(edge, false, true);
			edge = edge.nextOutgoingEdge;
		}

		edge = node.firstDirectedIncomingEdge;
		while (edge != null)
		{
			removeDirectedEdge(edge, true, false);
			edge = edge.nextIncomingEdge;
		}
	
		edge = node.firstUndirectedOutgoingEdge;
		while (edge != null)
		{
			removeUndirectedEdge(edge, false, true);
			edge = edge.nextOutgoingEdge;
		}

		edge = node.firstUndirectedIncomingEdge;
		while (edge != null)
		{
			removeUndirectedEdge(edge, true, false);
			edge = edge.nextIncomingEdge;
		}

		// Delete the node
		nodes.set(nodeIndex, null);
		nodeCount--;
		modifications++;
	}
	
	/**
	 * Time complexity: O(1)
	 */
	public E edgeObject(final int edgeIndex)
	{
		final LinkedListEdge<E> edge = edgeAtIndex(edgeIndex);
		if (edge == null)
			throw new InvalidIndexException("edgeIndex", edgeIndex);
		return edge.edgeObj;
	}
	
	/**
	 * Time complexity: O(1)
	 */
	public N nodeObject(final int nodeIndex)
	{
		final LinkedListNode<N,E> node = nodeAtIndex(nodeIndex);
		if (node == null)
			throw new InvalidIndexException("nodeIndex", nodeIndex);
		return node.nodeObj;
	}

	/**
	 * Time complexity: O(1)
	 */
	public void setEdgeObject(final int edgeIndex, final E edgeObj)
	{
		final LinkedListEdge<E> edge = edgeAtIndex(edgeIndex);
		if (edge == null)
			throw new InvalidIndexException("edgeIndex", edgeIndex);
		edge.edgeObj = edgeObj;
		modifications++;
	}

	public void setNodeObject(final int nodeIndex, final N nodeObj)
	{
		final LinkedListNode<N,E> node = nodeAtIndex(nodeIndex);
		if (node == null)
			throw new InvalidIndexException("nodeIndex", nodeIndex);
		node.nodeObj = nodeObj;
		modifications++;
	}

	/**
	 * Returns the edge index whose edge object equals <tt>edgeObj</tt>.
	 * <p>Time complexity: O(m)</p>
	 * <p>This method is <i>slow</i>. Construct a map between edge objects
	 * and corresponding edge indices for better performance.</p>
	 */
	public int edgeIndex(final E edgeObj)
	{
		for (int i = 0; i < edges.size(); i++)
		{
			final LinkedListEdge<E> edge = edges.get(i);
			if (edge != null)
				if (edge.edgeObj.equals(edgeObj))
					return i;
		}
		return -1;
	}
	
	/**
	 * Returns the node index whose node object equals <tt>nodeObj</tt>.
	 * <p>Time complexity: O(n)</p>
	 * <p>This method is <i>slow</i>. Construct a map between node objects
	 * and corresponding node indices for better performance.</p>
	 */
	public int nodeIndex(final N nodeObj)
	{
		for (int i = 0; i < nodes.size(); i++)
		{
			final LinkedListNode<N,E> node = nodes.get(i);
			if (node != null)
				if (node.nodeObj.equals(nodeObj))
					return i;
		}
		return -1;
	}

	public int maxNodeIndex()
	{
		return nodes.size() - 1;
	}

	public IntIterator nodes()
	{
		return new IntIterator()
		{
			int i = 0;
			int remaining = nodeCount;
			final int expectedModifications = modifications;
			
			public boolean hasNext()
			{
				return (remaining > 0);
			}

			public int next()
			{
				if (remaining <= 0)
					throw new NoSuchElementException();
				if (modifications != expectedModifications)
					throw new ConcurrentModificationException();
				
				while (nodes.get(i) == null)
					i++;

				remaining--;
				return i++;
			}

			public int numRemaining()
			{
				return remaining;
			}
		};
	}

	public IntIterator edges()
	{
		return new IntIterator()
		{
			private int i = 0;
			private int remaining = edgeCount;
			final int expectedModifications = modifications;
			
			public boolean hasNext()
			{
				return (remaining > 0);
			}

			public int next()
			{
				if (remaining <= 0)
					throw new NoSuchElementException();
				if (modifications != expectedModifications)
					throw new ConcurrentModificationException();

				while (edges.get(i) == null)
					i++;

				remaining--;
				return i++;
			}

			public int numRemaining()
			{
				return remaining;
			}
		};
	}

	public int nodeCount()
	{
		return nodeCount;
	}

	public int edgeCount()
	{
		return edgeCount;
	}

	public int edgeSource(final int edgeIndex)
	{
		final LinkedListEdge<E> edge = edgeAtIndex(edgeIndex);
		if (edge == null)
			throw new InvalidIndexException("edgeIndex", edgeIndex);
		return edge.sourceIndex;
	}

	public int edgeTarget(final int edgeIndex)
	{
		final LinkedListEdge<E> edge = edgeAtIndex(edgeIndex);
		if (edge == null)
			throw new InvalidIndexException("edgeIndex", edgeIndex);
		return edge.targetIndex;
	}

	public byte edgeType(final int edgeIndex)
	{
		final LinkedListEdge<E> edge = edgeAtIndex(edgeIndex);
		if (edge == null)
			throw new InvalidIndexException("edgeIndex", edgeIndex);
		return edge.edgeType;
	}

	/**
	 * Returns <tt>OUTGOING_EDGE | UNDIRECTED_EDGE</tt>
	 */
	public byte defaultEdgeType()
	{
		return OUTGOING_EDGE | UNDIRECTED_EDGE;
	}

	public int degree(final int nodeIndex)
	{
		return degree(nodeIndex, defaultEdgeType());
	}

	public int degree(final int nodeIndex, final byte edgeType)
	{
		final LinkedListNode<N,E> node = nodeAtIndex(nodeIndex);
		if (node == null)
			throw new InvalidIndexException("nodeIndex", nodeIndex);

		int degree = 0;
		if ((edgeType & Graph.INCOMING_EDGE) != 0)
			degree += node.directedIncomingCount;
		if ((edgeType & Graph.OUTGOING_EDGE) != 0)
			degree += node.directedOutgoingCount;
		if ((edgeType & Graph.UNDIRECTED_EDGE) != 0)
			degree += node.undirectedCount;
		return degree;
	}

	/**
	 * Returns adjacent nodes connected by the default edge type.
	 * <p>The iterator is <i>fail-fast</i>, as it will throw
	 * <code>ConcurrentModificationException</code> when
	 * <code>next()</code> is called if the graph was modified.</p>
	 *
	 * @throws ConcurrentModificationException if the graph was modified
	 *         during the iteration.
	 * @throws NoSuchElementException if <code>next()</code> was called
	 *         if <code>hasNext()</code> is returning false.
	 *
	 * <p>Note: The iterator returned may return the same node
	 * more than once if there are multiple edges connecting
	 * <code>nodeIndex</code> to the another node.</p>
	 */
	public IntIterator adjacentNodes(final int nodeIndex)
	{
		return adjacentNodes(nodeIndex, defaultEdgeType());
	}

	/**
	 * Returns adjacent nodes.
	 * <p>The iterator is <i>fail-fast</i>, as it will throw
	 * <code>ConcurrentModificationException</code> when
	 * <code>next()</code> is called if the graph was modified.</p>
	 *
	 * @throws ConcurrentModificationException if the graph was modified
	 *         during the iteration.
	 * @throws NoSuchElementException if <code>next()</code> was called
	 *         if <code>hasNext()</code> is returning false.
	 *
	 * <p>Note: The iterator returned may return the same node
	 * more than once if there are multiple edges connecting
	 * <code>nodeIndex</code> to the another node.</p>
	 */
	public IntIterator adjacentNodes(final int nodeIndex, final byte edgeType)
	{
		return new AdjNodesIterator(nodeIndex, edgeType);
	}
	
	protected class AdjNodesIterator extends BasicEdgesIterator implements IntIterator
	{
		private final int nodeIndex;
		public AdjNodesIterator(final int nodeIndex, final byte edgeType)
		{
			super(nodeIndex, edgeType);
			this.nodeIndex = nodeIndex;
		}

		public int next()
		{
			LinkedListEdge<E> edge = nextEdge();
			return nodeIndex ^ edge.sourceIndex ^ edge.targetIndex;
		}
	}

	/**
	 * Returns adjacent edges connected by the default edge type.
	 * <p>The iterator is <i>fail-fast</i>, as it will throw
	 * <code>ConcurrentModificationException</code> when
	 * <code>next()</code> is called if the graph was modified.</p>
	 *
	 * @throws ConcurrentModificationException if the graph was modified
	 *         during the iteration.
	 * @throws NoSuchElementException if <code>next()</code> was called
	 *         if <code>hasNext()</code> is returning false.
	 */
	public IntIterator adjacentEdges(final int nodeIndex)
	{
		return adjacentEdges(nodeIndex, defaultEdgeType());
	}

	/**
	 * Returns adjacent edges.
	 * <p>The iterator is <i>fail-fast</i>, as it will throw
	 * <code>ConcurrentModificationException</code> when
	 * <code>next()</code> is called if the graph was modified.</p>
	 *
	 * @throws ConcurrentModificationException if the graph was modified
	 *         during the iteration.
	 * @throws NoSuchElementException if <code>next()</code> was called
	 *         if <code>hasNext()</code> is returning false.
	 */
	public IntIterator adjacentEdges(final int nodeIndex, final byte edgeType)
	{
		return new AdjEdgesIterator(nodeIndex, edgeType);
	}

	protected class AdjEdgesIterator extends BasicEdgesIterator implements IntIterator
	{
		public AdjEdgesIterator(final int nodeIndex, final byte edgeType)
		{
			super(nodeIndex, edgeType);
		}

		public int next()
		{
			return nextEdge().edgeIndex;
		}
	}

	protected abstract class BasicEdgesIterator
	{
		protected boolean doDirIncoming;
		protected boolean doDirOutgoing;
		protected boolean doUndIncoming;
		protected boolean doUndOutgoing;
		protected int remaining;
		protected LinkedListEdge<E> edge;
		protected final LinkedListNode<N,E> node;
		protected final int expectedModifications = modifications;

		public BasicEdgesIterator(final int nodeIndex, final byte edgeType)
		{
			node = nodeAtIndex(nodeIndex);
			if (node == null)
				throw new InvalidIndexException("nodeIndex", nodeIndex);

			doDirIncoming = ((edgeType & Graph.INCOMING_EDGE) != 0) &&
					(node.firstDirectedIncomingEdge != null);

			doDirOutgoing = ((edgeType & Graph.OUTGOING_EDGE) != 0) &&
					(node.firstDirectedOutgoingEdge != null);

			doUndIncoming = ((edgeType & Graph.UNDIRECTED_EDGE) != 0) &&
					(node.firstUndirectedIncomingEdge != null);

			doUndOutgoing = ((edgeType & Graph.UNDIRECTED_EDGE) != 0) &&
					(node.firstUndirectedOutgoingEdge != null);

			remaining = (doDirIncoming ? node.directedIncomingCount : 0) +
					(doDirOutgoing ? node.directedOutgoingCount : 0) +
					((edgeType & Graph.UNDIRECTED_EDGE) != 0 ? node.undirectedCount : 0);

			edge =  (doDirIncoming ? node.firstDirectedIncomingEdge :
				(doDirOutgoing ? node.firstDirectedOutgoingEdge :
				(doUndIncoming ? node.firstUndirectedIncomingEdge :
				(doUndOutgoing ? node.firstUndirectedOutgoingEdge : null))));
		}

		public boolean hasNext()
		{
			return (remaining != 0);
		}

		public LinkedListEdge<E> nextEdge()
		{
			if (modifications != expectedModifications)
				throw new ConcurrentModificationException();
			if (doDirIncoming)
			{
				if (edge != null)
				{
					final LinkedListEdge<E> result = edge;
					edge = edge.nextIncomingEdge;
					remaining--;
					return result;
				}
				else
				{
					doDirIncoming = false;
					if (doDirOutgoing)
						edge = node.firstDirectedOutgoingEdge;
					else if (doUndIncoming)
						edge = node.firstUndirectedIncomingEdge;
					else if (doUndOutgoing)
						edge = node.firstUndirectedOutgoingEdge;
				}
			}

			if (doDirOutgoing)
			{
				if (edge != null)
				{
					final LinkedListEdge<E> result = edge;
					edge = edge.nextOutgoingEdge;
					remaining--;
					return result;
				}
				else
				{
					doDirOutgoing = false;
					if (doUndIncoming)
						edge = node.firstUndirectedIncomingEdge;
					else if (doUndOutgoing)
						edge = node.firstUndirectedOutgoingEdge;
				}
			}

			if (doUndIncoming)
			{
				while ((edge != null) && (edge.sourceIndex == edge.targetIndex))
					edge = edge.nextOutgoingEdge;

				if (edge != null)
				{
					final LinkedListEdge<E> result = edge;
					edge = edge.nextIncomingEdge;
					remaining--;
					return result;
				}
				else
				{
					doUndIncoming = false;
					if (doUndOutgoing)
						edge = node.firstUndirectedOutgoingEdge;
				}
			}

			if (doUndOutgoing)
			{
				if (edge != null)
				{
					final LinkedListEdge<E> result = edge;
					edge = edge.nextOutgoingEdge;
					remaining--;
					return result;
				}
			}
			throw new NoSuchElementException();
		}

		public int numRemaining()
		{
			return remaining;
		}
	}


	public int firstEdge(final int sourceIndex, final int targetIndex)
	{
		return firstEdge(sourceIndex, targetIndex, defaultEdgeType());
	}

	/**
	 * Time complexity: O(m)
	 */
	public int firstEdge(final int sourceIndex, final int targetIndex, final byte edgeType)
	{
		final LinkedListNode<N,E> node = nodeAtIndex(sourceIndex);
		if (node == null)
			throw new InvalidIndexException("sourceIndex", sourceIndex);

		if ((edgeType & Graph.INCOMING_EDGE) != 0)
		{
			LinkedListEdge<E> edge = node.firstDirectedIncomingEdge;
			while (edge != null)
			{
				final int neighbor = sourceIndex ^ edge.sourceIndex ^ edge.targetIndex;
				if (neighbor == targetIndex)
					return edge.edgeIndex;
				edge = edge.nextIncomingEdge;
			}
		}
		
		if ((edgeType & Graph.OUTGOING_EDGE) != 0)
		{
			LinkedListEdge<E> edge = node.firstDirectedOutgoingEdge;
			while (edge != null)
			{
				final int neighbor = sourceIndex ^ edge.sourceIndex ^ edge.targetIndex;
				if (neighbor == targetIndex)
					return edge.edgeIndex;
				edge = edge.nextOutgoingEdge;
			}
		}
		
		if ((edgeType & Graph.UNDIRECTED_EDGE) != 0)
		{
			LinkedListEdge<E> edge = node.firstDirectedIncomingEdge;
			while (edge != null)
			{
				final int neighbor = sourceIndex ^ edge.sourceIndex ^ edge.targetIndex;
				if (neighbor == targetIndex)
					return edge.edgeIndex;
				edge = edge.nextIncomingEdge;
			}
			
			edge = node.firstDirectedOutgoingEdge;
			while (edge != null)
			{
				final int neighbor = sourceIndex ^ edge.sourceIndex ^ edge.targetIndex;
				if (neighbor == targetIndex)
					return edge.edgeIndex;
				edge = edge.nextOutgoingEdge;
			}
		}
		
		return Graph.INVALID_INDEX;
	}

	public int compareTo(final Graph<N,E> other)
	{
		final int nodeCountDelta = nodeCount - other.nodeCount();
		if (nodeCountDelta != 0)
			return nodeCountDelta;

		final int edgeCountDelta = edgeCount - other.edgeCount();
		if (edgeCountDelta != 0)
			return edgeCountDelta;

		final int scoreCmp = Double.compare(score, other.score());
		if (scoreCmp != 0)
			return scoreCmp;

		return hashCode() - other.hashCode();
	}

	public void dump()
	{
		System.out.println("==============================================");
		System.out.println("Nodes:");
		for (int i = 0; i < nodes.size(); i++)
		{
			System.out.print(i + ": ");
			LinkedListNode<N,E> node = nodes.get(i);
			if (node == null)
			{
				System.out.println("null");
				continue;
			}
			System.out.println(node.nodeObj.toString());
			System.out.println("\tdirectedCount: in: " + node.directedIncomingCount + ", out: " + node.directedOutgoingCount);
			System.out.println("\tundirectedCount: " + node.undirectedCount);
			System.out.print("\t1st directed: in: ");
			System.out.print(node.firstDirectedIncomingEdge == null ? "-1" : node.firstDirectedIncomingEdge.edgeIndex);
			System.out.print(" out: ");
			System.out.print(node.firstDirectedOutgoingEdge == null ? "-1" : node.firstDirectedOutgoingEdge.edgeIndex);
			System.out.println();
			System.out.print("\t1st undirected: in: ");
			System.out.print(node.firstUndirectedIncomingEdge == null ? "-1" : node.firstUndirectedIncomingEdge.edgeIndex);
			System.out.print(" out: ");
			System.out.print(node.firstUndirectedOutgoingEdge == null ? "-1" : node.firstUndirectedOutgoingEdge.edgeIndex);
			System.out.println();
		}
		System.out.println("==============================================");
		System.out.println("Edges:");
		for (int i = 0; i < edges.size(); i++)
		{
			System.out.print(i + ": ");
			LinkedListEdge<E> edge = edges.get(i);
			if (edge == null)
			{
				System.out.println("null");
				continue;
			}
			System.out.println(edge.edgeObj.toString());
			System.out.println("\tsource: " + edge.sourceIndex);
			System.out.println("\ttarget: " + edge.targetIndex);
			System.out.println("\ttype: " + edge.edgeType);
			System.out.print("\tnext: in: ");
			System.out.print(edge.nextIncomingEdge == null ? "-1" : edge.nextIncomingEdge.edgeIndex);
			System.out.print(" out: ");
			System.out.print(edge.nextOutgoingEdge == null ? "-1" : edge.nextOutgoingEdge.edgeIndex);
			System.out.println();
		}
		System.out.println("==============================================");
	}
}
