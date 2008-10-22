
package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.util.undo.CyAbstractEdit;
import org.cytoscape.view.GraphView;
import org.cytoscape.view.NodeView;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import java.util.Set;


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
	
	DeleteEdit(CySubNetwork net, Set<CyNode> nodes, Set<CyEdge> edges,	DeleteAction deleteAction) {
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
		GraphView netView = Cytoscape.getNetworkView(net.getSUID());
		if ( netView != null ) {
			int i = 0;
			for ( CyNode n : nodes ) {
				NodeView nv = netView.getNodeView(n);
				xPos[++i] = nv.getXPosition();
				yPos[i] = nv.getYPosition();
			}
		}
	}

	public void redo() {
		super.redo();

		for ( CyNode n : nodes )
			net.removeNode(n);
		for ( CyEdge e : edges )
			net.removeEdge(e);

		GraphView netView = Cytoscape.getNetworkView(net.getSUID());	
		Cytoscape.redrawGraph(netView);
        Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null , net);
        deleteAction.setEnabled(false);
	}

	public void undo() {
		super.undo();

		for ( CyNode n : nodes )
			net.addNode(n);
		for ( CyEdge e : edges )
			net.addEdge(e);

		GraphView netView = Cytoscape.getNetworkView(net.getSUID());
		if ( netView != null && netView != Cytoscape.getNullNetworkView() ) {
			int i = 0;
			for ( CyNode n : nodes ) {
				NodeView nv = netView.getNodeView(n);
				nv.setOffset( xPos[++i], yPos[i] );
			}
		}

		Cytoscape.redrawGraph(netView);
        Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, net);
        deleteAction.setEnabled(true);
	}
}
