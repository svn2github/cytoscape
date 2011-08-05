
package cytoscape.actions;

import java.awt.event.KeyEvent;

import giny.view.NodeView;

import javax.swing.event.MenuKeyListener;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.util.undo.CyAbstractEdit;
import cytoscape.view.CyNetworkView;


/**
 * An undoable edit that will undo and redo deletion of nodes and edges.
 */ 
class DeleteEdit extends CyAbstractEdit {

	private static final long serialVersionUID = -1164181258019250610L;
	int[] nodes;
	int[] edges;
	double[] xPos;
	double[] yPos;
	CyNetwork net;
	// AJK: 03082008 DeleteAction to be reenabled upon undo
	DeleteAction deleteAction;
	

	DeleteEdit(CyNetwork net, int[] nodeInd, int[] edgeInd) {
		super("Delete");
		if ( net == null )
			throw new IllegalArgumentException("network is null");
		this.net = net;
		
		nodes = new int[nodeInd.length]; 
		for ( int i = 0; i < nodeInd.length; i++ )
			nodes[i] = nodeInd[i];

		edges = new int[edgeInd.length];
		for ( int i = 0; i < edgeInd.length; i++ ) 
			edges[i] = edgeInd[i];

		// save the positions of the nodes
		xPos = new double[nodes.length]; 
		yPos = new double[nodes.length]; 
		CyNetworkView netView = Cytoscape.getNetworkView(net.getIdentifier());
		if ( netView != null && netView != Cytoscape.getNullNetworkView() ) {
			for ( int i = 0; i < nodes.length; i++ ) {
				NodeView nv = netView.getNodeView(nodes[i]);
				xPos[i] = nv.getXPosition();
				yPos[i] = nv.getYPosition();
			}
		}
		
	}

	DeleteEdit(CyNetwork net, int[] nodeInd, int[] edgeInd,	DeleteAction deleteAction) {
		this (net, nodeInd, edgeInd);
		this.deleteAction = deleteAction;
	}


	public void redo() {
		super.redo();

		net.hideEdges(edges);
		net.hideNodes(nodes);
		CyNetworkView netView = Cytoscape.getNetworkView(net.getIdentifier());				
		netView.redrawGraph(true, true);
        Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, netView, net);
        deleteAction.setEnabled(false);
	}

	public void undo() {
	 	super.undo();

		net.restoreNodes(nodes);
		net.restoreEdges(edges);

		CyNetworkView netView = Cytoscape.getNetworkView(net.getIdentifier());
		if ( netView != null && netView != Cytoscape.getNullNetworkView() ) {
			for ( int i = 0; i < nodes.length; i++ ) {
				NodeView nv = netView.getNodeView(nodes[i]);
				nv.setOffset( xPos[i], yPos[i] );
			}
		}
		netView.redrawGraph(true, true);
        Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, netView, net);
        deleteAction.setEnabled(true);
	}
}
