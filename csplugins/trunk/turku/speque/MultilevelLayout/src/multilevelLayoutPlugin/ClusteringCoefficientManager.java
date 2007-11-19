/*
	
	MultiLevelLayoutPlugin for Cytoscape (http://www.cytoscape.org/) 
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

package multilevelLayoutPlugin;

import giny.model.Edge;
import giny.model.Node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cytoscape.CyNetwork;

/**
 * Class used to calculate and store clustering coefficients
 * (here: CC-values) of a graph. 
 * 
 * @author Pekka Salmela
 *
 */
public class ClusteringCoefficientManager {
	
	/**
	 * Network containing the nodes that CC-values 
	 * are calculated and stored for.
	 */
	private CyNetwork network;
	
	/**
	 * Container for CC-values. Maps node root
	 * graph indices to CC-values.
	 */
	private HashMap<Integer, Double> ccValues;
	
	/**
	 * Class constructor.
	 * @param network Network containing the nodes that 
	 * CC-values are calculated and stored for.
	 */
	public ClusteringCoefficientManager(CyNetwork network){
		this.network = network;
		this.ccValues = new HashMap<Integer, Double>();
	}
	
	/**
	 * Calculates and storees CC-values for all the
	 * nodes given in constructor. Does nothing if 
	 * the given network is <code>null</code>.
	 */
	@SuppressWarnings("unchecked")
	public void setAllCCs(){
		if(network == null) return;
		Iterator<Node> iter = network.nodesIterator();
		HashSet<Node> neighboursOfN = new HashSet<Node>();
		HashSet<Node> checked = new HashSet<Node>();
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
				double links = 0.0;
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
						if(target != m && neighboursOfN.contains(target)  && !checked.contains(target)) { 
							links++; checked.add(target);
						}
					}
				}
				addCC(n.getRootGraphIndex(), (links)/(numberOfNeighbours * (numberOfNeighbours - 1.0)));
			}
			else{
				addCC(n.getRootGraphIndex(), 0.0);
			}
		}
	}
	
	/**
	 * Sets CC-value for a node with root graph index
	 * <code>id</code> as <code>x</code>. Throws 
	 * <code>IllegalStateException</code> if 
	 * <code>x</code> is less than zero or more than
	 * one.
	 *   
	 * @param id Root graph index of the node the CC-value
	 * is stored for.
	 * @param x CC-value for the node with given root graph
	 * index.
	 * @throws IllegalStateException
	 */
	private void addCC(int id, double x) throws IllegalStateException{
		if(x < 0 || x > 1) throw new IllegalStateException("Value tried was " + x);
		ccValues.put(id, x);
	}
	
	/**
	 * Gets the CC-value of the node with given 
	 * root graph index. Throws NullPointerException
	 * if no node with given root graph index is found.
	 * 
	 * @param id Root graph index of the node the 
	 * CC-value is looked for.
	 * @return CC-value of the node with given root
	 * graph index.
	 * @throws NullPointerException
	 */
	public double getCC(int id) throws NullPointerException{
		return ccValues.get(id);
	}
}