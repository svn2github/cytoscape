/*
 * Created on May 24, 2005
 *
 */
package cytoscape.editor.actions;

import javax.swing.JMenuItem;

import phoebe.PEdgeView;
import cytoscape.CyEdge;
import cytoscape.view.CyNetworkView;
import edu.umd.cs.piccolo.PNode;
import giny.view.NodeView;

/**
 * 
 * action assigned to Connect button on toolbar in CytoscapeEditor sets cursor
 * to "edge cursor" and sets editor's mode to "CONNECT_MODE"
 * 
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 * 
 * 
 *  
 */


// AJK: 04/27/06 update for new ding renderer

public class EdgeAction {
	public EdgeAction() {
	}

	/**
	 * gets the label for a NodeView
	 * 
	 * @param args
	 *            the input arguments; args[0] should be the CyNetworkView, the
	 *            view for the network in which the edges are created
	 * @param node
	 * @return the name of the edge being created (or extended)
	 */
	public static String getTitle(Object[] args, PNode node) {
		final CyNetworkView nv = (CyNetworkView) args[0];
		if (node instanceof PEdgeView) {
			CyEdge cyEdge = (CyEdge) ((PEdgeView) node).getEdge();

			// AJK: 08/13/05 update for Cytoscape 2.2
			if (cyEdge != null) {
				return cyEdge.getIdentifier();
			}
		}
		return "";
	}

	  /**
	   * gets the context (right-click) menu item that is associated with the input EdgeView
	   * this should be a delete action
	   * @param args arguments, first argument should be the Network view
	   * @param node  (this should be an instance of class EdgeView)
	   * @return the menu item that is associated with the input EdgeView
	   */	
	public static JMenuItem getContextMenuItem(Object[] args, PNode node) {

		final CyNetworkView nv = (CyNetworkView) args[0];
		CyEdge cyEdge;

		if (node instanceof PEdgeView) {

			NodeView sourceView = ((PEdgeView) node).source;
			String sourceName = sourceView.getLabel().getText();
			NodeView targetView = ((PEdgeView) node).target;
			String targetName = targetView.getLabel().getText();
			String myName = new String(sourceName + " -> " + targetName);

			cyEdge = (CyEdge) ((PEdgeView) node).getEdge();
			return (new JMenuItem(new DeleteAction(cyEdge, myName)));

		}
		// if input is <b>not</b> an EdgeView, then just return a DeleteAction with not arguments
		return (new JMenuItem(new DeleteAction("Delete Selected Nodes and Edges")));
	}

}