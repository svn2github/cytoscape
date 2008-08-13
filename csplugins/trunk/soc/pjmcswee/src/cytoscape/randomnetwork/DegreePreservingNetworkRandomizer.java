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
import cytoscape.graph.dynamic.util.*;
import cytoscape.graph.dynamic.*;
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
	 private int iterations;

	/**
	*  Constructor 
	*
	*  @param  pNetwork The network to randomize.
	*  @param pDirected Specifices how to treat the network directed(true) undirected (false).
	*/
	public DegreePreservingNetworkRandomizer(CyNetwork pNetwork, 
			boolean pDirected, int pShuffles) 
	{
		super(pNetwork,  pDirected);
		iterations = pShuffles;
	}
	
	
	/**
	* Creates a copy of this NetworkRandomizer
	*
	* @return An exact copy of this NetworkRandomizer
	*/
	public RandomNetworkGenerator copy()
	{
		return new DegreePreservingNetworkRandomizer(cytoNetwork,  directed, iterations); 
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
	public DynamicGraph generate()
	{
		
		//
		DynamicGraph newGraph = DynamicGraphFactory.instantiateDynamicGraph();
		
		//Create a linkedList to hold the node Indicies
		LinkedList<Integer> nodeList = new LinkedList<Integer>();
		
		//Get an iterator for the nodes
		int N =  original.nodes().numRemaining();
		
		//Iterate through all of the nodes		
		for(int i = 0; i < N; i++)
		{
			//Get the node indicies
			int nodeIndex = newGraph.nodeCreate();
			
			//Save the node indicies in this list
			nodeList.add(nodeIndex);
		}
		
		//Iterate through all of the edges
		IntEnumerator edgeEnum = original.edges();
		while(edgeEnum.numRemaining() > 0)
		{
			int edgeIndex = edgeEnum.nextInt();
			int source = original.edgeSource(edgeIndex);
			int target = original.edgeTarget(edgeIndex);
			newGraph.edgeCreate(source, target, directed);
		}
		
		for(int e = 0; e < iterations; e++) 
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
				sourceAIndex = nodeList.get(random.nextInt(nodeList.size()));
				sourceBIndex = nodeList.get(random.nextInt(nodeList.size()));
		
				//Get their connection information
				IntEnumerator aenum = newGraph.edgesAdjacent(sourceAIndex,directed, false, !directed);
				IntEnumerator benum = newGraph.edgesAdjacent(sourceBIndex,directed, false, !directed);				
				
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
				int aNeighIndex = random.nextInt(aDegree);
				int bNeighIndex = random.nextInt(bDegree);				
				
				
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
				aenum = newGraph.edgesAdjacent(sourceAIndex,directed,false,!directed);
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
					if(!directed)
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
				benum = newGraph.edgesAdjacent(sourceBIndex,directed,false,!directed);
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
					if(!directed)
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
				int newEdge1Index = newGraph.edgeCreate(sourceAIndex,targetBIndex,directed);
				int newEdge2Index = newGraph.edgeCreate(sourceBIndex,targetAIndex,directed);
			}
			
		}
		return newGraph;
	}
}
	
	
	

