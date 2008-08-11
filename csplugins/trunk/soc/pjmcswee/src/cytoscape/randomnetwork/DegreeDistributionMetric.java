/* File: DegreeDistribution.java
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

import java.util.*;
import cytoscape.graph.dynamic.*;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;



/*
 *  Compute the alpha value from k^(-alpha).
 *  Most "real-world" scale-free networks have 
 *  alpha value between 2 and 3, sometimes between 1 and 2.
 */
public  class DegreeDistributionMetric implements NetworkMetric {

	public String getDisplayName()
	{
		return new String("Degree Distribution");
	}

	public NetworkMetric copy()
	{
		return new DegreeDistributionMetric();
	}
	
	public double analyze(DynamicGraph network, boolean directed)
	{
		//The value to store alpha in
		double power = 0;
		
		//Iterate through all of thie nodes in this network
		IntEnumerator nodeIterator = network.nodes();
		
		//Use as the number of nodes in the network
		int N  = nodeIterator.numRemaining();
		
		
		//Store the degree distribution 
		int degree[] = new int[N];
	

		while(nodeIterator.numRemaining() > 0)
		{
			int nodeIndex = nodeIterator.nextInt();
			IntEnumerator edgeIterator = network.edgesAdjacent(nodeIndex,true,false,true);
			int nodeDegree = edgeIterator.numRemaining();
			degree[nodeDegree]++;
			//System.out.println(nodeIndex + "\t" + nodeDegree);
		}
		
		int count = 0;
		for(int i = 2; i < (N-1); i ++)
		{
			//If this bin is not populated ignore it
			if(degree[i] != 0)
			{
				//Add to the power
				power += Math.log(((double)degree[i])/N)/Math.log(i); 
				//increment the count
				count++;
			}
		}
		
		return power/count;
	}
}
