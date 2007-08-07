// not sure this is the package this class should be in
package cytoscape.data.readers;

import y.base.*;
import y.view.*;
import y.io.YGFIOHandler;
import y.io.GMLIOHandler;

import cytoscape.GraphObjAttributes;
import cytoscape.data.Interaction;
import cytoscape.data.GraphProps;
/**
 * GMLWriter should be called when a GML file is saved.
 *
 * @author namin@mit.edu
 */
public class GMLWriter {
    GraphProps props;
    
    public GMLWriter(GraphProps props) {
	this.props = props;
    }

    public void write(String filename) {
	GMLIOHandler ioh = new GMLIOHandler ();
	Graph2D graph = props.getGraph();
	// Node labels should record the orf/id name.
	for (NodeCursor nc = graph.nodes(); nc.ok(); nc.next()) {
	    Node node = nc.node();
	    String name = props.getName(node);
	    graph.setLabelText(node, name);
	}
	// Edge labels should record the interaction type.
	for(EdgeCursor ec = graph.edges(); ec.ok(); ec.next()) {
	    Edge edge = ec.edge();
	    String type = props.getType(edge);
	    graph.setLabelText(edge, type);
	}

	ioh.write (graph,filename);

	// Erase the edge labels.
	for(EdgeCursor ec = graph.edges(); ec.ok(); ec.next()) {
	    Edge edge = ec.edge();
	    graph.setLabelText(edge, null);
	}
    }
}
