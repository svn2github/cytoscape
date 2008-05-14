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
import java.util.Random;
import java.util.Vector;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.view.CyNetworkView;

/**
 * Class used to calculate statistics about angles
 * between edges originating from a node in a given network
 * view. The idea is based on one of the graph aesthetics
 * criteria presented by Helen C. Purchase (2002). 
 * According to the so called Minimum Angle metric 
 * a nice layout should try to maximize the minimum 
 * angle between edges originating from every node.
 * this class is used to pick a random sample of nodes 
 * (no more than 1000) and calculate the mean of the 
 * minimum of the angles between edges.
 *  
 * @author Pekka Salmela
 *
 */
public class EdgeAngleStatistics {
	
	/**
	 * Network view the statistics are calculated for.
	 */
	private CyNetworkView view;
	
	/**
	 * Sum of the minimum angles.
	 */
	private double sum = 0;
	
	/**
	 * Number of sampled nodes.
	 */
	private int checkedNodes = 0;
	
	/**
	 * Class constructor.
	 * @param view Network view the statistics are 
	 * calculated for.
	 */
	public EdgeAngleStatistics(CyNetworkView view){
		this.view = view;
	}
	
	/**
	 * Generates statistics for the network view given 
	 * in the constructor and stores them locally.
	 */
	@SuppressWarnings("unchecked")
	public void generateStatistics(){
		CyNetwork network = view.getNetwork();
		HashSet<Node> selectedNodes = new HashSet<Node>();
		
		//check if there are more than 1000 nodes
		if(network.getNodeCount() <= 1000){
			//if not, use all of them with degree > 1
			Iterator<CyNode> iter = network.nodesIterator();
			while(iter.hasNext()){
				Node n = iter.next();
				if(network.getDegree(n) > 1) selectedNodes.add(n);
			}
		}
		else{
			//else pick (max) 1000 random nodes with degree > 1
			Iterator<CyNode> iter = network.nodesIterator();
			Vector<Integer> allNodeIndices = new Vector<Integer>();
			while(iter.hasNext()){
				allNodeIndices.add(new Integer(iter.next().getRootGraphIndex()));
			}
			int j = 0;
			while(j < 1000 && !allNodeIndices.isEmpty()){
				Random randomNumberGenerator = new Random(10);
				Integer x = allNodeIndices.elementAt((int)(randomNumberGenerator.nextDouble()*allNodeIndices.size()));
				Node n = network.getNode(x);
				allNodeIndices.remove(x);
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
				if(degree > 1){
					selectedNodes.add(n);
					j++;
				}
			}
		}
		
		//iterate over picked nodes
		Iterator<Node> iter = selectedNodes.iterator();
		while(iter.hasNext()){
			Node n  = iter.next();
			//collect adjacent edges 
			int[] adjacentEdges = network.getAdjacentEdgeIndicesArray(n.getRootGraphIndex(), true, true, true);
			//calculate angle between edges and x-axis, and store the results in a Vector
			Vector<EdgeAngle> angles = new Vector<EdgeAngle>();
			for(int i = 0; i < adjacentEdges.length; i++){
				Edge e = network.getEdge(adjacentEdges[i]);
				double x1 = view.getNodeView(e.getSource()).getXPosition();
				double y1 = view.getNodeView(e.getSource()).getYPosition();
				double x2 = view.getNodeView(e.getTarget()).getXPosition();
				double y2 = view.getNodeView(e.getTarget()).getYPosition();
				if(e.getSource() != e.getTarget() && (x1 != x2 && y1 != y2)){
					angles.add(new EdgeAngle(e, angleBetweenXAxis(n, e)));
				}
			}
			if (angles.size() > 1) {
				//sort the angles in ascending order
				angles = sortEdgeAngles(angles);
				//calculate angle between edges next to each other and store the smallest one
				double realAngles = 0;
				double smallestAngle = 360.0;
				for (int i = 0; i < angles.size(); i++) {
					if (i != angles.size() - 1) {
						Edge first = angles.elementAt(i).getEdge();
						Edge second = angles.elementAt(i + 1).getEdge();
						double candidate = angleBetweenEdges(n, first, second);
						if (candidate < smallestAngle && 
								!(first.getSource() == second.getSource() && first.getTarget() == second.getTarget()) &&
								!(first.getTarget() == second.getSource() && first.getSource() == second.getTarget()))
							smallestAngle = candidate;
							realAngles++;
					} else {
						//let's not forget the angle between the first and the last one
						Edge first = angles.elementAt(i).getEdge();
						Edge second = angles.elementAt(0).getEdge();
						double candidate = angleBetweenEdges(n, first, second);
						if (candidate < smallestAngle && 
								//are the end points of both edgees same? 
								!(first.getSource() == second.getSource() && first.getTarget() == second.getTarget()) &&
								!(first.getTarget() == second.getSource() && first.getSource() == second.getTarget()))
							smallestAngle = candidate;
							realAngles++;
					}
				}
				//
				if(smallestAngle != 360.0){
					sum += ((360.0/realAngles)-smallestAngle)/(360.0/realAngles);
					checkedNodes++;
				}
			}		
		}	
	}
	
	/**
	 * Gets information about the calculated statistics.
	 * @return String representation of the statistics.
	 */
	public String reportStatistics(){
		//return information about the mean of the smallest angles
		DecimalFormat df = new DecimalFormat("0.##");
		if(checkedNodes != 0) return "Angle deviation (0 = worst, 1 = best, " + checkedNodes + " nodes examined): " + (df.format(1.0 - sum/checkedNodes)) + "\n";
		else return "No nodes checked for angle deviation! \n";
	}
	
