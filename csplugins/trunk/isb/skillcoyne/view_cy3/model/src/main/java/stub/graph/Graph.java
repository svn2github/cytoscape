/**
 * 
 */
package stub.graph;

/**
 * @author skillcoy
 *
 */
public class Graph {//implements GraphObject {
	private static int nextId = 0;
	
	private int id;
	private String name;
	
	private java.util.Set<Node> nodes;
	private java.util.Set<Edge> edges;

	
	public Graph() {
		id = nextId;
		name = Integer.valueOf(id).toString();
		nextId++;
		nodes = new java.util.HashSet<Node>();
		edges = new java.util.HashSet<Edge>();
	}

	
	public Graph(String arg) {
		id = nextId;
		name = arg;
		nextId++;
		nodes = new java.util.HashSet<Node>();
		edges = new java.util.HashSet<Edge>();
	}
	
	public int getIdentifier() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void addNode(Node node) {
		nodes.add(node);
	}
	
	public void addEdge(Edge edge) {
		edges.add(edge);
	}
	
	public java.util.List<Node> getNodes() {
		return new java.util.ArrayList<Node>(nodes);
	}
	
	public java.util.List<Edge> getEdges() {
		return new java.util.ArrayList<Edge>(edges);
	}
	
	public boolean contains(Node node) {
		return nodes.contains(node);
	}
	
	public boolean contains(Edge edge) {
		return edges.contains(edge);
	}
	
}
