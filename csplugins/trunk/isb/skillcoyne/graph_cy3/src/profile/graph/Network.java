/**
 * 
 */
package profile.graph;

import java.util.*;

/**
 * @author skillcoy
 *
 */
public class Network {
	
	private static int networkIdIncrement = 0;
	
	private int id;
	private String name;
	private TreeMap<Integer, Node> nodes;
	
	public Network() {
		id = networkIdIncrement;
		name = Integer.toBinaryString(id);
		nodes = new TreeMap<Integer, Node>();
		networkIdIncrement++;
	}

	public Network(String arg) {
		id = networkIdIncrement;
		name = arg;
		nodes = new TreeMap<Integer, Node>();
		networkIdIncrement++;
	}

	public int getIdentifier() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Node addNode(String arg) {
		Node n = new Node(arg);
		nodes.put(n.getIndex(), n);
		return n;
	}

	public void removeNode(Node n) {
		nodes.remove(n.getIndex());
	}
	
	/* --- bulk operations... --- */
	/**
 	 * Get the nodes in the listed range (by their creation order...ids)
 	 * @param min
 	 * @param max
 	 * @return
	 */
	public List<Node> getNodes(int min, int max) {
		return new LinkedList<Node>(nodes.subMap(min, max).values()); 
	}

	/**
	 * Get all nodes
	 * @return
	 */
	public List<Node> getNodes() {
		return new ArrayList<Node>(nodes.values());
	}
	
}
