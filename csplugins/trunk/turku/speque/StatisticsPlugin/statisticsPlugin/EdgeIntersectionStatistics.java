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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;

/**
 * Class used to calculate edge intersection 
 * statistics for a sample of edges (max. 1000) 
 * in a given network view.
 * 
 * @author Pekka Salmela
 */
public class EdgeIntersectionStatistics {
	
	/**
	 * The network view the statistics are 
	 * calculated for.
	 */
	private CyNetworkView view;
	
	/**
	 * Number of intersections found.
	 */
	private int intersections;
	
	/**
	 * Number of edges checked.
	 */
	private int checkedEdges = 0;
	
	/**
	 * Class constructor.
	 * @param view The network view the statistics are 
	 * calculated for.
	 */
	public EdgeIntersectionStatistics(CyNetworkView view){
		this.view = view;
	}
	
	/**
	 * Calculates the edge intersection statistics 
	 * for the network view given in the constructor.
	 */
	@SuppressWarnings("unchecked")
	public void generateStatistics(){
		CyNetwork network = view.getNetwork();
		HashSet<Edge> selectedEdges = new HashSet<Edge>();
		
		//check if there are more than 1000 edges
		if(network.getEdgeCount() <= 1000){
			//if not, use all the edges with different source and target nodes
			Iterator<CyEdge> iter = network.edgesIterator();
			while(iter.hasNext()){
				Edge e = iter.next();
				if(e.getSource() != e.getTarget()) selectedEdges.add(e);
			}
		}
		else{
			//else pick (max) 1000 random nodes with different source and target nodes 
			Iterator<CyEdge> iter = network.edgesIterator();
			Vector<Integer> allEdgeIndices = new Vector<Integer>();
			while(iter.hasNext()){
				allEdgeIndices.add(new Integer(iter.next().getRootGraphIndex()));
			}
			int j = 0;
			while(j < 1000 && !allEdgeIndices.isEmpty()){
				Random randomNumberGenerator = new Random(10);
				Integer x = allEdgeIndices.elementAt((int)(randomNumberGenerator.nextDouble()*allEdgeIndices.size()));
				Edge e = network.getEdge(x);
				allEdgeIndices.remove(x);
				if(e.getSource() != e.getTarget()){ 
					selectedEdges.add(e);
					j++;
				}
			}
		}
		
		//use brute force to check all the edge pairs to check if they intesect
		Iterator<Edge> iter1 = selectedEdges.iterator();
		while(iter1.hasNext()){
			Edge e1 = iter1.next();
			Iterator<Edge> iter2 = selectedEdges.iterator();
			while(iter2.hasNext()){
				Edge e2 = iter2.next();
				if(e1!=e2 && edgeIntersection(e1, e2)) intersections++;
			}
		}
		checkedEdges = selectedEdges.size();
		intersections = intersections/2;
	}
	
	/**
	 * Gets a string representation of the 
	 * calculation results.
	 * @return Calculation results.
	 */
	public String reportStatistics(){
		return "Number of intersections of " + checkedEdges + " random edges: " + (intersections) + "\n";
	}
	
	/**
	 * Returns true if the two edges in the given view intersect, 
	 * false if the edges do not intersect.
	 * 
	 * @param e1 First edge.
	 * @param e2 Second edge.
	 * @return True, if the given edges intersect, 
	 * false otherwise. 
	 */
	private boolean edgeIntersection(Edge e1, Edge e2){
		double e1x1 = view.getNodeView(e1.getSource()).getXPosition();
		double e1x2 = view.getNodeView(e1.getTarget()).getXPosition();
		double e1y1 = view.getNodeView(e1.getSource()).getYPosition();
		double e1y2 = view.getNodeView(e1.getTarget()).getYPosition();
		double A1 = e1y2 - e1y1;
		double B1 = e1x1 - e1x2;
		double C1 = e1x2*e1y1 - e1x1*e1y2;
		
		double e2x1 = view.getNodeView(e2.getSource()).getXPosition();
		double e2x2 = view.getNodeView(e2.getTarget()).getXPosition();
		double e2y1 = view.getNodeView(e2.getSource()).getYPosition();
		double e2y2 = view.getNodeView(e2.getTarget()).getYPosition();
		double A2 = e2y2 - e2y1;
		double B2 = e2x1 - e2x2;
		double C2 = e2x2*e2y1 - e2x1*e2y2;
		
		//definition: if the lines share a common endpoint, they do not intersect
		if(e1x1 == e2x1 && e1y1 == e2y1) return false;
		if(e1x1 == e2x2 && e1y1 == e2y2) return false;
		if(e1x2 == e2x2 && e1y2 == e2y2) return false;
		if(e1x2 == e2x1 && e1y2 == e2y1) return false;
		
		double det = A1*B2 - A2*B1;
	    if(det != 0){
	    	boolean edgesCrossing = true;
	        Double x = (B1*C2 - B2*C1)/det;
	        Double y = (A2*C1 - A1*C2)/det;
	        if(x<=Math.min(e1x1, e1x2) || x>=Math.max(e1x1, e1x2)) edgesCrossing = false;
	        if(y<=Math.min(e1y1, e1y2) || y>=Math.max(e1y1, e1y2)) edgesCrossing = false;
	        if(x<=Math.min(e2x1, e2x2) || x>=Math.max(e2x1, e2x2)) edgesCrossing = false;
	        if(y<=Math.min(e2y1, e2y2) || y>=Math.max(e2y1, e2y2)) edgesCrossing = false;
	        if(edgesCrossing) return true;
	    }
		return false;
	}

	/**
	 * Gets the number of checked edges.
	 * @return The number of checked edges.
	 */
	public int getCheckedEdges() {
		return checkedEdges;
	}

	/**
	 * Gets the number of intersections found. 
	 * @return The number of intersections found.
	 */
	public int getIntersections() {
		return intersections;
	}	
}