package cytoscape.data;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import y.base.*;
import y.view.*;

import cytoscape.data.GraphProps;
/**
 * A structure to store KEGG Pathways
 * <p>
 * It contains tables accessible by IDs of:
 * <ul>
 *  <li>Descriptions</li>
 *  <li>Lists of Node Names</li>
 * </ul>
 *
 * @author namin@mit.edu
 * @version 2002-04-24
 */
public class KeggPathways {
    Hashtable descs;
    Hashtable lists;
    Hashtable node2pathways;

    public KeggPathways() {
	descs = new Hashtable();
	lists = new Hashtable();
	node2pathways = null;
    }

    public void add(String id, String desc, Vector list) {
	descs.put(id, desc);
	lists.put(id, list);
    }

    /**
     * Returns the description of a pathway, given its id.
     */
    public String getPathwayDesc(String id) {
	String desc = (String) descs.get(id);
	return desc;
    }

    /**
     * Returns all the pathways in which a given node is involved
     */
    public Vector ofNode(String nodeName) {
	if (node2pathways == null) {
	    // first initialization
	    initByNodes();
	    return ofNode(nodeName);
	} else {
	    Vector lst = (Vector) node2pathways.get(nodeName);
	    if (lst == null) {
		lst = new Vector();
	    }
	    return lst;
	}
    }

    public String ofNodeConciseDesc(String nodeName) {
	Vector lst = ofNode(nodeName);
	String s = "";
	if (lst.size() > 0) {
	    s = conciseDesc(lst);
	}
	return s;
    }

    /**
     * Returns a concise string given a vector of pathways
     */
    public String conciseDesc(Vector lst) {
	String s = "";
	boolean first = true;
	for (Enumeration e = lst.elements(); e.hasMoreElements(); ) {	
	    String id = (String) e.nextElement();
	    String desc = (String) descs.get(id);
	    if (!first) {
		s += ", ";
	    }
	    s += desc;
	}
	return s;
    }

    private void initByNodes() {
	node2pathways = new Hashtable();
	for (Enumeration e = descs.keys(); e.hasMoreElements(); ) {
	    String id = (String) e.nextElement();
	    Vector list = (Vector) lists.get(id);

	    for (Enumeration re = list.elements(); re.hasMoreElements(); ) {
		String node = (String) re.nextElement();
		Vector idList = (Vector) node2pathways.get(node);
		if (idList == null) {
		    idList = new Vector();
		}
		idList.add(id);
		node2pathways.put(node, idList);
	    }
	}
    }

    /**
     * Assuming that all pathways only list reaction nodes,
     * this extends the pathway to all the neighboring genes and compounds.
     * ONLY USED ONCE TO COMPLETE THE PATHWAYS
     */
    public void buildCompletePathways(GraphProps props) {
	Graph2D graph = props.getGraph();
	// hash of reaction names to reaction nodes
	Hashtable name2node = new Hashtable();
	// fill in the name 2 node hash
	for (NodeCursor nc = graph.nodes(); nc.ok(); nc.next()) {
	    Node node = nc.node();
	    String name = props.getName(node);
	    // even though only reaction nodes are needed,
	    // nodes of all types are stored
	    name2node.put(name, node);
	}

	for (Enumeration e = descs.keys(); e.hasMoreElements(); ) {
	    String id = (String) e.nextElement();
	    String desc = (String) descs.get(id);
	    Vector list = (Vector) lists.get(id);

	    // find the neighbors of every reaction in the list
	    Vector full = new Vector();
	    for (Enumeration re = list.elements(); re.hasMoreElements(); ) {
		String reactionName = (String) re.nextElement();
		Node reactionNode = (Node) name2node.get(reactionName);

		full.add(reactionName);
		if (reactionNode != null) {
		    for (EdgeCursor ec = reactionNode.edges(); ec.ok(); ec.next()) {
			Edge edge = ec.edge();
			Node neighborNode = edge.opposite(reactionNode);
			String neighborName = props.getName(neighborNode);
			if (!full.contains(neighborName)) {
			    full.add(neighborName);
			}
		    }
		} else {
		    // reaction not found
		    System.out.println("Attention! Reaction " + reactionName + " was not found.");
		}

		// update the hash
		lists.put(id, full);
	    }
	}
    }

    public void print() {
	System.out.println("Printing pathways");
	for (Enumeration e = descs.keys(); e.hasMoreElements(); ) {
	    String id = (String) e.nextElement();
	    String desc = (String) descs.get(id);
	    Vector list = (Vector) lists.get(id);

	    String line = desc + "\t" + id + list;
	    System.out.println(line);
	}
	System.out.println("Done with printing pathways");
    }
}
