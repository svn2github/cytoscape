/* File: AverageDegreeMetric.java
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
 *  This NetworkMetric determines the ratio of edges over nodes.
 *
 * @author Patrick J. McSweeney
 * @version 1.0
 */
public class AverageDegreeMetric implements NetworkMetric {

	/**
	 * Returns the name of this metric.
	 * @return The name of this metric
	 */
	public String getDisplayName()
	{
		return new String("Average Degree");
	}
	
	
	/**
	 * @return A new AverageDegreeMetric instace.
	 */
	public NetworkMetric copy()
	{
		return new AverageDegreeMetric();
	}
	
	/**
	 * Calculates the average of edges over nodes.
	 *
	 * @param pNetwork The network to analyze.
	 * @param pDirected Specifies how to treat the network directed or undirected.
	 *                  The pDirected parameter is not used here, but is required by the interface.
	 * @return The ratio of edges over nodes.
	 */
	public double analyze(RandomNetwork pNetwork, boolean pDirected)
	{
		//The value to be returned
		double averageDegree = 0;
		
		//The number of nodes
		IntEnumerator nodeIterator = pNetwork.nodes();
		double N  = nodeIterator.numRemaining();
	
		//The number of edges
		IntEnumerator edgeIterator = pNetwork.edges();
		double E = edgeIterator.numRemaining();
		
		//Every edge whether directed or undirected has two edge-endpoints (or degrees)
		E *= 2.0d;	
		
		
		//compute the ratio
		averageDegree = E / N;
	
		//Return the result
		return averageDegree;
	}
	
}





