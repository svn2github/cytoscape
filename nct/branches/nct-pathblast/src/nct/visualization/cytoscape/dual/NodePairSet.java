
//============================================================================
// 
//  file: NodePairSet.java
// 
//  Copyright (c) 2006, University of California San Diego 
// 
//  This program is free software; you can redistribute it and/or modify it 
//  under the terms of the GNU General Public License as published by the 
//  Free Software Foundation; either version 2 of the License, or (at your 
//  option) any later version.
//  
//  This program is distributed in the hope that it will be useful, but 
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
//  for more details.
//  
//  You should have received a copy of the GNU General Public License along 
//  with this program; if not, write to the Free Software Foundation, Inc., 
//  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// 
//============================================================================



package nct.visualization.cytoscape.dual;


import java.util.*;
import giny.model.Node;
/**
 * This class keeps track of a set of unordered pairs of nodes
 */
public class NodePairSet{
	private Map<Node,Set<Node>> node2NodeSet;
	public NodePairSet(){
		node2NodeSet = new HashMap<Node,Set<Node>>();
	}

	public void add(Node one,Node two){
		if(!isGreater(one,two)){
			Node temp = one;
			one = two;
			two = temp;
		}
		Set<Node> nodeSet = node2NodeSet.get(one);
		if(nodeSet == null){
			nodeSet = new HashSet<Node>();
			node2NodeSet.put(one,nodeSet);
		}

		nodeSet.add(two);
	}

	public boolean contains(Node one, Node two){
		if(!isGreater(one,two)){
			Node temp = one;
			one = two;
			two = temp;
		}
		Set<Node> nodeSet = node2NodeSet.get(one);
		if(nodeSet == null){
			return false;
		}
		else{
			return nodeSet.contains(two);
		}
	}
	public Map getOuterMap(){
		return node2NodeSet;
	}



	/**
	 * Determines the ordering of nodes for the purpose of this nodePari map.
	 * Since we are interested in unorder paris, we have to know which one out of
	 * the pair to look up first (so that getCount(one,two)==getCount(two,one)))
	 */
	private boolean isGreater(Node one, Node two){
		int hash1 = one.hashCode();
		int hash2 = two.hashCode();
		if(hash1<hash2){
			return false;				
		}
		else if(hash1 == hash2){
			//I'm betting most of this code is never executed, since the default hashCode of object
			//should do a pretty thourough job of distinguishing between the two
			String ident1 = one.toString();
			String ident2 = two.toString();
			if(ident1.equals(ident2)){
				throw new IllegalArgumentException("Members of node pair not distinct");
			}
			else if(ident1.compareTo(ident2)<0){
				return false;				
			}
		}
		return true;
	}

}
