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
import giny.view.NodeView;

import java.text.DecimalFormat;
import java.util.Iterator;

import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;

/**
 * 
 * @author Pekka Salmela
 *
 */
public class EdgeLengthStatistics {
	private int count = 0;
	private int totalCount = 0;
	private double sum = 0;
	private double squareSum = 0;
	private double max = Double.NEGATIVE_INFINITY;
	private double min = Double.POSITIVE_INFINITY;
	private double sumAbsValueMinusMean = 0;
	private CyNetworkView view;
	
	public EdgeLengthStatistics(CyNetworkView view){
		this.view = view;
	}
	
	@SuppressWarnings("unchecked")
	public void generateEdgeStatistics(){
		CyNetwork network = view.getNetwork();
		Iterator<Edge> iter = network.edgesIterator();
	    double tempMax = 0;
	    while(iter.hasNext()){
	    	Edge e = iter.next();
	    	if(e.getSource() != e.getTarget()){
	    		NodeView sv = view.getNodeView(e.getSource());
	    		NodeView tv = view.getNodeView(e.getTarget());
	    		double length = Math.sqrt(
	    				Math.pow((sv.getXPosition()-tv.getXPosition()), 2) +
	    				Math.pow((sv.getYPosition()-tv.getYPosition()), 2));
	            if (length > tempMax)
	               tempMax = length;
	    	}
	    }
	    iter = network.edgesIterator();
	    while(iter.hasNext()){
	    	Edge e = iter.next();
	    	if(e.getSource() != e.getTarget()){
	    		NodeView sv = view.getNodeView(e.getSource());
	    		NodeView tv = view.getNodeView(e.getTarget());
	    		double length = 100.0*Math.sqrt(
					Math.pow((sv.getXPosition()-tv.getXPosition()), 2) +
					Math.pow((sv.getYPosition()-tv.getYPosition()), 2))
					/tempMax;
	    		if (length > max)
	                max = length;
	    		if (length < min)
	                min = length;
	    		count++;
	    		sum += length;
	    		squareSum += length*length;	
	    	}
	    	else totalCount++;
	    }
	    
	    iter = network.edgesIterator();
	    while(iter.hasNext()){
	    	Edge e = iter.next();
	    	if(e.getSource() != e.getTarget()){
	    		NodeView sv = view.getNodeView(e.getSource());
	    		NodeView tv = view.getNodeView(e.getTarget());
	    		double length = 100.0*Math.sqrt(
						Math.pow((sv.getXPosition()-tv.getXPosition()), 2) +
						Math.pow((sv.getYPosition()-tv.getYPosition()), 2))
						/tempMax;
	    		sumAbsValueMinusMean += Math.abs(length - getMean());
	    	}
	    }
	    
	}
	
	public String reportStatistics(){
		DecimalFormat df = new DecimalFormat("0.##");
	    String result = "";
	    result += "Number of (non-loop) edges: " + getCount() + " (total: " + (count + totalCount) + ")\n";
	    result += "Edge length standard deviation: " + df.format(getStandardDeviation()) + "\n";
	    //result += "Edge Length Mean Deviation: " + df.format(getMeanDeviation()) + "\n";
	    return result;
	}

	public int getCount() {   
	    // Return number of edges
	 return count;
	}
	
	public double getSum() {
	    // Return the sum of edge lengths
	 return sum;
	}
	
	public double getMean() {
	    // Return average of edge lengths
	    // Value is Double.NaN if count == 0.
	 return sum / count;  
	}
	
	public double getStandardDeviation() {  
	   // Return standard deviation of edge lengths
	   // Value will be Double.NaN if count == 0.
	 double mean = getMean();
	 return Math.sqrt( squareSum/count - mean*mean );
	}
	
	public double getMeanDeviation(){
		// Return standard deviation of edge lengths
		// Value will be Double.NaN if count == 0.
		return sumAbsValueMinusMean/count;
	}
	
	public double getMin() {
	   // Return the smallest edge length
	   // Value will be infinity if if count == 0.
	 return min;
	}
	
	public double getMax() {
	   // Return the largest edge length
	   // Value will be -infinity if count == 0.
	 return max;
	}
}
