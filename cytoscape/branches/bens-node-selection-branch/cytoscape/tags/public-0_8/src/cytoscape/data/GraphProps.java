package cytoscape.data;

import y.base.*;
import y.view.*;

import cytoscape.GraphObjAttributes;
/**
 * Graph Utilities Class.
 *
 * @author namin@mit.edu
 */
public class GraphProps {
    private Graph2D graph;
    private GraphObjAttributes nodeAttributes;
    private GraphObjAttributes edgeAttributes;
    
    public GraphProps(Graph2D graph, 
		      GraphObjAttributes nodeAttributes, GraphObjAttributes edgeAttributes) {
	this.graph = graph;
	this.nodeAttributes = nodeAttributes;
	this.edgeAttributes = edgeAttributes;
    }

    public GraphProps(Graph2D graph) {
	this.graph = graph;
	this.nodeAttributes = new GraphObjAttributes();
	this.edgeAttributes = new GraphObjAttributes();
    }

    public Graph2D getGraph() {
	return graph;
    }

    public GraphObjAttributes getNodeAttributes() {
	return nodeAttributes;
    }

    public GraphObjAttributes getEdgeAttributes() {
	return edgeAttributes;
    }

    /*
    public void initNames() {
	nodeAttributes.initNames(graph);
	edgeAttributes.initNames(graph);
    }

    public void setName(Node node, String name) {
	nodeAttributes.setName(node, name);
    }

    public void setName(Edge edge, String name) {
	edgeAttributes.setName(edge, name);
    }
    */

    public String getName(Node node) {
	String name = nodeAttributes.getCanonicalName(node);
	if (name == null) {
	    // use node label as name
	    Graph2DView gView = new Graph2DView(graph);
	    name = graph.getLabelText(node);
	}
	return name;
    }

    public String getName(Edge edge) {
	String name = edgeAttributes.getCanonicalName(edge);
	if (name == null) {
	    System.out.println("ERROR: getName for edge was called before name attribute was initialized");
	}
	return name;
    }

    public void setType(Edge edge, String type) {
	edgeAttributes.add("interaction", getName(edge), type);
    }

    public String getType(Edge edge) {
	String type = (String) edgeAttributes.getValue("interaction", 
						       getName(edge));

	return type;
    }

    public String getType(Node node) {
	// Infer the type from the node name
	String name = getName(node);
	if (name != null) {
	    char s = name.charAt(0);
	    if (s == 'C') {
		return "compound";
	    } else if (s == 'R') {
		return "reaction";
	    }
	}
	// default
	return "gene";
    }
}
