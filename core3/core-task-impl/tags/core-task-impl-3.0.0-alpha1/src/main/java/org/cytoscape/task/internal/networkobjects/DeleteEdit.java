package org.cytoscape.task.internal.networkobjects;


import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_LOCATION;

import java.util.List;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

import org.cytoscape.util.swing.CyAbstractEdit;


/**
 * An undoable edit that will undo and redo deletion of nodes and edges.
 */ 
class DeleteEdit extends CyAbstractEdit {
	final private List<CyNode> nodes;
	final private Set<CyEdge> edges;
	final private double[] xPos;
	final private double[] yPos;
	final private CySubNetwork net;
	final private DeleteSelectedNodesAndEdgesTask deleteAction;
	final private CyNetworkViewManager netViewMgr;
	
	DeleteEdit(final CySubNetwork net, final List<CyNode> nodes, final Set<CyEdge> edges,
		   final DeleteSelectedNodesAndEdgesTask deleteAction,
		   final CyNetworkViewManager netViewMgr)
	{
		super("Delete");

		this.deleteAction = deleteAction;
		if (net == null)
			throw new NullPointerException("network is null");
		this.net = net;

		if (netViewMgr == null)
			throw new NullPointerException("network manager is null");
		this.netViewMgr = netViewMgr;

		if (nodes == null)
			throw new NullPointerException("nodes is null");
		this.nodes = nodes; 

		if (edges == null)
			throw new NullPointerException("edges is null");
		this.edges = edges; 

		// save the positions of the nodes
		xPos = new double[nodes.size()]; 
		yPos = new double[nodes.size()]; 
		CyNetworkView netView = netViewMgr.getNetworkView(net.getSUID());
		if (netView != null) {
			int i = 0;
			for (CyNode n : nodes) {
				View<CyNode> nv = netView.getNodeView(n);
				xPos[i] = nv.getVisualProperty(NODE_X_LOCATION);
				yPos[i] = nv.getVisualProperty(NODE_Y_LOCATION);
				i++;
			}
		}
	}

	public void redo() {
		super.redo();

		for (CyNode n : nodes)
			net.removeNode(n);
		for (CyEdge e : edges)
			net.removeEdge(e);

		CyNetworkView netView = netViewMgr.getNetworkView(net.getSUID());
		
		// Manually call update presentation
		netView.updateView();
	}

	public void undo() {
		super.undo();

		for (CyNode n : nodes)
			net.addNode(n);
		for (CyEdge e : edges)
			net.addEdge(e);

		CyNetworkView netView = netViewMgr.getNetworkView(net.getSUID());
		if ( netView != null ) {
			int i = 0;
			for ( CyNode n : nodes ) {
				View<CyNode> nv = netView.getNodeView(n);
				nv.setVisualProperty(NODE_X_LOCATION, xPos[i]);
				nv.setVisualProperty(NODE_Y_LOCATION, yPos[i] );
				i++;
			}
		}

		netView.updateView();
	}
}
