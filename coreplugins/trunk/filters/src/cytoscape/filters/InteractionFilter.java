
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
import cytoscape.Cytoscape;
import csplugins.quickfind.util.QuickFind;
import csplugins.widgets.autocomplete.index.GenericIndex;


public class InteractionFilter extends CompositeFilter {
	public static int NODE_UNDEFINED = -1;
	public static int NODE_SOURCE = 0;
	public static int NODE_TARGET = 1;
	public static int NODE_SOURCE_TARGET = 3;
	
	protected int nodeType = NODE_SOURCE;

	protected boolean isSourceChecked = true;
	protected boolean isTargetChecked = true;
	
	protected CompositeFilter passFilter = null;
	
	public InteractionFilter() {
	}

	public InteractionFilter(String pName) {
		name = pName;
	}

	public void setPassFilter(CompositeFilter pFilter) {
		passFilter = pFilter;
		childChanged = true;
	}

	public CompositeFilter getPassFilter() {
		return passFilter;
	}

	public int getNodeType() {
		return nodeType;
	}

	public void setNodeType(int pNodeType) {
		if (nodeType == pNodeType) {
			return;
		}
		nodeType = pNodeType;
		childChanged = true;
	}

	public boolean isSorceChecked() {
		return isSourceChecked;
	}
	
	public boolean isTargetChecked() {
		return isTargetChecked;
	}
	
	public void setSorceChecked(boolean pIsChecked) {
		isSourceChecked =pIsChecked;
		updateNodeType();		
	}

	public void setTargetChecked(boolean pIsChecked) {
		isTargetChecked =pIsChecked;
		updateNodeType();		
	}
	
	private void updateNodeType() {
		//update nodeType
		if (isSourceChecked && isTargetChecked) {
			nodeType = NODE_SOURCE_TARGET;
		}
		else if (isSourceChecked) {
			nodeType = NODE_SOURCE;
		}
		else if (isTargetChecked) {
			nodeType = NODE_TARGET;
		}
		else {
			nodeType = NODE_UNDEFINED;
		}
	}
	
	
	public BitSet getNodeBits() {
		apply();
		return node_bits;
	}
	
	public BitSet getEdgeBits(){
		apply();
		return edge_bits;		
	}
	
	
	public void apply() {
		if ( !childChanged ) 
			return;

		//Make sure the pass filter is current
		if (passFilter == null) {
			passFilter = new InteractionFilter("None");
		}
		
		if (!passFilter.getName().equalsIgnoreCase("None")) {
			passFilter.setNetwork(network);
			passFilter.apply();			
		}	

		List<Node> nodes_list = null;
		List<Edge> edges_list=null;

		int objectCount = -1;
		
		if (advancedSetting.isNodeChecked()) {
			nodes_list = network.nodesList();
			objectCount = nodes_list.size();
			node_bits = new BitSet(objectCount); // all the bits are false at very beginning
			
			for (int i=0; i<objectCount; i++) {
				if (isHit(nodes_list.get(i))) {
					node_bits.set(i);
				}
			}
		}
		else if (advancedSetting.isEdgeChecked()) {
			edges_list = network.edgesList();
			objectCount = edges_list.size();
			edge_bits = new BitSet(objectCount); // all the bits are false at very beginning			

			for (int i=0; i<objectCount; i++) {
				if (isHit(edges_list.get(i))) {
					edge_bits.set(i);
				}
			}
		}
		else {
			System.out.println("TopologyFilter: objectType is undefined.");
			return;
		}

		if (negation) {
			if (advancedSetting.isNodeChecked()) {
				node_bits.flip(0, objectCount);
			}
			if (advancedSetting.isEdgeChecked()) {
				edge_bits.flip(0, objectCount);
			}
		}

		childChanged = false;
	}

	
	private boolean isHit(Object pObj) {
		/*
		// Get all the neighbors for pNode that pass the given filter
		HashSet neighborSet = new HashSet();
		getNeighbors(pObj, neighborSet, withinDistance);
		
		//Exclude self from the neighbor
		if (neighborSet.contains(pObj)) {
			neighborSet.remove(pObj);
		}

		// remove all the neighbors that do not pass the given filter
		if (!passFilter.getName().equalsIgnoreCase("None")) {
			Object [] nodeArray = neighborSet.toArray();
			for (int i=0; i< nodeArray.length; i++) {
				int nodeIndex = network.nodesList().indexOf(nodeArray[i]);

				if (!passFilter.getNodeBits().get(nodeIndex)) {
					neighborSet.remove(nodeArray[i]);
				}
			}							
		}
		if (neighborSet.size() < minNeighbors) {
			return false;
		}
		*/
		return true;
	}
	
	// Get all the neighbors for pNode within pDistance
	private void getNeighbors(Object pObj, HashSet pNeighborSet, int pDistance) {
		if (pDistance == 0) {
			if (!pNeighborSet.contains(pObj)) {
				pNeighborSet.add(pObj);
			}
			return;
		}
		
		List neighbors = network.neighborsList((Node)pObj);

		Iterator nodeIt = neighbors.iterator();
		while (nodeIt.hasNext()) {
			Node nextNode = (Node) nodeIt.next();

			if (!pNeighborSet.contains(nextNode)) {
				pNeighborSet.add(nextNode);
			}
			getNeighbors(nextNode, pNeighborSet, pDistance-1);
		}
	}
	
	
	public String toString() {
		String retStr = "<InteractionFilter>\n";
		
		retStr = retStr + "name=" + name + "\n";
		retStr = retStr + advancedSetting.toString() + "\n";
		retStr = retStr + "Negation=" + negation + "\n";
		retStr = retStr + "nodeType=" + nodeType + "\n";

		if (passFilter == null) {
			retStr += "passFilter=null\n";			
		}
		else {
			retStr += "passFilter=" + passFilter.getName()+"\n";						
		}
		
		retStr += "</InteractionFiler>";

		return retStr;
	}
	
	public void setNodeBits(BitSet b) {
		node_bits = b;
		//parent.childChanged();
	}

	public void setEdgeBits(BitSet b) {
		edge_bits = b;
		//parent.childChanged();
	}

	public void setParent(CyFilter p) {
		parent = p;
	}
	public CyFilter getParent() {
		return parent;
	}
		
	public void setNegation(boolean pNot) {
		negation = pNot;
		//getParent().childChanged();
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
		if (network != null && network == pNetwork) {
			return;
		}
		network = pNetwork;
		if (passFilter != null) {
			passFilter.setNetwork(network);			
		}

		childChanged();
	}
}