	/**
	 * Calculates the angle between two edges originating 
	 * from the same node.
	 * @param n The common node for the two edges.
	 * @param e1 First edge.
	 * @param e2 Second edge.
	 * @return The angle between the two given edges (in degrees).
	 */
	private double angleBetweenEdges(Node n, Edge e1, Edge e2){
		double e1x1 = view.getNodeView(n).getXPosition();
		double e1y1 = view.getNodeView(n).getYPosition();
		double e1x2; double e1y2;
		if(view.getNodeView(e1.getSource()).getXPosition() != e1x1 || view.getNodeView(e1.getSource()).getYPosition() != e1y1){
			e1x2 = view.getNodeView(e1.getSource()).getXPosition();
			e1y2 = view.getNodeView(e1.getSource()).getYPosition();
		}
		else{
			e1x2 = view.getNodeView(e1.getTarget()).getXPosition();
			e1y2 = view.getNodeView(e1.getTarget()).getYPosition();
		}
		
		double e2x1 = e1x1;
		double e2y1 = e1y1;
		double e2x2; double e2y2;
		if(view.getNodeView(e2.getSource()).getXPosition() != e2x1 || view.getNodeView(e2.getSource()).getYPosition() != e2y1){
			e2x2 = view.getNodeView(e2.getSource()).getXPosition();
			e2y2 = view.getNodeView(e2.getSource()).getYPosition();
		}
		else{
			e2x2 = view.getNodeView(e2.getTarget()).getXPosition();
			e2y2 = view.getNodeView(e2.getTarget()).getYPosition();
		}
		
		double ax = e1x2 - e1x1;
		double ay = e1y2 - e1y1;
		double bx = e2x2 - e2x1;
		double by = e2y2 - e2y1;
		
		double angle = Math.acos((ax*bx + ay*by)/(Math.sqrt(ax*ax + ay*ay)*Math.sqrt(bx*bx + by*by)));
		
		return Math.toDegrees(angle);
	}
	
	/**
	 * Calculates the angle between the given edge and 
	 * x-axis.
	 * 
	 * @param n 
	 * @param e
	 * @return
	 */
	private double angleBetweenXAxis(Node n, Edge e){
		double e1x1 = view.getNodeView(n).getXPosition();
		double e1y1 = view.getNodeView(n).getYPosition();
		double e1x2 = e1x1 + 1.0;
		double e1y2 = e1y1;
		
		double e2x1 = e1x1;
		double e2y1 = e1y1;
		double e2x2; double e2y2;
		if(view.getNodeView(e.getSource()).getXPosition() != e2x1 || view.getNodeView(e.getSource()).getYPosition() != e2y1){
			e2x2 = view.getNodeView(e.getSource()).getXPosition();
			e2y2 = view.getNodeView(e.getSource()).getYPosition();
		}
		else{
			e2x2 = view.getNodeView(e.getTarget()).getXPosition();
			e2y2 = view.getNodeView(e.getTarget()).getYPosition();
		}
		
		double ax = e1x2 - e1x1;
		double ay = e1y2 - e1y1;
		double bx = e2x2 - e2x1;
		double by = e2y2 - e2y1;
		
		double angle = Math.acos((ax*bx + ay*by)/(Math.sqrt(ax*ax + ay*ay)*Math.sqrt(bx*bx + by*by)));
		
		if(by < 0) angle = angle + Math.PI;
		
		return Math.toDegrees(angle);
	}
	
	private Vector<EdgeAngle> sortEdgeAngles(Vector<EdgeAngle> ea){
		if(ea.size() <= 1) return ea;
		Vector<EdgeAngle> less = new Vector<EdgeAngle>();
		Vector<EdgeAngle> pivotList = new Vector<EdgeAngle>();
		Vector<EdgeAngle> greater = new Vector<EdgeAngle>();
		
	    double pivot = ea.elementAt((int)(Math.random() * ea.size())).getAngle();
	    
	    for (int i = 0; i < ea.size(); i++){
	         if(ea.elementAt(i).getAngle() < pivot) { less.add(ea.elementAt(i)); }
	         if(ea.elementAt(i).getAngle() == pivot) { pivotList.add(ea.elementAt(i)); }
	         if(ea.elementAt(i).getAngle() > pivot) { greater.add(ea.elementAt(i)); }
	    }
	    
	    less = sortEdgeAngles(less);
	    less.addAll(pivotList);
	    greater = sortEdgeAngles(greater);
	    less.addAll(greater);
	    return less;
	}
	
	/**
	 * Container class used to store edges with 
	 * the angle between x-axis.
	 */
	private class EdgeAngle{
		private Edge e;
		private double angle;
		public EdgeAngle(Edge e, double angle){
			this.e = e;
			this.angle = angle;
		}
		public double getAngle(){
			return angle;
		}
		public Edge getEdge(){
			return e;
		}
	}

	/**
	 * Gets the number of checked nodes.
	 * @return Number of checked nodes.
	 */
	public int getCheckedNodes() {
		return checkedNodes;
	}

	/**
	 * Gets the sum of checked angles.
	 * @return Sum of checked angles.
	 */
	public double getSum() {
		return sum;
	}
	
}
