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
 * Class used to count statistics of node degrees 
 * of a network.
 *  
 * @author Pekka Salmela
 */
public class NodeDegreeStatistics {
	
	/**
	 * Bunch of figures used in the calculation.
	 */
	private int count = 0;
	private double sum = 0;
	private double squareSum = 0;
	private double max = Double.NEGATIVE_INFINITY;
	private double min = Double.POSITIVE_INFINITY;
	private double sumAbsValueMinusMean = 0;
	private int degreeOne = 0;
	private int degreeLessThanFive = 0;
	private int degreeLessThanTen = 0;
	private int degreeLessThanFifty = 0;
	
	/**
	 * The network statistics are calculated for.
	 */
	private CyNetwork network;
	
	/**
	 * Class constructor.
	 * @param network The network statistics are 
	 * calculated for.
	 */
	public NodeDegreeStatistics(CyNetwork network){
		this.network = network;
	}
	
	@SuppressWarnings("unchecked")
	public void generateStatistics(){
		Iterator<Node>  iter = network.nodesIterator();
		while(iter.hasNext()){
			count++;
			Node n = iter.next();
			int[] adjEdgeIndices = network.getAdjacentEdgeIndicesArray(n.getRootGraphIndex(), true, true, true);
			HashSet<Node> adjacentEdges = new HashSet<Node>();
			for(int i = 0; i < adjEdgeIndices.length; i++){
				Edge e = network.getEdge(adjEdgeIndices[i]);
				Node t = e.getTarget();
				Node s = e.getSource();
				if(n.getRootGraphIndex() != t.getRootGraphIndex() && !adjacentEdges.contains(t)) adjacentEdges.add(t);
				if(n.getRootGraphIndex() != s.getRootGraphIndex() && !adjacentEdges.contains(s)) adjacentEdges.add(s);
			}
			int degree = adjacentEdges.size();
    		if (degree > max)
                max = degree;
    		if (degree < min)
                min = degree;
    		if(degree == 1) degreeOne++;
    		if(degree <= 5) degreeLessThanFive++;
    		if(degree <= 10) degreeLessThanTen++;
    		if(degree <= 50) degreeLessThanFifty++;
    		sum += degree;
    		squareSum += degree*degree;	
		}
		/*iter = network.nodesIterator();
		while(iter.hasNext()){
			CyNode n = iter.next();
			int degree = network.getDegree(n);
			sumAbsValueMinusMean += Math.abs(degree - getMean());
		}*/
	}
	
	public String reportStatistics(){
		DecimalFormat df = new DecimalFormat("0.##");
		String result = "";
		result += "Node degree mean: " + df.format(getMean()) + "\n";
		result += "Maximum node degree: " + getMax() + "\n";
		result += "Minimum node degree: " + getMin() + "\n";
		return result;
	}
	
	public int getCount() {   
	    // Return number of nodes
	 return count;
	}
	
	public double getSum() {
	    // Return the sum of degrees
	 return sum;
	}
	
	private double getMean() {
	    // Return average of degrees
	    // Value is Double.NaN if count == 0.
	 return sum / count;  
	}
	
	public double getStandardDeviation() {  
	   // Return standard deviation of edge degrees
	   // Value will be Double.NaN if count == 0.
	 double mean = getMean();
	 return Math.sqrt( squareSum/count - mean*mean );
	}
	
	public double getMeanDeviation(){
		// Return standard deviation of degrees
		// Value will be Double.NaN if count == 0.
		return sumAbsValueMinusMean/count;
	}
	
	public double getMin() {
	   // Return the smallest degree
	   // Value will be infinity if if count == 0.
	 return min;
	}
	
	public double getMax() {
	   // Return the largest degree
	   // Value will be -infinity if count == 0.
	 return max;
	}

	/**
	 * @return the degreeLessThanFifty
	 */
	public int getDegreeLessThanFifty() {
		return degreeLessThanFifty;
	}

	/**
	 * @return the degreeLessThanFive
	 */
	public int getDegreeLessThanFive() {
		return degreeLessThanFive;
	}

	/**
	 * @return the degreeLessThanTen
	 */
	public int getDegreeLessThanTen() {
		return degreeLessThanTen;
	}

	/**
	 * @return the degreeOne
	 */
	public int getDegreeOne() {
		return degreeOne;
	}
	
}
