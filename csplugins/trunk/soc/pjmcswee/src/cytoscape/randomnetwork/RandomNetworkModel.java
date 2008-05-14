/*
File: RandomNetworkPlugin
Author: Patrick J. McSweeney
Creation Date: 5/07/08
*/
package cytoscape.randomnetwork;
import java.util.Random;

public abstract class RandomNetworkModel  
{
	protected int numNodes;
	protected int numEdges;
	protected boolean directed;
	protected long seed;
	protected Random random;


	protected static int UNSPECIFIED = -1;

	
	RandomNetworkModel(int pNumNodes, int pNumEdges, boolean pDirected)
	{
		numNodes = pNumNodes;
		numEdges = pNumEdges;
		directed = pDirected;
		seed = UNSPECIFIED;
		random = new Random();
	}
	
	public void setSeed(long pSeed)
	{
		seed = pSeed; 
		random = new Random(seed);
	}
	public long getSeed(){return seed;}
	public int getNumNodes(){return numNodes;}
	public int getNumEdges(){return numEdges;}
	public boolean getDirected(){return directed;}
	
	public abstract void Generate(); 
	public abstract void Compare();
}