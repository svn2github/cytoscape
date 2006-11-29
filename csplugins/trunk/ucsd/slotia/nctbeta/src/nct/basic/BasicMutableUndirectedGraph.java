package nct.basic;

import java.util.Iterator;

import nct.UndirectedGraph;
import nct.MutableGraph;
import nct.basic.BasicUndirectedGraph;
import nct.util.IntArray;
import nct.util.IntSet;
import nct.util.IntIterator;

public class BasicMutableUndirectedGraph<N,E>
		extends BasicUndirectedGraph<N,E>
		implements UndirectedGraph<N,E>, MutableGraph<N,E>
{
	public BasicMutableUndirectedGraph()
	{
		super();
	}

	public int addEdge(int sourceIndex, int targetIndex, E edgeObj)
	{
		if (!super.nodeExists(sourceIndex) || !super.nodeExists(targetIndex))
			return Integer.MIN_VALUE;

		int edgeIndex = super.edges.size();

		UndirectedEdge<E> edge = new UndirectedEdge<E>();
		edge.sourceIndex = sourceIndex;
		edge.targetIndex = targetIndex;
		edge.edgeObj = edgeObj;
		super.edges.add(edge);

		UndirectedNode<N> source = nodes.get(sourceIndex);
		source.adjacentNodes.add(targetIndex);
		source.adjacentEdges.add(edgeIndex);

		if (sourceIndex != targetIndex)
		{
			UndirectedNode<N> target = nodes.get(targetIndex);
			target.adjacentNodes.add(sourceIndex);
			target.adjacentEdges.add(edgeIndex);
		}

		super.edgeCount++;
		return edgeIndex;
	}

	public int addNode(N nodeObj)
	{
		int nodeIndex = super.nodes.size();
		UndirectedNode<N> node = new UndirectedNode<N>();
		node.nodeObj = nodeObj;
		node.adjacentNodes = new IntSet();
		node.adjacentEdges = new IntArray();
		super.nodes.add(node);
		super.nodeCount++;
		return nodeIndex;
	}

	public boolean removeEdge(int edgeIndex)
	{
		if (!super.edgeExists(edgeIndex))
			return false;
		
		UndirectedEdge<E> edge = super.edges.get(edgeIndex);
		UndirectedNode<N> source = super.nodes.get(edge.sourceIndex);
		UndirectedNode<N> target = super.nodes.get(edge.targetIndex);
		
		source.adjacentEdges.remove(source.adjacentEdges.indexOf(edgeIndex));
		if (edge.sourceIndex != edge.targetIndex)
			target.adjacentEdges.remove(target.adjacentEdges.indexOf(edgeIndex));
		
		if (!isMultipleEdge(edgeIndex))
		{
			source.adjacentNodes.remove(edge.targetIndex);
			if (edge.sourceIndex != edge.targetIndex)
				target.adjacentNodes.remove(edge.sourceIndex);
		}
		
		super.edges.set(edgeIndex, null);
		
		super.edgeCount--;
		return true;
	}

	public boolean removeNode(int nodeIndex)
	{
		if (!super.nodeExists(nodeIndex))
			return false;

		UndirectedNode<N> node = super.nodes.get(nodeIndex);

		for (int i = 0; i < node.adjacentEdges.size(); i++)
		{
			int edgeIndex = node.adjacentEdges.get(i);
			UndirectedEdge<E> edge = super.edges.get(edgeIndex);
			
			int neighborIndex = nodeIndex ^ edge.sourceIndex ^ edge.targetIndex;
			UndirectedNode<N> neighbor = super.nodes.get(neighborIndex);
			neighbor.adjacentNodes.remove(nodeIndex);

			neighbor.adjacentEdges.remove(neighbor.adjacentEdges.indexOf(edgeIndex));
			super.edges.set(edgeIndex, null);
		}
		
		super.nodes.set(nodeIndex, null);
		
		super.nodeCount--;
		return true;
	}

	public boolean setEdgeObject(int edgeIndex, E edgeObj)
	{
		if (!super.edgeExists(edgeIndex))
			return false;
		
		super.edges.get(edgeIndex).edgeObj = edgeObj;
		return true;
	}

	public boolean setNodeObject(int nodeIndex, N nodeObj)
	{
		if (!super.nodeExists(nodeIndex))
			return false;
		
		super.nodes.get(nodeIndex).nodeObj = nodeObj;
		return true;
	}

	private boolean isMultipleEdge(int edgeIndex)
	{
		int sourceIndex = edges.get(edgeIndex).sourceIndex;
		int targetIndex = edges.get(edgeIndex).targetIndex;

		for (int i = 0; i < super.edges.size(); i++)
		{
			UndirectedEdge<E> edge = super.edges.get(i);
			if (edge != null && i != edgeIndex)
				if (((edge.sourceIndex == sourceIndex) && (edge.targetIndex == targetIndex)) ||
				    ((edge.sourceIndex == targetIndex) && (edge.targetIndex == sourceIndex)))
					return true;
		}
		return false;
	}
}
