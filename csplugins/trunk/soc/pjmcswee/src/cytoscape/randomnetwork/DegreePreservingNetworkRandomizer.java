 /* File: EdgeShuffleRandomizer.java
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


import cytoscape.graph.dynamic.util.*;
import cytoscape.graph.dynamic.*;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;
import java.util.*;


/*

*/
public class DegreePreservingNetworkRandomizer extends NetworkRandomizerModel{

	/* Default Constructor */
	public DegreePreservingNetworkRandomizer(DynamicGraph pOriginal, String[] pNodeIds,
			boolean pDirected) 
	{
		super(pOriginal, pNodeIds, pDirected);
	}
	

	/**
	*
	*/
	public DynamicGraph generate()
	{
		//DynamicGraph graph = originalGraph.copy();
		DynamicGraph graph = original;
		
		//Create a linkedList to hold the node Indicies
		LinkedList<Integer> nodeList = new LinkedList<Integer>();
		
		//Get an iterator for the nodes
		IntEnumerator nodeIterator = graph.nodes();
		
		while(nodeIterator.numRemaining() > 0)
		{
			int nodeIndex = nodeIterator.nextInt();
			nodeList.add(nodeIndex);
			
			
		}
		
		
	
		
		
		int numEdges = graph.edges().numRemaining();
		for ( int e = 0; e < numEdges; e++ ) 
		{
			int sourceAIndex = -1;
			int sourceBIndex = -1;
			int targetAIndex = -1;
			int targetBIndex = -1;
			int edge1Index = -1;
			int edge2Index = -1; 
			
			boolean done = false;
			while (!done) {
			
				//Choose two random nodes
				sourceAIndex = nodeList.get( random.nextInt(nodeList.size()) );
				sourceBIndex = nodeList.get( random.nextInt(nodeList.size()) );
		
				//Get their connection information
				IntEnumerator aenum = graph.edgesAdjacent(sourceAIndex,directed,false, !directed);
				IntEnumerator benum = graph.edgesAdjacent(sourceBIndex,directed,false, !directed);				
				
				///See what their degree is
				int aDegree = aenum.numRemaining();
				int bDegree = benum.numRemaining();

				//Make sure they do not match
				if (( sourceAIndex == sourceBIndex) || (aDegree <= 0 )|| (bDegree <= 0 ))
				     continue;

				//Choose two neighbors from these nodes
				int aNeighIndex = random.nextInt(aDegree);
				int bNeighIndex = random.nextInt(bDegree);				
				
				//System.out.println(e + "\t" + aNeighIndex + "<\t" + aDegree + "\t" + bNeighIndex + "<\t" + bDegree);
				
				//Iterate over their edges to choose the random one
				for(int k = 0; k <= aNeighIndex; k++)
				{	
					edge1Index = aenum.nextInt();
				}
				
				//Iterate over their edges to choose the random one
				for(int k = 0; k <= bNeighIndex; k++)
				{	
					edge2Index = benum.nextInt();
				}
				
				//Get the other node associated with this edge
				targetAIndex = graph.edgeSource(edge1Index);
				if(targetAIndex == sourceAIndex)
				{
					targetAIndex = graph.edgeTarget(edge1Index);
				}
				
				//Get the other node associated with this edge
				targetBIndex = graph.edgeSource(edge2Index);
				if(targetBIndex == sourceBIndex)
				{
					targetBIndex = graph.edgeTarget(edge2Index);
				}				


				//Make sure the targets do not match
				if((targetBIndex == targetAIndex) || ( targetAIndex == sourceBIndex) || (targetBIndex == sourceAIndex))
					continue;

				// don't want to stomp on existing edges
				boolean shouldBreak  = false;
				aenum = graph.edgesAdjacent(sourceAIndex,directed,false,!directed);
				while((aenum.numRemaining() > 0)&&(!shouldBreak))
				{
					int nextEdge = aenum.nextInt();
					if(graph.edgeTarget(nextEdge) == targetBIndex)
					{
						shouldBreak = true;
					}
					
					if(!directed)
					{
						if(graph.edgeSource(nextEdge) == targetBIndex)
						{
							shouldBreak = true;
						}
					}
				}
				if(shouldBreak)
					continue;
				
				benum = graph.edgesAdjacent(sourceBIndex,directed,false,!directed);
				while((benum.numRemaining() > 0)&&(!shouldBreak))
				{
					int nextEdge = benum.nextInt();
					if(graph.edgeTarget(nextEdge) == targetAIndex)
					{
						shouldBreak = true;
					}
					
					if(!directed)
					{
						if(graph.edgeSource(nextEdge) == targetAIndex)
						{
							shouldBreak = true;
						}
						
					
					
					}
				}
				
				if(shouldBreak)
					continue;
				
				done = true;
			}
			

			graph.edgeRemove(edge1Index);
			graph.edgeRemove(edge2Index);

			int newEdge1Index = graph.edgeCreate(sourceAIndex,targetBIndex,directed);
			int newEdge2Index = graph.edgeCreate(sourceBIndex,targetAIndex,directed);

			
		}
		return graph;
	}
}
	
	
	

