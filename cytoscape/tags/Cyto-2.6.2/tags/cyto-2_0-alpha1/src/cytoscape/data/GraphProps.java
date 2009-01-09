package cytoscape.data;

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/


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
	//if (name == null) {
	//  System.out.println("ERROR: getName for edge was called before name attribute was initialized");
	//}
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


