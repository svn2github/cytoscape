/**
 * 
 */
package profile.graph;

/**
 * @author skillcoy
 *
 */
public class Edge {

	
	private static int edgeIdIncrement = 0;
	
	private int id;
	private String name;
	
	private Node source;
	private Node target;
	private boolean directed = false;
	
	private void init(Node s, Node t, String arg) {
		id = edgeIdIncrement;
		name = (arg == null)? Integer.toString(id): arg;
		source = s;
		target = t;
		edgeIdIncrement++;
	}
	
	Edge(Node s, Node t) {
		init(s, t, null);
	}
	
	Edge(String arg, Node s, Node t) {
		init(s, t, arg);
	}
	
	public int getIndex() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public Node getSource() {
		return source;
	}
	
	public Node getTarget() {
		return target;
	}
	
	public boolean isDirected() {
		return directed;
	}
}
