
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

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


public class TopologyFilter extends CompositeFilter {
	
	private int minNeighbors = 1;
	private int withinDistance = 1;
	private CompositeFilter passFilter = null;
	
	public TopologyFilter() {
        super.advancedSetting.setNode(true);
	}

	public TopologyFilter(String pName) {
		name = pName;
        super.advancedSetting.setNode(true);
	}

	public void setPassFilter(CompositeFilter pFilter) {
		passFilter = pFilter;
		childChanged = true;
	}

	public CompositeFilter getPassFilter() {
		return passFilter;
	}

	public void setMinNeighbors(int pNeighbors) {
		minNeighbors = pNeighbors;
		childChanged = true;
	}

	public int getMinNeighbors() {
		return minNeighbors;
	}

	public void setDistance(int pDistance) {
		withinDistance = pDistance;
		childChanged = true;
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
	
	
	public void apply() {
		if ( !childChanged ) 
			return;

		if (network == null) {
			return;
		}

		//Make sure the pass filter is current
		if (passFilter == null) {
			passFilter = new TopologyFilter("None");
		}

		if (!passFilter.getName().equalsIgnoreCase("None")) {
			passFilter.setNetwork(network);
			passFilter.apply();                     
		}       

		List<CyNode> nodes_list = null;
		List<CyEdge> edges_list=null;

		int objectCount = -1;

		if (advancedSetting.isNodeChecked()) {
			nodes_list = network.nodesList();
			objectCount = nodes_list.size();

			//Create an index mapping between RootGraphIndex and index in current network
			HashMap<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
			Object [] nodeArray = network.nodesList().toArray();
			int rootgraphIndex = -1;
			for (int i=0; i<objectCount; i++) {
				rootgraphIndex = network.getIndex((CyNode)nodeArray[i]);
				indexMap.put(Integer.valueOf(rootgraphIndex), Integer.valueOf(i));
			}

			//
			node_bits = new BitSet(objectCount); // all the bits are false at very beginning

			for (int i=0; i<objectCount; i++) {
				if (isHit(nodes_list.get(i), indexMap)) {
					node_bits.set(i);
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
		}

		childChanged = false;
	}

	
	private boolean isHit(Object pObj, HashMap<Integer, Integer> pIndexMap) {
		// Get all the neighbors for pNode that pass the given filter
		HashSet neighborSet = new HashSet();
		getNeighbors(pObj, neighborSet, withinDistance);

		//Exclude self from the neighbor
		if (neighborSet.contains(pObj)) {
			neighborSet.remove(pObj);
		}

		// Obviously, this does not meet the criteria, don't do extra work 
		if (neighborSet.size() < minNeighbors) {
			return false;
		}

		// remove all the neighbors that do not pass the given filter
		if (!passFilter.getName().equalsIgnoreCase("None")) {
			Object [] nodeArray = neighborSet.toArray();
			for (int i=0; i< nodeArray.length; i++) {
				//int nodeIndex = network.nodesList().indexOf(nodeArray[i]); //This works, but very slow
				int rootgraphIndex = network.getIndex((CyNode)nodeArray[i]);
				int nodeIndex = pIndexMap.get(Integer.valueOf(rootgraphIndex)).intValue();                          

				if (!passFilter.getNodeBits().get(nodeIndex)) {
					neighborSet.remove(nodeArray[i]);
				}                               
			}                                                       
		}

		if (neighborSet.size() < minNeighbors) {
			return false;
		}

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
		
		List neighbors = network.neighborsList((CyNode)pObj);

		Iterator nodeIt = neighbors.iterator();
		while (nodeIt.hasNext()) {
			CyNode nextNode = (CyNode) nodeIt.next();

			if (!pNeighborSet.contains(nextNode)) {
				pNeighborSet.add(nextNode);
			}
			getNeighbors(nextNode, pNeighborSet, pDistance-1);
		}
	}
	
	
	public String toString() {
		String retStr = "<TopologyFilter>\n";
		
		retStr = retStr + "name=" + name + "\n";
		retStr = retStr + advancedSetting.toString() + "\n";
		retStr = retStr + "Negation=" + negation + "\n";
		retStr = retStr + "minNeighbors=" + minNeighbors + "\n";
		retStr = retStr + "withinDistance=" + withinDistance + "\n";

		if (passFilter == null) {
			retStr += "passFilter=null\n";			
		}
		else {
			retStr += "passFilter=" + passFilter.getName()+"\n";						
		}
		
		retStr += "</TopologyFiler>";

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
