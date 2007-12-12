/**
 * 
 */
package profile.graph;

import java.util.*;
/**
 * @author skillcoy
 *
 */
public class Node {

	private static int nodeIdIncrement = 0;
	
	private int id;
	private String name;
	private Set<Edge> edges;
	
	private void init(String arg) {
		id = nodeIdIncrement;
		name = (arg == null)? Integer.toString(id): arg;
		edges = new HashSet<Edge>();
		nodeIdIncrement++;
	}
	
	Node() {
		init(null);
	}
	
	Node(String arg) {
		init(arg);
	}
	
	public int getIndex() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	protected void addEdge(Edge e) {
		edges.add(e);
	}
	
	public Edge addEdge(Node target, boolean directed) {
		Edge e = new Edge(this, target);
		target.addEdge(e);
		this.addEdge(e);
		return e;
	}
	
	public void removeEdge(Edge e) {
		e.getSource().removeEdgeFromList(e);
		e.getTarget().removeEdgeFromList(e);
	}

	protected void removeEdgeFromList(Edge e) {
		edges.remove(e);
	}
	
	public List<Edge> getEdges() {
		return new LinkedList<Edge>(edges);
	}
	
	
}
