
package cytoscape.internal.actions;

import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_LOCATION;

import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

import org.cytoscape.util.swing.CyAbstractEdit;


/**
 * An undoable edit that will undo and redo deletion of nodes and edges.
 */ 
class DeleteEdit extends CyAbstractEdit {

	private static final long serialVersionUID = -1164181258019250610L;
	Set<CyNode> nodes;
	Set<CyEdge> edges;
	double[] xPos;
	double[] yPos;
	CySubNetwork net;
	DeleteAction deleteAction;
	CyNetworkManager netmgr;
	
	DeleteEdit(CySubNetwork net, Set<CyNode> nodes, Set<CyEdge> edges,	DeleteAction deleteAction, CyNetworkManager netmgr) {
		super("Delete");
		this.deleteAction = deleteAction;
		if ( net == null )
			throw new IllegalArgumentException("network is null");
		this.net = net;

		if ( nodes == null )
			throw new IllegalArgumentException("nodes is null");
		this.nodes = nodes; 

		if ( edges == null )
			throw new IllegalArgumentException("edges is null");
		this.edges = edges; 

		// save the positions of the nodes
		xPos = new double[nodes.size()]; 
		yPos = new double[nodes.size()]; 
		CyNetworkView netView = netmgr.getNetworkView(net.getSUID());
		if ( netView != null ) {
			int i = 0;
			for ( CyNode n : nodes ) {
				View<CyNode> nv = netView.getNodeView(n);
				xPos[++i] = nv.getVisualProperty(NODE_X_LOCATION);
				yPos[i] = nv.getVisualProperty(NODE_Y_LOCATION);
			}
		}
	}

	public void redo() {
		super.redo();

		for ( CyNode n : nodes )
			net.removeNode(n);
		for ( CyEdge e : edges )
			net.removeEdge(e);

		CyNetworkView netView = netmgr.getNetworkView(net.getSUID());	
		netView.updateView();
        //Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null , net);
        deleteAction.setEnabled(false);
	}

	public void undo() {
		super.undo();

		for ( CyNode n : nodes )
			net.addNode(n);
		for ( CyEdge e : edges )
			net.addEdge(e);

		CyNetworkView netView = netmgr.getNetworkView(net.getSUID());
		if ( netView != null ) {
			int i = 0;
			for ( CyNode n : nodes ) {
				View<CyNode> nv = netView.getNodeView(n);
				nv.setVisualProperty(NODE_X_LOCATION, xPos[++i]);
				nv.setVisualProperty(NODE_Y_LOCATION, yPos[i] );
			}
		}

		netView.updateView();
        //Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, net);
        deleteAction.setEnabled(true);
	}
}
