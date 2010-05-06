package org.idekerlab.PanGIAPlugin.ModFinder;


import java.util.HashSet;
import java.util.Set;

import org.idekerlab.PanGIAPlugin.networks.SFNetwork;
import org.idekerlab.PanGIAPlugin.networks.linkedNetworks.TypedLinkNode;
import org.idekerlab.PanGIAPlugin.networks.linkedNetworks.TypedLinkNodeModule;


public class SouravScore extends HCScoringFunction {

	private float alpha = 1.6f;
	private float alpham = 1.0f; //Multiplier coefficient for the alpha term
	//private final float min_lod_genetic = 0.3485652f/(1-0.3485652f); //Smallest genetic background score
	//private final float min_lod_physical =  0.09817198f/(1-0.09817198f); //Smallest physical background score
	//private final float min_lod_genetic = (float)Math.log(.007/(1-.007)); //Smallest genetic background score
	//private final float min_lod_physical =  (float)Math.log(.001/(1-.001)); //Smallest physical background score
	
	//This should ALWAYS be 0. Scoring should be rescaled to 0+.
	//private final float min_lod_genetic = 0f; //Smallest genetic background score 
	//private final float min_lod_physical =  0f; //Smallest physical background score
	
	public SouravScore(SFNetwork pnet, SFNetwork gnet, float alpha, float alpham)
	{
		super(pnet,gnet);
		this.alpha = alpha;
		this.alpham = alpham;
	}
	
	public void Initialize(SFNetwork pnet, SFNetwork gnet)
	{
		buildScoreTables(pnet, gnet);
	}
	
	/*
	public float getPhysicalWithinScore(String n1 , String n2)
	{
		if (this.pnet.contains(n1, n2)) return this.pnet.edgeValue(n1, n2);
		else return 0;
	}
	
	public float getPhysicalBetweenScore(String n1 , String n2)
	{
		if (this.pnet.contains(n1, n2))	return this.pnet.edgeValue(n1, n2);
		else return 0;
	}
	
	public float getGeneticWithinScore(String n1 , String n2)
	{
		if (this.gnet.contains(n1, n2)) return this.gnet.edgeValue(n1, n2);
		else return 0;
	}
	
	public float getGeneticBetweenScore(String n1 , String n2)
	{
		if (this.gnet.contains(n1, n2)) return this.gnet.edgeValue(n1, n2);
		else return 0;
	}*/
	
	public float getWithinScore(TypedLinkNodeModule<String,BFEdge> mod)
	{
		float score = 0;
		
		float s1 = getWithinScore(mod,this.pscores);
		float s2 = getWithinScore(mod,this.gscores);
		
		if (!Float.isNaN(s1)) score+=s1;
		if (!Float.isNaN(s2)) score+=s2;
		
		score += complexReward(mod.size());
				
		return score;
	}
	
	/***
	 *Retrieves the within score for a module, for a given network. (physical/genetic)   
	 */
	private float getWithinScore(TypedLinkNodeModule<String,BFEdge> mod, SFNetwork scores)
	{
		float score = 0;
		
		Set<TypedLinkNode<String,BFEdge>> members = new HashSet<TypedLinkNode<String,BFEdge>>(mod.size());
		
		for (TypedLinkNode<String,BFEdge> member : mod.members())
		{	
			members.add(member);
			
			for (TypedLinkNode<String,BFEdge> other : members)
			{
				if (other.equals(member)) continue;
				final float val = scores.edgeValue(member.value(), other.value());
				if (!Float.isNaN(val))
					score += val;
			}
		}
				
		return score;
	}
	
	public float getBetweenScore(TypedLinkNode<String,BFEdge> n1, TypedLinkNode<String,BFEdge> n2)
	{
		float score = 0;
		
		float s1 = -this.pscores.edgeValue(n1.value(), n2.value());
		float s2 = this.gscores.edgeValue(n1.value(), n2.value());
		
		if (!Float.isNaN(s1)) score+=s1;
		if (!Float.isNaN(s2)) score+=s2;
		
		return score;
	}
	
	/***
	 * Gets the between score for two modules.
	 * Score = genetic - physical
	 */
	public float getBetweenScore(TypedLinkNodeModule<String,BFEdge> mod1, TypedLinkNodeModule<String,BFEdge> mod2)
	{
		float score = 0;
		
		float s1 = -getBetweenScore(mod1, mod2, this.pscores);
		float s2 = getBetweenScore(mod1, mod2, this.gscores);
		
		if (!Float.isNaN(s1)) score+=s1;
		if (!Float.isNaN(s2)) score+=s2;
		
		return score;
	}
	
	/***
	 *Retrieves the between score for two modules, for a given network. (physical/genetic)   
	 */
	private float getBetweenScore(TypedLinkNodeModule<String,BFEdge> mod1, TypedLinkNodeModule<String,BFEdge> mod2, SFNetwork scores)
	{
		float score = 0;
		
		for (TypedLinkNode<String,BFEdge> m1 : mod1.members())
			for (TypedLinkNode<String,BFEdge> m2 : mod2.members()) {
				final float val = scores.edgeValue(m1.value(), m2.value());
				if (!Float.isNaN(val))
					score += val;
			}
					
		return score;
	}
	
	public float complexReward(int size)
	{
		return alpham*(float)Math.pow(size,alpha);
	}

}
