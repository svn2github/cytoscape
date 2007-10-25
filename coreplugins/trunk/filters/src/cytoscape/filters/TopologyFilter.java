
/*
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

package cytoscape.filters;

import giny.model.Edge;
import giny.model.Node;

import java.util.*;

import cytoscape.CyNetwork;
import csplugins.quickfind.util.QuickFind;
import csplugins.widgets.autocomplete.index.GenericIndex;


public class TopologyFilter extends CompositeFilter {

	protected BitSet node_bits = null;
	protected BitSet edge_bits = null;

	protected CyFilter parent;
	
	protected String name; // Name of the filter

	protected boolean negation = false;
	protected CyNetwork network = null;
	
	protected int minNeighbors = 1;
	protected int withinDistance = 1;
	
	protected CompositeFilter passFilter = null;
	
	
	public TopologyFilter() {
	}

	public TopologyFilter(String pName) {
		name = pName;
	}

	public void setPassFilter(CompositeFilter pFilter) {
		passFilter = pFilter;
	}

	public CompositeFilter getPassFilter() {
		return passFilter;
	}

	public void setMinNeighbors(int pNeighbors) {
		minNeighbors = pNeighbors;
	}

	public int getMinNeighbors() {
		return minNeighbors;
	}

	public void setDistance(int pDistance) {
		withinDistance = pDistance;
	}

	public int getDistance() {
		return withinDistance;
	}

	public BitSet getNodeBits() {
		apply();
		return node_bits;
	}
	
	public BitSet getEdgeBits(){
		apply();
		return edge_bits;		
	}
	
	public boolean passesFilter(Object obj) {
		List<Node> nodes_list = null;
		List<Edge> edges_list=null;

		int index = -1;
		if (obj instanceof Node) {
			nodes_list = network.nodesList();
			index = nodes_list.lastIndexOf((Node) obj);	
			return node_bits.get(index);			
		}
		
		if (obj instanceof Edge) {
			edges_list = network.edgesList();
			index = edges_list.lastIndexOf((Edge) obj);	
			return edge_bits.get(index);			
		}		
		
		return false;
	}

	
	public void apply() {
		System.out.println("TopologyFilter.apply()");
	}
	
	public String toString() {
		if (passFilter == null) {
			return "TopologyFilter:"+ name+ ":"+ minNeighbors + ":" + withinDistance + ":null";						
		}
		else {
			return "TopologyFilter:"+ name+ ":"+ minNeighbors + ":" + withinDistance + ":" + passFilter.getName();			
		}
	}
	
	public void setNodeBits(BitSet b) {
		node_bits = b;
		parent.childChanged();
	}

	public void setEdgeBits(BitSet b) {
		edge_bits = b;
		parent.childChanged();
	}

	public void setParent(CyFilter p) {
		parent = p;
	}
	public CyFilter getParent() {
		return parent;
	}
	
	// an atomic filter can't have any children, so this is a no-op
	public void childChanged() {}; 
	
	public void setNegation(boolean pNot) {
		negation = pNot;
		getParent().childChanged();
	}
	public boolean getNegation() {
		return negation;
	}

	public String getName(){
		return name;
	}
	public void setName(String pName){
		name = pName;
	}

	public void setNetwork(CyNetwork pNetwork) {
		network = pNetwork;
	}
}
