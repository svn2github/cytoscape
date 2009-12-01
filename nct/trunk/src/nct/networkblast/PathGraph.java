

//============================================================================
// 
//  file: PathGraph.java
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

import java.util.*;
import java.lang.*;
import nct.graph.*;


public class PathGraph<NodeType extends Comparable<? super NodeType>>
	implements Graph<NodeType,Double>, 
	           Comparable<Graph<NodeType,Double>>, 
		   Cloneable  {

	List<NodeType> nodes;
	Double score;

	public PathGraph( NodeType n ) {
		nodes = new ArrayList<NodeType>();
		nodes.add(n);
		score = Double.NEGATIVE_INFINITY;
	}

	public boolean addNode(NodeType node) {
		nodes.add( node );
		return true;
	}

	public boolean addEdge(NodeType nodeA, NodeType nodeB, Double weight) { 
		return false;
	}

	public boolean addEdge(NodeType nodeA, NodeType nodeB, Double weight, String desc) {
		return false; 
	}

	public boolean isEdge(NodeType nodeA, NodeType nodeB) {
		return false;
	}

	public boolean isNode(NodeType node) {
		if ( nodes.indexOf( node ) > -1 )
			return true;
		else
			return false;
	}

	public Double getEdgeWeight(NodeType nodeA, NodeType nodeB) {
		return 0.0; 
	}

	public Set<NodeType> getNodes() {
		HashSet<NodeType> ns = new HashSet<NodeType>();
		ns.addAll( nodes );
		return ns;
	}

	public List<NodeType> getNodeList() {
		return Collections.unmodifiableList(nodes);
	}

	public Set<NodeType> getNeighbors(NodeType node) {
		HashSet<NodeType> ns = new HashSet<NodeType>();
		int ind = nodes.indexOf( node );
		if ( ind < 0 )
			return null;

		if ( ind > 0 )
			ns.add( nodes.get( ind - 1 ) );

		if ( ind < (nodes.size()-1) )
			ns.add( nodes.get( ind + 1 ) );

		return ns;
	}

	public String getId() {
		return "asdfasdf";
	}

	public int numberOfNodes() {
		return nodes.size();
	}

	public int numberOfEdges() {
		return nodes.size() - 1;
	}

	public int degreeOfNode(NodeType node) {
		if ( nodes.get(0).compareTo( node ) == 0 ||
		     nodes.get(nodes.size()-1).compareTo( node ) == 0 )
		     	return 1;
		else
			return 2;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double f) {
		score = f;
	}

	public int compareTo(Graph<NodeType,Double> g) {
		if ( score < g.getScore() ) {
			return -1;
		} else if ( score > g.getScore() ) {
			return 1;
		} else { 
			if ( numberOfNodes() < g.numberOfNodes() )
				return -1;
			else if ( numberOfNodes() > g.numberOfNodes() )
				return 1;
			else
				return 0;	
		}
	}

	public String getEdgeDescription(NodeType nodeA, NodeType nodeB) {
		return "(none)";
	}

	public boolean setEdgeDescription(NodeType nodeA, NodeType nodeB, String desc) {
		return false;
	}

	public Edge<NodeType,Double> getEdge(NodeType nodeA, NodeType nodeB) {
		return fastGetEdge(nodeA,nodeB,nodes.indexOf(nodeA));
	}

	private Edge<NodeType,Double> fastGetEdge(NodeType nodeA, NodeType nodeB, int i) {
		return new BasicEdge<NodeType,Double>(nodeA,nodeB,0.0,"asdf");	
	}

	public Set<Edge<NodeType,Double>> getEdges() {
		Set<Edge<NodeType,Double>> es = new HashSet<Edge<NodeType,Double>>();
		es.addAll( getEdgeList() );
		return es;
	}

	public List<Edge<NodeType,Double>> getEdgeList() {
		List<Edge<NodeType,Double>> el = new ArrayList<Edge<NodeType,Double>>(nodes.size()-1);
		for ( int i = 0; i < nodes.size()-1; i++ ) 
			el.add(fastGetEdge(nodes.get(i), nodes.get(i+1), i));
		return el;
	}

	public Object clone() {
		PathGraph<NodeType> fg = new PathGraph<NodeType>( nodes.get(0) );
		for ( int i = 1; i < nodes.size(); i++ ) {
			fg.nodes.add(nodes.get(i));
		}	
		fg.score = score;
		return fg;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append( "# score: ");
		sb.append( score.toString() );
		sb.append( "\n" ); 
		for ( int i = 0; i < nodes.size()-1; i++ ) {
			sb.append("source: ");
			sb.append(nodes.get(i));
			sb.append("  target: ");
			sb.append(nodes.get(i+1));
			sb.append( "\n" ); 
		}
		return sb.toString(); 
	}

	public boolean removeEdge(NodeType nodeA, NodeType nodeB) {
		return false;
	}

	public boolean removeNode(NodeType node) {
		return false;
	}

	public boolean setEdgeWeight(NodeType nodeA, NodeType nodeB, Double weight) {
		return false;
	}
}
