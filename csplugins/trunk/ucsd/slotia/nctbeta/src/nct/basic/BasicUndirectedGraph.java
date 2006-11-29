package nct.basic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.NoSuchElementException;

import nct.UndirectedGraph;
import nct.basic.UndirectedNode;
import nct.util.IntIterator;

public class BasicUndirectedGraph<N,E> implements UndirectedGraph<N,E>
{
	protected ArrayList<UndirectedNode<N>> nodes;
	protected ArrayList<UndirectedEdge<E>> edges;

	protected int nodeCount;
	protected int edgeCount;

	public BasicUndirectedGraph()
	{
		nodes = new ArrayList<UndirectedNode<N>>();
		edges = new ArrayList<UndirectedEdge<E>>();

		nodeCount = 0;
		edgeCount = 0;
	}

	public boolean edgeExists(int edgeIndex)
	{
		if (edgeIndex < 0 || edgeIndex >= edges.size())
			return false;

		return (edges.get(edgeIndex) != null);
	}

	public boolean nodeExists(int nodeIndex)
	{
		if (nodeIndex < 0 || nodeIndex >= nodes.size())
			return false;

		return (nodes.get(nodeIndex) != null);
	}

	public E edgeObject(int edgeIndex)
	{
		if (edgeIndex < 0 || edgeIndex >= edges.size())
			return null;

		UndirectedEdge<E> edge = edges.get(edgeIndex);
		if (edge == null)
			return null;
		else
			return edge.edgeObj;
	}

	public N nodeObject(int nodeIndex)
	{
		if (nodeIndex < 0 || nodeIndex >= nodes.size())
			return null;

		UndirectedNode<N> node = nodes.get(nodeIndex);
		if (node == null)
			return null;
		else
			return node.nodeObj;
	}

	public Set<Integer> nodeSet()
	{
		Set<Integer> set = new HashSet<Integer>();
		for (int i = 0; i < nodes.size(); i++)
			if (nodes.get(i) != null)
				set.add(new Integer(i));

		return set;
	}

	public Set<Integer> edgeSet()
	{
		Set<Integer> set = new HashSet<Integer>();
		for (int i = 0; i < edges.size(); i++)
			if (edges.get(i) != null)
				set.add(new Integer(i));

		return set;
	}

	public Iterator<Integer> nodeIterator()
	{
		return new Iterator<Integer>()
		{
			private int i = 0;
			private int remaining = nodeCount;
			
			public boolean hasNext()
			{
				return (remaining > 0);
			}

			public Integer next()
			{
				if (remaining <= 0)
					throw new NoSuchElementException();

				while (nodes.get(i) == null)
					i++;

				remaining--;
				return new Integer(i++);
			}

			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}

	public Iterator<Integer> edgeIterator()
	{
		return new Iterator<Integer>()
		{
			private int i = 0;
			private int remaining = edgeCount;
			
			public boolean hasNext()
			{
				return (remaining > 0);
			}

			public Integer next()
			{
				if (remaining <= 0)
					throw new NoSuchElementException();

				while (edges.get(i) == null)
					i++;

				remaining--;
				return new Integer(i++);
			}

			public void remove()
			{
				throw new UnsupportedOperationException();
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
	
	public int degree(int nodeIndex)
	{
		if (!this.nodeExists(nodeIndex))
			return Integer.MIN_VALUE;

		return nodes.get(nodeIndex).adjacentEdges.size();
	}

	public Iterator<Integer> neighborsIterator(int nodeIndex)
	{
		if (!this.nodeExists(nodeIndex))
			return null;

		return nodes.get(nodeIndex).adjacentNodes.iterator();
	}

	public Iterator<Integer> connectingEdgesIterator(int nodeIndex)
	{
		if (!this.nodeExists(nodeIndex))
			return null;

		return nodes.get(nodeIndex).adjacentEdges.iterator();
	}

	public void diag()
	{
		System.out.println("Nodes (" + nodes.size() + ")");
		for (int i = 0; i < nodes.size(); i++)
		{
			System.out.print("nodes[" + i + "] = ");
			if (nodes.get(i) == null)
				System.out.println("null");
			else
			{
				System.out.print("adjNodes: { ");
				IntIterator adjNodesIterator = nodes.get(i).adjacentNodes.intIterator();
				while (adjNodesIterator.hasNext())
					System.out.print(adjNodesIterator.next() + " ");
				System.out.print("} adjEdges: { ");
				for (int n = 0; n < nodes.get(i).adjacentEdges.size(); n++)
					System.out.print(nodes.get(i).adjacentEdges.get(n) + " ");
				System.out.println("} nodeObj = \"" + nodes.get(i).nodeObj.toString() + "\"");
			}
		}

		System.out.println("Edges (" + edges.size() + ")");
		for (int i = 0; i < edges.size(); i++)
		{
			System.out.print("edges[" + i + "] = ");
			if (edges.get(i) == null)
				System.out.println("null");
			else
				System.out.println("(" + edges.get(i).sourceIndex + ", " + edges.get(i).targetIndex + ") nodeObj = \"" + edges.get(i).edgeObj.toString() + "\"");
		}
	}
}
