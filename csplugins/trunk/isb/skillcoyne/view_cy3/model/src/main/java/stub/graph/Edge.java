/**
 * 
 */
package stub.graph;

/**
 * @author skillcoy
 *
 */
public class Edge implements GraphObject {

	private static int nextId = 0;
	
	private int id;
	private String name;

	private Node source;
	private Node target;
	
	public Edge() {
		id = nextId;
		name = Integer.valueOf(id).toString();
		nextId++;
	}
	
	public Edge(String arg) {
		id = nextId;
		name = arg;
		nextId++;
	}
	
	public Edge(String arg, Node s, Node t) {
		id = nextId;
		name = arg;
		nextId++;
		source = s;
		target = t;
		
		s.addEdge(this);
		t.addEdge(this);
	}
	
	
	public void setSource(Node node) {
		source = node;
		source.addEdge(this);
	}
	
	public Node getSource() {
		return source;
	}
	
	public void setTarget(Node node) {
		target = node;
		target.addEdge(this);
	}
	
	public Node getTarget() {
		return target;
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

}
