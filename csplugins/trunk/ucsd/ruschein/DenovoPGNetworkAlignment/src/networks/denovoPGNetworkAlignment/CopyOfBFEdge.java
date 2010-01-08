package networks.denovoPGNetworkAlignment;

import java.util.*;

import networks.linkedNetworks.*;

/***
 * An edge object for use with ONetworks to track statistics for the HCNetsearch algorithm.
 */
public class CopyOfBFEdge implements Finalish{

	private float link; //Current link score
	
	//Hypothetical merge scores
	
	//Set of new link scores. Each linkscore is a Map<Int,Float>, where the Int
	//refers to a node and the float is the link score connecting the new module
	//to that node.
	private Map<TypedLinkNode<TypedLinkNodeModule<String,CopyOfBFEdge>,CopyOfBFEdge>,Float> linkscores; 
	
	private float complexMerge; //The new complex score
	private float linkMerge; //The sum of all new link scores
	private float global; //The global score change
		
	//Set of interaction types (ie. physical, genetic, etc.)
	private Set<InteractionType> types = new HashSet<InteractionType>(3,1);
	
	public enum InteractionType {Physical, Genetic}
	
	public CopyOfBFEdge()
	{
	}
	
	/**
	 * Copy constructor.
	 */
	public CopyOfBFEdge(CopyOfBFEdge edge)
	{
		complexMerge = edge.complexMerge;
		linkMerge = edge.linkMerge;
		global = edge.global;
		link = edge.link;
		linkscores = edge.linkscores;
		types = edge.types;
	}
	
	/**
	 * Preferred constructor. Initializes the linkscores object for physical interactions. 
	 * @param type
	 */
	public CopyOfBFEdge(InteractionType type)
	{
		this.types.add(type);
		if (type.equals(InteractionType.Physical)) linkscores = new HashMap<TypedLinkNode<TypedLinkNodeModule<String,CopyOfBFEdge>,CopyOfBFEdge>,Float>(30);
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
		if (!this.isType(InteractionType.Physical))
		{
			this.types.add(type);
			if (this.isType(InteractionType.Physical)) linkscores = new HashMap<TypedLinkNode<TypedLinkNodeModule<String,CopyOfBFEdge>,CopyOfBFEdge>,Float>(30);
		}
		else this.types.add(type);
		
	}
	
	/**
	 * Adds a set of interaction types.
	 * If the edge newly becomes physical, this initializes the linkscores object.
	 */
	public void addType(Set<InteractionType> types)
	{
		if (!this.isType(InteractionType.Physical))
		{
			this.types.addAll(types);
			if (this.isType(InteractionType.Physical)) linkscores = new HashMap<TypedLinkNode<TypedLinkNodeModule<String,CopyOfBFEdge>,CopyOfBFEdge>,Float>(30);
		}
		else this.types.addAll(types);
	}
	
	public void addLinkScore(TypedLinkNode<TypedLinkNodeModule<String,CopyOfBFEdge>,CopyOfBFEdge> node, float linkscore)
	{
		linkscores.put(node, linkscore);
	}
	
	public void removeLinkScore(TypedLinkNode<TypedLinkNodeModule<String,CopyOfBFEdge>,CopyOfBFEdge> node)
	{
		linkscores.remove(node);
	}
	
	public float getLinkScore(TypedLinkNode<TypedLinkNodeModule<String,CopyOfBFEdge>,CopyOfBFEdge> node)
	{
		return linkscores.get(node);
	}
	
	public Set<InteractionType> getTypes()
	{
		return types;
	}
	
	public boolean hasLinkScore(TypedLinkNode<TypedLinkNodeModule<String,CopyOfBFEdge>,CopyOfBFEdge> node)
	{
		if (linkscores==null) return false;
		return linkscores.containsKey(node);
	}
	
	public float sumPositiveLinkScores()
	{
		float sum = 0;
		for (Float f : this.linkscores.values())
			if (f>0) sum+=f;
		
		return sum;
	}
	
	public float sumLinkScores()
	{
		float sum = 0;
		for (Float f : this.linkscores.values())
			sum+=f;
		
		return sum;
	}
	
	public void clearLinkScores()
	{
		if (linkscores!=null) linkscores = new HashMap<TypedLinkNode<TypedLinkNodeModule<String,CopyOfBFEdge>,CopyOfBFEdge>,Float>(linkscores.size());
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
	
	public static Map<TypedLinkNode<TypedLinkNodeModule<String,CopyOfBFEdge>,CopyOfBFEdge>,Float> mergeLinkScores(CopyOfBFEdge e1, CopyOfBFEdge e2)
	{
		Map<TypedLinkNode<TypedLinkNodeModule<String,CopyOfBFEdge>,CopyOfBFEdge>,Float> out = new HashMap<TypedLinkNode<TypedLinkNodeModule<String,CopyOfBFEdge>,CopyOfBFEdge>,Float>(100);
		
		for (TypedLinkNode<TypedLinkNodeModule<String,CopyOfBFEdge>,CopyOfBFEdge> node : e1.linkscores.keySet())
		{
			Float f1 = e1.linkscores.get(node);
			Float f2 = e2.linkscores.get(node);
			
			
			if (f2==null) out.put(node, f1);
			else out.put(node, f1+f2);
		}
		
		for (TypedLinkNode<TypedLinkNodeModule<String,CopyOfBFEdge>,CopyOfBFEdge> node : e2.linkscores.keySet())
		{
			Float f1 = e1.linkscores.get(node);
			if (f1==null) out.put(node, e2.linkscores.get(node));
		}
			
		return out;
	}
	
	public void setLinkScores(Map<TypedLinkNode<TypedLinkNodeModule<String,CopyOfBFEdge>,CopyOfBFEdge>,Float> hif)
	{
		this.linkscores = hif;
	}
	
	/*Can't think of a useful hashcode that is finalish.
	public int hashCode()
	{
		return linkscores.hashCode();
	}*/
	
	public boolean equals(Object other)
	{
		return this==other;
	}
	
	public int countLinkScores()
	{
		return this.linkscores.size();
	}
}
