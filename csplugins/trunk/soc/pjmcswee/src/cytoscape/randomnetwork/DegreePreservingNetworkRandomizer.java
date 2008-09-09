 /* File: DegreePreservingNetworkRandomizer.java
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package cytoscape.randomnetwork;

import cytoscape.*;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;
import java.util.*;


/**
 *  This NetworkRandomizerModel shuffles edges, while keeping the in-degree and out-degree of a node the same.
 *   
 *   Based on NCT code.  
 *
 *	@author Patrick J. McSweeney
 *  @version 1.0
 *
 */
public class DegreePreservingNetworkRandomizer extends NetworkRandomizerModel{

	/**
	 * The number of iterations to shuffle the edges for.
	 */
	 private int mIterations;
	
	
	/**
	*  Constructor 
	*
	*  @param  pGraph The DynamicGraph to randomize.
	*  @param pDirected Specifices how to treat the network directed(true) undirected (false).
	*/
	public DegreePreservingNetworkRandomizer(RandomNetwork pGraph,
			boolean pDirected, int pShuffles) 
	{
		super(pGraph,  pDirected);
		mIterations = pShuffles;
	}
	
	/**
	* Creates a copy of this NetworkRandomizer
	*
	* @return An exact copy of this NetworkRandomizer
	*/
	public RandomNetworkGenerator copy()
	{
		
		return new DegreePreservingNetworkRandomizer(mOriginal,  mDirected, mIterations); 
	}
	
	
	/**
	 * @return Gets the display name for this generator.
	 */
	public String getName()
	{
		return new String("Degree Preserving Edge Shuffle Model");
	}


	/**
	 * This function generates a randomized instance of the original network.
	 * Where in each node has the same in/out degree as the original network,
	 * however each node's neighbor hood has been stochastically shuffled
	 *  
	 * Works by continuously picking two edges (u,v) (s,t) with no overlap in {u,v,s,t}.
	 * Removes the original two edges and creates two new edges (u,t) and (s,v).
	 * 
	 *
	 * @return Returns a DynamicGraph which has had its edges shuffled.
	 */
	public RandomNetwork generate()
	{
		
		//
		RandomNetwork newGraph = new RandomNetwork(mOriginal, mDirected);
		
		//Get an iterator for the nodes
		int N =  mOriginal.nodes().numRemaining();

		//Create a linkedList to hold the node Indicies
		LinkedList<Integer> nodeList = new LinkedList<Integer>();
		
		IntEnumerator iter = newGraph.nodes();
		while(iter.numRemaining() > 0)
		{
			nodeList.add(new Integer(iter.nextInt()));
		}
		
	
		for(int e = 0; e < mIterations; e++) 
		{
		
			
			//Variables to hold onto two edges: A, B
			//The source of edge A
			int sourceAIndex = -1;
			//The source of edge B
			int sourceBIndex = -1;
			//The target of edge A
			int targetAIndex = -1;
			//The target of edge B
			int targetBIndex = -1;
			//The index for edge 1
			int edge1Index = -1;
			//The index for edge 2
			int edge2Index = -1; 
			
			//Iterate until we find two suitable edges
			boolean done = false;
			while (!done) {
			
				//Choose two random nodes
				sourceAIndex = nodeList.get(mRandom.nextInt(nodeList.size()));
				sourceBIndex = nodeList.get(mRandom.nextInt(nodeList.size()));
		
				//Get their connection information
				IntEnumerator aenum = newGraph.edgesAdjacent(sourceAIndex,mDirected, false, !mDirected);
				IntEnumerator benum = newGraph.edgesAdjacent(sourceBIndex,mDirected, false, !mDirected);				
				
				///See what their degrees are
				int aDegree = aenum.numRemaining();
				int bDegree = benum.numRemaining();
				
				/*if(allowSelfEdge && (aDegree == N))
				{				
					System.out.println("Breaking 1");
					break;
				}
				else*/ 
				if(aDegree >= (N - 1))
				{				
					System.out.println("Breaking 2");
					break;
				}
				
				

				//Make sure they do not match
				if(( sourceAIndex == sourceBIndex) || (aDegree <= 0 ) || (bDegree <= 0 ))
				{
				     continue;
				}
				
				//Choose two neighbors from these nodes
				int aNeighIndex = mRandom.nextInt(aDegree);
				int bNeighIndex = mRandom.nextInt(bDegree);				
				
				
				//Iterate over their edges to choose a random neighbor
				for(int k = 0; k <= aNeighIndex; k++)
				{	
					edge1Index = aenum.nextInt();
				}
				
				//Iterate over their edges to choose a random neighbor
				for(int k = 0; k <= bNeighIndex; k++)
				{	
					edge2Index = benum.nextInt();
				}
				
				//Get the other node associated with this edge
				targetAIndex = newGraph.edgeSource(edge1Index);
				if(targetAIndex == sourceAIndex)
				{
					targetAIndex = newGraph.edgeTarget(edge1Index);
				}
				
				//Get the other node associated with this edge
				targetBIndex = newGraph.edgeSource(edge2Index);
				if(targetBIndex == sourceBIndex)
				{
					targetBIndex = newGraph.edgeTarget(edge2Index);
				}				


				//Make sure the targets do not match with each other, or their alternate sources
				if((targetBIndex == targetAIndex) || ( targetAIndex == sourceBIndex) || (targetBIndex == sourceAIndex))
				{
					continue;
				}
				//Don't want to stomp on existing edges
				boolean shouldBreak  = false;
				
				//Iterate through the existing edges from source A
				aenum = newGraph.edgesAdjacent(sourceAIndex,mDirected,false,!mDirected);
				while((aenum.numRemaining() > 0)&&(!shouldBreak))
				{
					//get the next edge
					int nextEdge = aenum.nextInt();

					//if we have a match then break
					if(newGraph.edgeTarget(nextEdge) == targetBIndex)
					{
						shouldBreak = true;
					}
					
					//If it is undirected check the source of teh edge as well
					if(!mDirected)
					{
						//If we have a match then we should break
						if(newGraph.edgeSource(nextEdge) == targetBIndex)
						{
							shouldBreak = true;
						}
					}
				}
				//If the loop ended prematurely then we found a match, keep searching
				if(shouldBreak)
				{
					continue;
				}
				
				//Iterate throught the existing edges on the other nedge
				benum = newGraph.edgesAdjacent(sourceBIndex,mDirected,false,!mDirected);
				while((benum.numRemaining() > 0)&&(!shouldBreak))
				{
					//Get the next edge
					int nextEdge = benum.nextInt();
					
					//if we found a match we should break
					if(newGraph.edgeTarget(nextEdge) == targetAIndex)
					{
						shouldBreak = true;
					}
					
					//If it is undirected edge, then look in both directions
					if(!mDirected)
					{
						//If we found a match, then we should break
						if(newGraph.edgeSource(nextEdge) == targetAIndex)
						{
							shouldBreak = true;
						}
					}
				}
				
				//If the loop ended prematurely then we found a match, keep searching
				if(shouldBreak)
				{
					continue;
				}
				
				//If we got this far then we are done
				done = true;
			
			}
			
			if(done)
			{
				//Remove these two edges
				newGraph.edgeRemove(edge1Index);
				newGraph.edgeRemove(edge2Index);
			
				//Create the two new edges
				int newEdge1Index = newGraph.edgeCreate(sourceAIndex,targetBIndex,mDirected);
				int newEdge2Index = newGraph.edgeCreate(sourceBIndex,targetAIndex,mDirected);
			}
			
		}
		return newGraph;
	}
}
	
	
	

