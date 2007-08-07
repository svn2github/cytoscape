package cytoscape.filters;

import y.base.*;
import y.view.*;

import cytoscape.undo.UndoableGraphHider;

import cytoscape.data.*;

/**
 * Dummy filter that never flags.
 * 
 * @author namin@mit.edu
 * @version 2002-03-02
 */
public class TrueFilter extends Filter {

    public TrueFilter(Graph2D graph) {
	super(graph);
    }

    public NodeList get(NodeList hidden) {
	return new NodeList();
    }

}
