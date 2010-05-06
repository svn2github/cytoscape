package org.idekerlab.PanGIAPlugin.ModFinder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.idekerlab.PanGIAPlugin.networks.linkedNetworks.Finalish;

/***
 * An edge object for use with ONetworks to track statistics for the HCNetsearch algorithm.
 */
public class BFEdge implements Finalish{

	private float link; //Current link score
	private float complexMerge; //The new complex score
	private float linkMerge; //The sum of all new link scores
	private float global; //The global score change
		
	//Set of interaction types (ie. physical, genetic, etc.)
	private Set<InteractionType> types = new HashSet<InteractionType>(3,1);
	
	public enum InteractionType {Physical, Genetic}
	
	public BFEdge()
	{
	}
	
	/**
	 * Copy constructor.
	 */
	public BFEdge(BFEdge edge)
	{
		complexMerge = edge.complexMerge;
		linkMerge = edge.linkMerge;
		global = edge.global;
		link = edge.link;
		types = edge.types;
	}
	
	/**
	 * Preferred constructor. Initializes the linkscores object for physical interactions. 
	 * @param type
	 */
	public BFEdge(InteractionType type)
	{
		this.types.add(type);
		//if (type.equals(InteractionType.Physical)) linkscores = new HashMap<TypedLinkNode<TypedLinkNodeModule<String,BFEdge>,BFEdge>,Float>(30);
	}
	
	public float complexMerge()
	{
		return complexMerge;
	}
	
	public float linkMerge()
	{
		return linkMerge;
	}
	
	public float global()
	{
		return global;
	}
	
	public float link()
	{
		return link;
	}
	
	public boolean isType(InteractionType type)
	{
		return types.contains(type);
	}
	
	public void setComplexMerge(float merge)
	{
		this.complexMerge = merge;
	}
	
	public void setLinkMerge(float merge)
	{
		this.linkMerge = merge;
	}
		
	public void setGlobal(float global)
	{
		this.global = global;
	}
	
	public void setLink(float link)
	{
		this.link = link;
	}
	
	/**
	 * Adds a given interaction type.
	 * If the edge newly becomes physical, this initializes the linkscores object.
	 */
	public void addType(InteractionType type)
	{
		this.types.add(type);
	}
	
	/**
	 * Adds a set of interaction types.
	 * If the edge newly becomes physical, this initializes the linkscores object.
	 */
	public void addType(Set<InteractionType> types)
	{
		this.types.addAll(types);
	}
	
	public Set<InteractionType> getTypes()
	{
		return types;
	}
	
	public String toString()
	{
		if (this.types.size()==0) return "";
		
		String out = "";
		
		Iterator<InteractionType> iti = types.iterator();
		out += iti.next().toString();
		
		while (iti.hasNext()) out += ","+iti.next().toString();
		
		
		return out;
	}
	
	public boolean equals(Object other)
	{
		return this==other;
	}
}
