package cytoscape.hyperedge.editor.actions;

import cytoscape.hyperedge.HyperEdgeFactory;
import cytoscape.hyperedge.impl.HyperEdgeManagerImpl;
import cytoscape.Cytoscape;

import cytoscape.editor.actions.DeleteAction;

import giny.model.GraphObject;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


/**
 * Subclass of DeleteAction that also hides HyperEdge ConnectorNodes.
 * This action is used when the menu item 'Delete Selected Nodes and Edges'
 * is performed, or use of the delete key.
 * NOTE: If HyperEdges are added to the Cytoscape core, this class can
 * be removed.
 */
public class HyperEdgeDeleteAction extends DeleteAction {
    // ///////////////////////////////////////////////////////////
    private static final long serialVersionUID = 7106699897489080982L;

    /**
    * action for deleting selected Cytoscape nodes and edges
    */
    public HyperEdgeDeleteAction() {
        super();
	// MLC 01/09/07:
	// We seem to need to redefine the accelerator so
	// it will use the HyperEdgeDeleteAction versus the DeleteAction:
	setAcceleratorCombo(KeyEvent.VK_DELETE, 0);
    }

    /**
     * perform deletion on the input object. if object is a Node, then this will
     * result in also deleting the edges adjacent to the node
     *
     * @param obj
     *            the object to be deleted
     */
    public HyperEdgeDeleteAction(GraphObject obj) {
        super(obj);
	// MLC 01/09/07:
	// We seem to need to redefine the accelerator so
	// it will use the HyperEdgeDeleteAction versus the DeleteAction:
	setAcceleratorCombo(KeyEvent.VK_DELETE, 0);
    }

    // ////////////////////////////////////////////////////////////////

    // overrides DeleteAction.actionPerformed:
    public void actionPerformed(ActionEvent ae) {
        super.actionPerformed(ae);
        // NOTE: Because this may go away if HyperEdge's are placed in
        // the core, we don't make this a part of the public
        // interface:
        ((HyperEdgeManagerImpl) HyperEdgeFactory.INSTANCE.getHyperEdgeManager()).hideConnectorNodes(Cytoscape.getCurrentNetworkView()
                                                                                                             .getNetwork());
    }
}
