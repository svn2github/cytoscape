package csplugins.layout.algorithms;
import giny.view.NodeView;

import java.util.Collection;
import java.util.Iterator;

import giny.view.NodeView;

import cytoscape.CyNode;
import cytoscape.view.CyNetworkView;
import cytoscape.layout.AbstractLayout;

public class StackedNodeLayout extends AbstractLayout {
	
	/**
	 * Puts a collection of nodes into a "stack" layout. This means the nodes are
	 * arranged in a line vertically, with each node overlapping with the previous.
	 * 
	 * @param nodes the nodes whose position will be modified
	 * @param x_position the x position for the nodes
	 * @param y_start_position the y starting position for the stack
	 */

	private double y_start_position;
	private double x_position;
	private Collection nodes;

	public StackedNodeLayout( double x_position, double y_start_position, Collection nodes) {
		super();
		this.x_position = x_position;
		this.y_start_position = y_start_position;
		this.nodes = nodes;
	}

	public void construct() {
        
		Iterator it = nodes.iterator();
		double yPosition = y_start_position;
		while(it.hasNext()){
			CyNode node = (CyNode)it.next();
			NodeView nodeView = networkView.getNodeView(node);
			nodeView.setXPosition(x_position);
			nodeView.setYPosition(yPosition);
			yPosition += nodeView.getHeight() * 2;
		}
	}

	public String getName() { return "Stacked Node Layout"; }

	public String toString() { return getName(); }
}
