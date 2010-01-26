package org.idekerlab.denovoplugin.networks.denovoPGNetworkAlignment;

import java.util.*;

import org.idekerlab.denovoplugin.networks.linkedNetworks.*;
import org.idekerlab.denovoplugin.networks.matrixNetworks.FloatMatrixNetwork;
import org.idekerlab.denovoplugin.networks.*;
import org.idekerlab.denovoplugin.data.DoubleVector;

public abstract class HCScoringFunction {

	//Computed pair by pair scores
	//protected FloatMatrixNetwork scores_pwithin;
	//protected FloatMatrixNetwork scores_pbetween;
	//protected FloatMatrixNetwork scores_gwithin;
	//protected FloatMatrixNetwork scores_gbetween;
	
	protected FloatMatrixNetwork pscores;
	protected FloatMatrixNetwork gscores;
	
	//The largest node indexes 
	protected int gmaxnodei;
	protected int pmaxnodei;
	
	//Genetic scores assigned directly from the network
	protected FloatMatrixNetwork raw_genetic;
	
	public abstract float complexReward(int size);
	/*
	protected abstract float getPhysicalWithinScore(String n1, String n2);
	protected abstract float getPhysicalBetweenScore(String n1, String n2);
	protected abstract float getGeneticWithinScore(String n1, String n2);
	protected abstract float getGeneticBetweenScore(String n1, String n2);
	*/
	/**
	 * Perform initial calculations. (build lookup matricies etc.)
	 */
	public abstract void Initialize(SFNetwork pnet, SFNetwork gnet);
	
	public abstract float getWithinScore(TypedLinkNodeModule<String,BFEdge> m1);
	
	/***
	 * Gets the between score for two modules.
	 */
	public abstract float getBetweenScore(TypedLinkNode<String,BFEdge> n1, TypedLinkNode<String,BFEdge> n2);
	public abstract float getBetweenScore(TypedLinkNodeModule<String,BFEdge> m1, TypedLinkNodeModule<String,BFEdge> m2);
	
	/***
	 * Retrieves the raw interaction data from a network and puts it into a lookup matrix.
	 */
	public HCScoringFunction(SFNetwork pnet, SFNetwork gnet)
	{
		gmaxnodei = gnet.numNodes()-1;
		pmaxnodei = pnet.numNodes()-1;
	}
	
	protected void buildScoreTables(SFNetwork pnet, SFNetwork gnet)
	{
		//Build the physical within and between scores
		System.out.println("Initializing physical scores...");
		List<String> nodes = new ArrayList<String>(pnet.getNodes());
		pscores = new FloatMatrixNetwork(false,false,nodes);
						
		for (int i=0;i<nodes.size()-1;i++)
		{
			if (i%1000==0) System.out.println((float)i/nodes.size()*100+"%");
			for (int j=i+1;j<nodes.size();j++)
			{
				float score = pnet.edgeValue(nodes.get(i), nodes.get(j));
				if (!Float.isNaN(score)) pscores.set(i, j, score);
			}
		}
		
		//Build the genetic within and between scores
		System.out.println("Initializing genetic scores...");
		nodes = new ArrayList<String>(gnet.getNodes());
		gscores = new FloatMatrixNetwork(false,false,nodes);
		
		for (int i=0;i<nodes.size()-1;i++)
		{
			if (i%1000==0) System.out.println((float)i/nodes.size()*100+"%");
			for (int j=i+1;j<nodes.size();j++)
			{
				float score = gnet.edgeValue(nodes.get(i), nodes.get(j));
				if (!Float.isNaN(score)) gscores.set(i, j, score);
			}
		}
	}
	
	
	
	
	public void InitializeRaw(FloatMatrixNetwork gnet)
	{
		this.raw_genetic = gnet;
	}
	
	/***
	 *Calculates the empirical within enrichment score of a hypermodule. 
	 */
	public float getWithinEnrichment(TypedLinkNodeModule<String,Float> m, int numtrials)
	{
		DoubleVector dist = getWithinDistribution(m,numtrials);
		return (float)dist.getEmpiricalPvalue(getRawWithin(m),true);
	}
	
	/***
	 *Calculates the empirical between enrichment score for two hypermodules. 
	 */
	public float getBetweenEnrichment(TypedLinkNodeModule<String,Float> m1, TypedLinkNodeModule<String,Float> m2, int numtrials)
	{
		DoubleVector dist = getBetweenDistribution(m1,m2,numtrials);
		//dist.plothist(100);
		return (float)dist.getEmpiricalPvalue(getRawBetween(m1,m2),true); 
	}
	
	/***
	 *Gets the raw genetic score sum between two hypermodules.  
	 */
	private float getRawBetween(TypedLinkNodeModule<String,Float> m1, TypedLinkNodeModule<String,Float> m2)
	{
		float score = 0;
		
		for (TypedLinkNode<String,Float> n1 : m1)
			for (TypedLinkNode<String,Float> n2 : m2)
				score+= raw_genetic.edgeValue(n1.value(),n2.value());
		
		return score;
	}
	
	/***
	 *Gets the raw genetic score sum within a hypermodule.  
	 */
	private float getRawWithin(TypedLinkNodeModule<String,Float> m)
	{
		float score = 0;
		
		Set<TypedLinkNode<String,Float>> members = new HashSet<TypedLinkNode<String,Float>>(m.size(),1);
		
		for (TypedLinkNode<String,Float> n : m)
		{
			members.add(n);
			
			for (TypedLinkNode<String,Float> other : members)
			{
				if (other==n) continue;
				
				score+= raw_genetic.edgeValue(other.value(),n.value());
			}
		}
		
		return score;
	}
	
	/***
	 *Gets a random distribution of genetic interactions sumscores with the same count as are possible between two hypermodules.   
	 */
	private DoubleVector getBetweenDistribution(TypedLinkNodeModule<String,Float> m1, TypedLinkNodeModule<String,Float> m2, int numtrials)
	{
		DoubleVector scores = new DoubleVector(numtrials);
		
		int edgeCount = m1.size() * m2.size();
				
		java.util.Random randgen = new java.util.Random();
		randgen.setSeed(System.nanoTime());
		for (int t=0;t<numtrials;t++)
		{
			float score = 0;
					
			for (int p=0;p<edgeCount;p++)
			{
				int p1 = (int)Math.round(randgen.nextDouble()*gmaxnodei);
				int p2 = (int)Math.round(randgen.nextDouble()*gmaxnodei);
				
				if (p1==p2) {p--; continue;}
				
				score += raw_genetic.edgeValue(p1,p2);
			}
			
			scores.add(score);
		}
		
		return scores;
	}
	
	
	/***
	 *Gets a random distribution of genetic interactions sumscores with the same count as are possible within a hypermodule.   
	 */
	private DoubleVector getWithinDistribution(TypedLinkNodeModule<String,Float> m, int numtrials)
	{
		DoubleVector scores = new DoubleVector(numtrials);
		
		int edgeCount = m.size()*(m.size()-1)/2;
				
		java.util.Random randgen = new java.util.Random();
		randgen.setSeed(System.nanoTime());
		for (int t=0;t<numtrials;t++)
		{
			float score = 0;
			
			for (int p=0;p<edgeCount;p++)
			{
				int p1 = (int)Math.round(randgen.nextDouble()*gmaxnodei);
				int p2 = (int)Math.round(randgen.nextDouble()*gmaxnodei);
				
				if (p1==p2) {p--; continue;}
				
				score += raw_genetic.edgeValue(p1,p2);
			}
			
			scores.add(score);
		}
		
		return scores;
	}
}
