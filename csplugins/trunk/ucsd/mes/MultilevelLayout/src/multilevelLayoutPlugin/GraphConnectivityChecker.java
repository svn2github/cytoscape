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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

/**
 * Class used to check if a graph is connected or not.
 * @author Pekka Salmela
 *
 */

public class GraphConnectivityChecker {
	
	/**
	 * Checks if the graph <code>cn </code> is connected.
	 * @param cn Graph to be checked.
	 * @return <code>True</code> if the graph <code>cn</code> is
	 * connected, <code>false</code> otherwise.
	 */
	@SuppressWarnings("unchecked")
	public static Vector<CyNetwork> getGraphComponents(CyNetwork cn){
	    Iterator<CyNode> iter = cn.nodesIterator();
	    HashSet<Node> L = new HashSet<Node>();
	    Vector<Node> K = new Vector<Node>();
	    HashSet<Edge> E = new HashSet<Edge>();
	    Vector<CyNetwork> result = new Vector<CyNetwork>();
	    while(iter.hasNext()){
	    	CyNode x = iter.next();
	    	boolean newComponent = false;
	    	if(result.isEmpty()) newComponent = true;
	    	else{
	    		newComponent = true;
	    		for(CyNetwork network : result){
	    			if(network.containsNode(x)) newComponent = false;
	    		}
	    	}
	    	if(newComponent){
			    L.clear(); L.add(x);
			    K.clear(); K.add(x);
			    E.clear();
			    while (!K.isEmpty()){
			    	Node y = K.lastElement();
			    	K.remove(K.size()-1);
			    	int[] eIndices = cn.getAdjacentEdgeIndicesArray(y.getRootGraphIndex(), true, true, true);
			    	HashSet<Edge> edgesY = new HashSet<Edge>(); 
					for(int i = 0; i < eIndices.length; i++){
						edgesY.add(cn.getEdge(eIndices[i]));
					}
			        for(Edge e : edgesY){
			        	E.add(e);
			        	if(e.getSource() != y && !L.contains(e.getSource())) {L.add(e.getSource()); K.add(e.getSource());}  
						if(e.getTarget() != y && !L.contains(e.getTarget())) {L.add(e.getTarget()); K.add(e.getTarget());}
			        }
			    }
			    if(cn.getNodeCount() == L.size()){
			    	result.add(cn);
			    	return result;
			    }
			    else{
			    	int[] empty = {}; 
					CyNetwork next = Cytoscape.getRootGraph().createNetwork(empty, empty);
					for(Node m : L) { next.addNode(m); }
					for(Edge f: E) { next.addEdge(f); }
					result.add(next);
			    }
	    	}
	    }
	    return result;
	}
}
