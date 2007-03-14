
//============================================================================
// 
//  file: CompatibilityNode.java
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



package nct.networkblast;

import java.lang.*;
import java.util.*;
import java.util.logging.Logger;

import nct.graph.*;

/**
 */
public class CompatibilityNode<NodeType extends Comparable<? super NodeType>,
                            WeightType extends Comparable<? super WeightType>> 
	implements Comparable<CompatibilityNode<NodeType,WeightType>> {

	protected List<Graph<NodeType,WeightType>> graphList;
	protected List<NodeType> nodeList;

	public CompatibilityNode() {
		graphList = new ArrayList<Graph<NodeType,WeightType>>();
		nodeList = new ArrayList<NodeType>();
	}

	public boolean add(Graph<NodeType,WeightType> g, NodeType n) {

		// only one node per graph is allowed
		for ( int i = 0; i < graphList.size(); i++ ) 
			if ( graphList.get(i) == g ) 
				return false;


		// only add the pair if the node is a node in the graph
		if ( g.isNode(n) ) {
			graphList.add(g);
			nodeList.add(n);
			return true;
		} else {
			return false;
		}
	}

	public Graph<NodeType,WeightType> getGraph(NodeType n) {
		for ( int i = 0; i < nodeList.size(); i++ ) 
			if ( nodeList.get(i) == n )
				return graphList.get(i);

		return null;
	}

	public NodeType getNode(Graph<NodeType,WeightType> g) {
		for ( int i = 0; i < graphList.size(); i++ ) 
			if ( graphList.get(i) == g )
				return nodeList.get(i);
		return null;
	}

	public int getIndex(NodeType n) {
		for ( int i = 0; i < nodeList.size(); i++ ) 
			if ( nodeList.get(i) == n )
				return i;
		return -1;
	}

	public int getIndex(Graph<NodeType,WeightType> g) {
		for ( int i = 0; i < graphList.size(); i++ ) 
			if ( graphList.get(i) == g )
				return i;
		return -1;
	}

	public Collection<NodeType> getNodes() {
		return nodeList; 
	}

	public int compareTo(CompatibilityNode<NodeType,WeightType> c) {
		int thisHash = hashCode();
		int cHash = c.hashCode();

		if ( thisHash < cHash )
			return -1;
		else if ( thisHash == cHash )
			return 0;
		else
			return 1;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for ( int i = 0; i < nodeList.size(); i++ ) {
			sb.append( nodeList.get(i).toString() ); 
			sb.append( "|" ); 
		}
		sb.deleteCharAt(sb.length() - 1);
		
		return sb.toString();
	}
}
