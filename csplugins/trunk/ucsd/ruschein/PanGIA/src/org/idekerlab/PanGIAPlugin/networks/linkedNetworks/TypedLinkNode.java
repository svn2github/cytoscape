package org.idekerlab.PanGIAPlugin.networks.linkedNetworks;

import java.util.*;

import org.idekerlab.PanGIAPlugin.utilities.IIterator;

public class TypedLinkNode<NT,ET> implements Finalish
{
	private final int hc;
	private final NT value;
	private Map<TypedLinkNode<NT,ET>,TypedLinkEdge<NT,ET>> neighbors;
	
	public TypedLinkNode(NT value)
	{
		this.value = value;
		neighbors = new HashMap<TypedLinkNode<NT,ET>,TypedLinkEdge<NT,ET>>();
		this.hc = value.hashCode();
	}
	
	public TypedLinkNode(NT value, int numNeighbors)
	{
		this.value = value;
		neighbors = new HashMap<TypedLinkNode<NT,ET>,TypedLinkEdge<NT,ET>>(numNeighbors);
		this.hc = value.hashCode();
	}
	
	public int hashCode()
	{
		return hc;
	}
	
	public boolean equals(Object o)
	{
		if (o instanceof TypedLinkNode)
		{
			return this.value.equals(((TypedLinkNode<?,?>)o).value);
		}else return false;
	}
	
	public String toString()
	{
		return value.toString();
	}
	
	public IIterator<TypedLinkEdge<NT,ET>> edgeIterator()
	{
		return new IIterator<TypedLinkEdge<NT,ET>>(neighbors.values().iterator());
	}
	
	public IIterator<TypedLinkNode<NT,ET>> nodeIterator()
	{
		return new IIterator<TypedLinkNode<NT,ET>>(neighbors.keySet().iterator());
	}
	
	public NT value()
	{
		return this.value;
	}
	
	public boolean isConnected(TypedLinkNode<NT,?> node)
	{
		for (TypedLinkEdge<NT,?> edge : this.edgeIterator())
			if (edge.source().equals(node) || edge.target().equals(node)) return true;
		
		return false;
	}
	
	public TypedLinkEdge<NT,ET> getEdge(NT node)
	{
		return neighbors.get(new TypedLinkNode<NT,ET>(node));
	}
	
	public Set<TypedLinkEdge<NT,ET>> edges()
	{
		return new HashSet<TypedLinkEdge<NT,ET>>(neighbors.values());
	}
	
	public Set<TypedLinkNode<NT,ET>> neighbors()
	{
		return new HashSet<TypedLinkNode<NT,ET>>(neighbors.keySet());
	}
	
	public TypedLinkNodeModule<NT,ET> neighbors(int degree)
	{
		return new TypedLinkNodeModule<NT,ET>(this).neighbors(degree);
	}
	
	public void addNeighbor(TypedLinkNode<NT,ET> node, TypedLinkEdge<NT,ET> edge)
	{
		this.neighbors.put(node, edge);
	}
	
	public void removeNeighbor(TypedLinkNode<NT,ET> node)
	{
		this.neighbors.remove(node);
	}
	
	public int numNeighbors()
	{
		return neighbors.size();
	}
}
