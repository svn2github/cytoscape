package org.idekerlab.PanGIAPlugin.networks.linkedNetworks;

import java.util.*;

public final class TypedLinkEdge<NT,ET> implements Finalish
{
	private final TypedLinkNode<NT,ET> source;
	private final TypedLinkNode<NT,ET> target;
	
	private final ET value;
	private final int hc;
	
	private final boolean directed;
	
	public TypedLinkEdge(TypedLinkNode<NT,ET> source, TypedLinkNode<NT,ET> target, ET value, boolean directed)
	{
		this.source = source;
		this.target = target;
		this.value = value;
		this.directed = directed;
		this.hc = source.hashCode()+target.hashCode();
	}
	
	public int hashCode()
	{
		return hc;
	}
	
	public boolean equals(Object o)
	{
		if (o instanceof TypedLinkEdge)
		{
			TypedLinkEdge<?,?> other = (TypedLinkEdge<?,?>)o;
			return (other.value.equals(this.value) && (other.source.equals(this.source) && other.target.equals(this.target) || (!directed && other.source.equals(this.target) && other.target.equals(this.source))));
		}else return false;
	}
	
	public TypedLinkNode<NT,ET> source()
	{
		return source;
	}

	public TypedLinkNode<NT,ET> target()
	{
		return target;
	}
	
	public ET value()
	{
		return value;
	}
	
	public Set<TypedLinkEdge<NT,ET>> edgeNeighbors()
	{
		Set<TypedLinkEdge<NT,ET>> en = this.source.edges();
		en.addAll(this.target.edges());
		en.remove(this);
		
		return en;
	}
	
	public String toString()
	{
		return source.toString()+"-"+target.toString();
	}
	
	public TypedLinkNode<NT,ET> opposite(TypedLinkNode<NT,ET> node)
	{
		if (this.source.equals(node)) return this.target;
		if (this.target.equals(node)) return this.source;
		return null;
	}
}
