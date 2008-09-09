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
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;



/**
 *  Compute the alpha value from k^(-alpha).
 *  Most "real-world" scale-free networks have 
 *  alpha value between 2 and 3, sometimes between 1 and 2.
 *
 *
 *	@author Patrick J. McSweeney
 *  @version 1.0
 */
public  class DegreeDistributionMetric implements NetworkMetric {


	/**
	 * @return The display name of this network metric
	 */
	public String getDisplayName()
	{
		return new String("Degree Distribution");
	}

	/**
	 *
	 */
	public NetworkMetric copy()
	{
		return new DegreeDistributionMetric();
	}
	
	
	/**
	 *
	 */
	public double analyze(RandomNetwork network, boolean directed)
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
			IntEnumerator edgeIterator = network.edgesAdjacent(nodeIndex,directed,directed,!directed);
			int nodeDegree = edgeIterator.numRemaining();
			degree[nodeDegree]++;
		}
		
						
		return  leastSquares(degree)[0];
	}
	
	
	
	/**----------------------------------------------------------------------
	 *
	 * Fits the logarithm distribution/degree to a straight line of the form:
	 *	a + b *x which is then interrpreted as a*x^y in the non-logarithmic scale
	 *
	 * @param dist The distribution of node degrees to fit to a logarithmized straight line
	 *
	 * @return An array of 4 doubles
	 *					index 0:  beta value
	 *					index 1:  log(alpha) value (e^alpha for comparisons with NetworkAnalyzer
	 *					index 2:  r^2 correlation coefficient
	 *					index 3:  covariance
	 *
	 *  For more see Wolfram Least Squares Fitting
	 *----------------------------------------------------------------------*/
	public double[] leastSquares(int dist[])
	{
		
		//Vararibles to compute
		double SSxx = 0;
		double SSyy = 0;
		double SSxy = 0;
		
		
		//Compute the average log(x) value when for positive (>0) values
		double avgX = 0;
		double nonZero = 0;
		for(int i = 1; i < dist.length; i++)
		{
			if(dist[i] > 0)
			{
				avgX += Math.log(i);
				nonZero++;
			}
		}
		avgX /= nonZero;
	
		//compute the variance of log(x)
		for(int i = 1; i < dist.length; i++)
		{
			if(dist[i] > 0)
			{
				SSxx += Math.pow(Math.log(i)  - avgX ,2);
			}
		}
		
		
		//Compute the average log(y) values
		double avgY = 0;
		for(int i = 1; i < dist.length; i++)
		{
			if(dist[i] > 0)
			{
				avgY += Math.log(dist[i]);
			}
		}
		avgY /= nonZero;
	
		//compute the variance over the log(y) values
		for(int i = 1; i < dist.length; i++)
		{
			if(dist[i] > 0)
			{
				SSyy += Math.pow(Math.log(dist[i]) - avgY ,2);
			}
		}


		//Compute teh SSxy term
		for(int i = 1; i < dist.length; i++)
		{
			if(dist[i] > 0)
			{
				SSxy += (Math.log(i) - avgX) * (Math.log(dist[i]) - avgY);
			}
		}
	
			
		//Compute and return the results
		double results[] = new double[4];
		results[0] = SSxy/SSxx; 
		results[1] = avgY - results[0] * avgX;
		results[2] =  (SSxy * SSxy) /(SSxx * SSyy);
		results[3] = SSxy/nonZero;
	
		return results;
	
	}
	
}