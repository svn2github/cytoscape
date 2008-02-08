/* -*-Java-*-
 ********************************************************************************
 *
 * File:         DeleteAction.java
 * RCS:          $Header: $
 * Description:
 * Author:       Allan Kuchinsky
 * Created:      Tue May 24 06:54:43 2005
 * Modified:     Tue Jan 09 05:16:48 2007 (Michael L. Creech) creech@w235krbza760
 * Language:     Java
 * Package:
 * Status:       Experimental (Do Not Distribute)
 *
 * (c) Copyright 2006, Agilent Technologies, all rights reserved.
 *
 ********************************************************************************
 *
 * Revisions:
 *
* Fri May 11 16:58:47 2007 (Michael L. Creech) creech@w235krbza760
*  Removed unneeded imports and update some generics.
 * Tue Jan 09 05:15:18 2007 (Michael L. Creech) creech@w235krbza760
 *  Added setup of Delete keyboard accelerator to constructors.
 * Wed Dec 27 06:54:56 2006 (Michael L. Creech) creech@w235krbza760
 *  Removed use of _title and removed 2 parameter constructor, removed unused
 *  local variables.
 ********************************************************************************
 */
package cytoscape.actions;

import cytoscape.GraphObject;
import cytoscape.Edge;
import cytoscape.Node;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.MenuEvent;

import cytoscape.Edge;
import cytoscape.GraphPerspective;
import cytoscape.Node;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.undo.CyUndo;
import cytoscape.view.CyNetworkView;


/**
 *
 * action for deleting selected Cytoscape nodes and edges
 *
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 *
 */
public class DeleteAction extends CytoscapeAction {
	private static final long serialVersionUID = -5769255815829787466L;

	/**
	 * 
	 */
	public static final String ACTION_TITLE = "Delete Selected Nodes and Edges";

	private GraphObject graphObj = null;

	/**
	 * action for deleting selected Cytoscape nodes and edges
	 */
	public DeleteAction() {
		this(null);
	}

	/**
	 * perform deletion on the input object. if object is a Node, then this will
	 * result in also deleting the edges adjacent to the node
	 *
	 * @param obj the object to be deleted
	 */

	public DeleteAction(GraphObject obj) {
		super(ACTION_TITLE);
		setPreferredMenu("Edit");
		setAcceleratorCombo(KeyEvent.VK_DELETE, 0);
		graphObj = obj;
	}


	/**
	 * Delete all selected nodes, edges, edges adjacent to the selected nodes,
	 * and any nodes/edges passed as arguments.
	 */
	public void actionPerformed(ActionEvent ae) {

		CyNetworkView myView = Cytoscape.getCurrentNetworkView();
		GraphPerspective cyNet = myView.getNetwork();
		List<Edge> edgeViews = myView.getSelectedEdges();
		List<Node> nodeViews = myView.getSelectedNodes();
		Node cyNode;
		Edge cyEdge;

		// if an argument exists, add it to the appropriate list
		if (graphObj != null ) {
			if ( graphObj instanceof cytoscape.Node) {
				cyNode = (Node) graphObj;
				if ( !nodeViews.contains(cyNode) )
					nodeViews.add(cyNode);
			} else if ( graphObj instanceof cytoscape.Edge) {
				cyEdge = (Edge) graphObj;
				if ( !edgeViews.contains(cyEdge) )
					edgeViews.add(cyEdge);
			}
		}

		Set<Integer> edgeIndices = new HashSet<Integer>();
		Set<Integer> nodeIndices = new HashSet<Integer>();

		// add all node indices
		for (int i = 0; i < nodeViews.size(); i++) {
			cyNode = nodeViews.get(i);
			nodeIndices.add(cyNode.getRootGraphIndex());

			// add adjacent edge indices for each node 
			int[] edgesList = cyNet.getAdjacentEdgeIndicesArray(cyNode.getRootGraphIndex(),true,true,true);
			for ( int x = 0; x < edgesList.length; x++ )
				edgeIndices.add(edgesList[x]);
		}

		// add all selected edge indices
		for (int i = 0; i < edgeViews.size(); i++) {
			cyEdge = edgeViews.get(i); 
			edgeIndices.add( cyEdge.getRootGraphIndex() );
		}

		// convert
		int i = 0;
		int[] edgeInd = new int[edgeIndices.size()];
		for (Integer ei : edgeIndices) 
			edgeInd[i++] = ei.intValue();

		i = 0;
		int[] nodeInd = new int[nodeIndices.size()];
		for (Integer ni : nodeIndices) 
			nodeInd[i++] = ni.intValue();

		CyUndo.getUndoableEditSupport().postEdit( new DeleteEdit(cyNet,nodeInd,edgeInd) );

		// delete the actual nodes and edges
		cyNet.hideEdges(edgeInd);
		cyNet.hideNodes(nodeInd);

		Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, cyNet);
	}

    public void menuSelected(MenuEvent me) {
        GraphPerspective n = Cytoscape.getCurrentNetwork();
        if ( n == null || n == Cytoscape.getNullNetwork() ) {
            setEnabled(false);
            return;
        }

        java.util.Set nodes = n.getSelectedNodes();
        java.util.Set edges = n.getSelectedEdges();

        if ( ( nodes != null && nodes.size() > 0 ) || 
		     ( edges != null && edges.size() > 0 ) )
            setEnabled(true);
        else
            setEnabled(false);
    }
}
