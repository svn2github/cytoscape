
package cytoscape.editor;

import cytoscape.Cytoscape;
import cytoscape.util.undo.CyAbstractEdit;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.GraphView;
import org.cytoscape.view.NodeView;

/**
 * An edit used by the Editor when adding new nodes to a network.
 */
public class AddNodeEdit extends CyAbstractEdit {

	// MLC 05/09/07:
	private static final long serialVersionUID = 4869581496913388294L;
	protected CyNetwork net;
	protected CyNode node;
	protected double xPos = 0.0;
	protected double yPos = 0.0;

	public AddNodeEdit(CyNetwork net, CyNode node) {
		super("Add Node");
		if ( net == null || node == null )
			throw new IllegalArgumentException("network or node is null"); 
		this.net = net;
		this.node = node;

		GraphView view = Cytoscape.getNetworkView(net.getIdentifier());
		if ( view != null || view != Cytoscape.getNullNetworkView() ) {
			NodeView nv = view.getNodeView(node);
			xPos = nv.getXPosition(); 
			yPos = nv.getYPosition(); 
		}
	}

	public void undo() {
		super.undo();
		net.hideNode( node );
		Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED,
		                             CytoscapeEditorManager.CYTOSCAPE_EDITOR, net);
	}

	public void redo() {
		super.redo();
		net.restoreNode( node );

		GraphView view = Cytoscape.getNetworkView(net.getIdentifier());
		if ( view != null || view != Cytoscape.getNullNetworkView() ) {
			NodeView nv = view.getNodeView(node);
			nv.setXPosition(xPos);
			nv.setYPosition(yPos);
		}

		Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED,
		                             CytoscapeEditorManager.CYTOSCAPE_EDITOR, net);
	}
}
