
package cytoscape.editor;

import cytoscape.Cytoscape;
import cytoscape.util.undo.CyAbstractEdit;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;

/**
 * An edit used by the Editor when adding new edges to a network.
 */
public class AddEdgeEdit extends CyAbstractEdit {

	// MLC 05/09/07:
	private static final long serialVersionUID = 2403924055921657412L;
	protected CyNetwork net;
	protected CyEdge edge;

	public AddEdgeEdit(CyNetwork net, CyEdge edge) {
		super("Add Edge");
		if ( net == null || edge == null )
			throw new IllegalArgumentException("network or edge is null");
		this.net = net;
		this.edge = edge;
	}

	public void undo() {
		super.undo();
		net.hideEdge( edge );
        Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED,
                                     CytoscapeEditorManager.CYTOSCAPE_EDITOR, net);
	}

	public void redo() {
		super.redo();
		net.restoreEdge( edge );
        Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED,
                                     CytoscapeEditorManager.CYTOSCAPE_EDITOR, net);
	}
}
