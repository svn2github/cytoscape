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

import giny.model.Node;
import giny.view.NodeView;

import java.text.DecimalFormat;
import java.util.Iterator;

import cytoscape.view.CyNetworkView;

/**
 * Class used to calculate node deviation 
 * (mapped to x- and y-axises) statistics 
 * of a network view
 * 
 * @author Pekka Salmela
 */
public class NodeDeviationStatistics {
	
	/**
	 * Bunch of figures used in the calculation.
	 */
	private int count = 0;
	private double sum = 0;
	private double squareSum = 0;
	private double max = Double.NEGATIVE_INFINITY;
	private double min = Double.POSITIVE_INFINITY;
	private double sumAbsValueMinusMean = 0;
	private double xStandardDeviation = 0;
	private double yStandardDeviation = 0;
	
	/**
	 * Network view the statistics are calculated for. 
	 */
	private CyNetworkView view;
	
	/**
	 * Class constructor.
	 * @param view Network view the statistics are 
	 * calculated for.
	 */
	public NodeDeviationStatistics(CyNetworkView view){
		this.view = view;
	}
	
	/**
	 * Calculate node deviation statistics for 
	 * the network view given in the constructor.
	 */
	@SuppressWarnings("unchecked")
	public void generateStatistics(){
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		
		Iterator<Node> iter = view.getNetwork().nodesIterator();
		while(iter.hasNext()){
			Node n = iter.next();
			NodeView nv = view.getNodeView(n);
			//NodeView nv  = finalView.getNodeView(iter.next());
			if(nv.getXPosition() < minX) minX = nv.getXPosition();
			if(nv.getYPosition() < minY) minY = nv.getYPosition();
			if(nv.getXPosition() > maxX) maxX = nv.getXPosition();
			if(nv.getYPosition() > maxY) maxY = nv.getYPosition();
		}
		
		iter = view.getNetwork().nodesIterator();
		int[] xDeviation = new int[10];
		int[] yDeviation = new int[10];
		while(iter.hasNext()){
			Node n = iter.next();
			NodeView nv = view.getNodeView(n);
			int xBucket = (int)(10.0*(nv.getXPosition()-minX)/(maxX-minX));
			if (xBucket==10) xBucket = 9;
			xDeviation[xBucket]++;
			int yBucket = (int)(10.0*(nv.getYPosition()-minY)/(maxY-minY));
			if (yBucket==10) yBucket = 9;
			yDeviation[yBucket]++;
		}
		
		for(int i = 0; i < 10; i++){
			count++;
			if(xDeviation[i]>max) max = xDeviation[i];
			if(xDeviation[i]<min) min = xDeviation[i];
			sum += xDeviation[i];
			squareSum += xDeviation[i]*xDeviation[i];
		}
		
		for(int i = 0; i < 10; i++){
			sumAbsValueMinusMean += Math.abs(xDeviation[i] - getMean());
		}
		
		xStandardDeviation = getStandardDeviation();
		
		count = 0;
		sum = 0;
		squareSum = 0;
		max = Double.NEGATIVE_INFINITY;
		min = Double.POSITIVE_INFINITY;
		sumAbsValueMinusMean = 0;
		
		for(int i = 0; i < 10; i++){
			count++;
			if(yDeviation[i]>max) max = yDeviation[i];
			if(yDeviation[i]<min) min = yDeviation[i];
			sum += yDeviation[i];
			squareSum += yDeviation[i]*yDeviation[i];
		}
		
		for(int i = 0; i < 10; i++){
			sumAbsValueMinusMean += Math.abs(yDeviation[i] - getMean());
		}
		
		yStandardDeviation = getStandardDeviation();
	}
	
	/**
	 * Gets a string representation of the 
	 * calculated statistics.
	 * @return Results of the calculation.
	 */
	public String reportStatistics(){
		DecimalFormat df = new DecimalFormat("0.##");
		String result = "";
		result += "Standard deviation of node x-positions: " + df.format(xStandardDeviation) + "\n";
		result += "Standard deviation of node y-positions: " + df.format(yStandardDeviation) + "\n";
		return result;
	}
	
	/**
	 * Gets the average of node coordinates.
	 * Value is Double.NaN if count == 0.
	 * @return The average of node coordinates.
	 */
	private double getMean() {
		return sum / count;  
	}
	
	/**
	 * Gets the standard deviation of node coordinates.
	 * Value will be Double.NaN if count == 0.
	 * @return The standard deviation of node coordinates.
	 */
	private double getStandardDeviation() {  
	   double mean = getMean();
	   return Math.sqrt( squareSum/count - mean*mean );
	}
	
	/**
	 * Gets the standard deviation of node 
	 * x-coordinates.
	 * @return The standard deviation of node 
	 * x-coordinates.
	 */
	public double getXStandardDeviation() {
		return xStandardDeviation;
	}

	/**
	 * Gets the standard deviation of node 
	 * y-coordinates.
	 * @return The standard deviation of node 
	 * y-coordinates.
	 */
	public double getYStandardDeviation() {
		return yStandardDeviation;
	}
}
