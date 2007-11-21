/*
	
	StatisticsPlugin for Cytoscape (http://www.cytoscape.org/) 
	Copyright (C) 2007 Pekka Salmela

	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
	
 */

package statisticsPlugin;

import giny.model.Edge;
import giny.model.Node;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Iterator;

import cytoscape.CyNetwork;

/**
 * Class used to calculate clustering coefficient 
 * (here: CC-value) statistics from a given network.
 *  
 * @author Pekka Salmela
 *
 */
public class ClusteringCoefficientStatistics {
	
	/**
	 * Network the statistics are calculated for.
	 */
	private CyNetwork network;
	
	/**
	 * Number of nodes in the given network.
	 */
	private double count = 0;
	
	/**
	 * Sum of all the calculated CC-values.
	 */
	private double sum = 0;
	
	/**
	 * Class constructor.
	 * @param network Network the statistics are 
	 * calculated for. 
	 */
	public ClusteringCoefficientStatistics(CyNetwork network){
		this.network = network;
		this.count = network.getNodeCount();
	}
	
	/**
	 * Calculate CC-values for all the nodes in 
	 * the given network. 
	 */
	@SuppressWarnings("unchecked")
	public void generateStatistics(){
		HashSet<Node> neighboursOfN = new HashSet<Node>();
		HashSet<Node> checked = new HashSet<Node>();
		Iterator<Node> iter = network.nodesIterator();
		while(iter.hasNext()){
			Node n = iter.next();
			int[] adjacentEdges = network.getAdjacentEdgeIndicesArray(n.getRootGraphIndex(), true, true, true);
			neighboursOfN.clear();
			for(int i = 0; i < adjacentEdges.length; i++){
				Edge e = network.getEdge(adjacentEdges[i]);
				if(e.getSource() != n) neighboursOfN.add(e.getSource());
				if(e.getTarget() != n) neighboursOfN.add(e.getTarget());
			}
			double numberOfNeighbours = neighboursOfN.size();
			if (numberOfNeighbours > 1) {
				double links = 0;
				for(Node m : neighboursOfN){
					int[] adjacentEdges2 = network.getAdjacentEdgeIndicesArray(m.getRootGraphIndex(), true, true, true);
					checked.clear();
					for(int i = 0; i < adjacentEdges2.length; i++){
						Edge e = network.getEdge(adjacentEdges2[i]);
						Node source = e.getSource();
						if(source != m && neighboursOfN.contains(source) && !checked.contains(source)) { 
							links++; checked.add(source);
						}
						Node target = e.getTarget();
						if(target != m && neighboursOfN.contains(target) && !checked.contains(target)) { 
							links++; checked.add(target);
						}
					}
				}
				sum += (links)/(numberOfNeighbours * (numberOfNeighbours -1.0));
			}
		}
	}
	
	/**
	 * Gets information about the statistics calculated.
	 * @return String representation of the statistics 
	 * calculated.
	 */
	public String reportStatistics(){
		DecimalFormat df = new DecimalFormat("0.##");
		return "Clustering coefficient: " + df.format(sum/count) + "\n";
	}

	/**
	 * Gets the number of nodes in the network 
	 * given in the constructor.
	 * @return Number of nodes in the network 
	 * given in the constructor.
	 */
	public double getCount() {
		return count;
	}

	/**
	 * Gets the sum of all the calculated CC-values.
	 * @return Sum of all the calculated CC-values.
	 */
	public double getSum() {
		return sum;
	}
}
