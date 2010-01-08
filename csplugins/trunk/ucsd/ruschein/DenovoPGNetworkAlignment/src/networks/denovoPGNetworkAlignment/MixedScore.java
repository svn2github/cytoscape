package networks.denovoPGNetworkAlignment;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import networks.linkedNetworks.*;
import networks.matrixNetworks.FloatMatrixNetwork;

public class MixedScore extends HCScoringFunction{

	private float alpha = 3.5f;
	private float alpham = -1.0f; //Multiplier coefficient for the alpha term
	private final float beta = .9f;
	private final float logbeta = (float)Math.log(beta);
	private final float logoneminusbeta = (float)Math.log(1-beta);
	
	//Stats saved for Ryan's background correction
	private float ppnetsum;
	private float ginetsum;
	
	public MixedScore(FloatMatrixNetwork pnet, FloatMatrixNetwork gnet, float alpha, float alpham)
	{
		super(pnet,gnet);
		this.alpha = alpha;
		this.alpham = alpham;
	}
	
	public void Initialize()
	{
		setNetSums(); //Need a FMN version of this?
		buildScoreTables();
	}
	
	private void setNetSums()
	{
		//Calculate ppnetsum and ginetsum (used for background scores later)
		ppnetsum=0;
		for (TypedLinkNode<String,Float> n : pnet.nodeIterator())
			for (TypedLinkEdge<String,Float> e : n.edgeIterator())
				ppnetsum += e.value();
		ppnetsum /= 2.0;
					
		ginetsum=0;
		for (TypedLinkNode<String,Float> n : gnet.nodeIterator())
			for (TypedLinkEdge<String,Float> e : n.edgeIterator())
				ginetsum += e.value();
		ginetsum /= 2.0;
	}
	
	protected float getPhysicalWithinScore(String n1, String n2)
	{
		return getWithinScore(pnet,n1,n2,ppnetsum);
	}
	
	protected float getGeneticWithinScore(String n1, String n2)
	{
		return getWithinScore(gnet,n1,n2,ginetsum);
	}
	
	protected float getPhysicalBetweenScore(String n1, String n2)
	{
		return getBetweenScore(pnet,n1,n2,ppnetsum);
	}
	
	protected float getGeneticBetweenScore(String n1, String n2)
	{
		return getBetweenScore(gnet,n1,n2,ginetsum);
	}
	
	public float complexReward(int size)
	{
		if (size>1) return alpham*(float)Math.pow(size-1,alpha);
		else return 0;
	}
	
	/***
	 * A quick way to get the within score for two proteins.
	 */
	private float getWithinScore(TypedLinkNetwork<String,Float> net, String n1, String n2, float netsum)
	{
		float score = 0;
		float backscore = backgroundScore(net, n1, n2, netsum);
		
		TypedLinkEdge<String,Float> edge = net.getEdge(n1, n2);
		
		if (edge==null) score += logoneminusbeta - Math.log(1-backscore);
		else
		{
			float eval = edge.value(); 
			
			score += eval * (logbeta - Math.log(backscore));
			score += (1-eval) * (logoneminusbeta - Math.log(1-backscore));
		}
		
		return score;
	}
	
	/***
	 * A quick way to get the between score for two proteins.
	 */
	private float getBetweenScore(TypedLinkNetwork<String,Float> net, String n1, String n2, float netsum)
	{
		float score = 0;
		float backscore = backgroundScore(net, n1, n2, netsum);
		
		TypedLinkEdge<String,Float> edge = net.getEdge(n1, n2);
		if (edge==null) score += logoneminusbeta - Math.log(1-backscore);
		else
		{
			float eval = edge.value(); 
			
			score += eval * (logbeta - Math.log(backscore));
			score += (1-eval) * (logoneminusbeta - Math.log(1-backscore));
		}
		
		return score;
	}
	
	/***
	 * Returns the background score for two proteins.
	 */
	private float backgroundScore(TypedLinkNetwork<String,Float> net, String n1, String n2, float netsum)
	{
		float d1 = (float)net.getNode(n1).numNeighbors();
		float d2 = (float)net.getNode(n1).numNeighbors();
		
		float this_score = 1.0f / (1.0f + 2*(netsum + .5f - d1 - d2 + 1) / (d1 * d2));
		
		return this_score;		
	}
	
	/***
	 * Gets the within score for a module based only in the physical space. (slower than using the hypermodule version)
	 * Score = physical + genetic
	 */
	public float getWithinScore(TypedLinkNodeModule<String,Float> aset)
	{
		float score = getWithinScore(aset, pnet,scores_pwithin);
		score += getWithinScore(aset, gnet,scores_gwithin);
				
		return score;
	}
		
	/***
	 * Gets the between score for two modules.
	 * Score = genetic - physical
	 */
	public float getBetweenScore(TypedLinkNodeModule<String,Float> set1, TypedLinkNodeModule<String,Float> set2)
	{
		float score = -getBetweenScore(set1, set2, pnet,scores_pbetween, pmaxnodei);
		score += getBetweenScore(set1, set2, gnet,scores_gbetween, gmaxnodei);
		
		return score;
	}
	
	
	/***
	 *Retrieves the within score for a module, for a given network. (physical/genetic)   
	 */
	private float getWithinScore(TypedLinkNodeModule<String,Float> aset, TypedLinkNetwork<String,Float> net, FloatMatrixNetwork scores)
	{
		float score = 0;
		
		Set<TypedLinkNode<String,Float>> members = new HashSet<TypedLinkNode<String,Float>>(aset.size(),1);
		
		for (TypedLinkNode<String,Float> n1 : aset)
		{	
			members.add(n1);
			
			for (TypedLinkNode<String,Float> other : members)
			{
				if (other==n1) continue;
				
				score+= scores.edgeValue(other.value(),n1.value());
			}
		}
		
		//Alpha term correction
		score += complexReward(aset.size());
				
		return score;
	}
	
	/***
	 *Retrieves the between score for two modules, for a given network. (physical/genetic)   
	 */
	private float getBetweenScore(TypedLinkNodeModule<String,Float> set1, TypedLinkNodeModule<String,Float> set2, TypedLinkNetwork<String,Float> net, FloatMatrixNetwork scores, int maxnodei)
	{
		float score = 0;
		
		for (TypedLinkNode<String,Float> n1 : set1)
			for (TypedLinkNode<String,Float> n2 : set2)
				score+= scores.edgeValue(n1.value(),n2.value());
		
		return score;
	}
}
