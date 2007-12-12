/**
 * 
 */
package stub.graph;

/**
 * @author skillcoy
 *
 */
public class Node implements GraphObject {

	private static int nextId = 0;
	
	private int id;
	private String name;

	private java.util.List<Edge> edges;
	
	
	public Node() {
		id = nextId;
		name = Integer.valueOf(id).toString();
		nextId++;
		edges = new java.util.ArrayList<Edge>();
	}
	
	public Node(String arg) {
		id = nextId;
		name = arg;
		nextId++;
		edges = new java.util.ArrayList<Edge>();
	}
	
	/* (non-Javadoc)
	 * @see test.graph.GraphObject#getIdentifier()
	 */
	public int getIdentifier() {
		return id;
	}

	/* (non-Javadoc)
	 * @see test.graph.GraphObject#getName()
	 */
	public String getName() {
		return name;
	}
	
	public void addEdge(Edge edge) {
		edges.add(edge);
	}

	public java.util.List<Edge> getEdges() {
		return edges;
	}
}
