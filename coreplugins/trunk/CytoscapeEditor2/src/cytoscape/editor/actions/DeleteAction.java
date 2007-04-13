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
 * Tue Jan 09 05:15:18 2007 (Michael L. Creech) creech@w235krbza760
 *  Added setup of Delete keyboard accelerator to constructors.
 * Wed Dec 27 06:54:56 2006 (Michael L. Creech) creech@w235krbza760
 *  Removed use of _title and removed 2 parameter constructor, removed unused
 *  local variables.
 ********************************************************************************
 */
package cytoscape.editor.actions;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.actions.GinyUtils;

import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.DeleteEdit;

import cytoscape.util.CytoscapeAction;

import cytoscape.util.undo.CyUndo;

import cytoscape.view.CyNetworkView;

import giny.model.GraphObject;

import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.event.ActionEvent;

import java.awt.event.KeyEvent;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.event.MenuEvent;

import java.util.List;
import java.util.LinkedList;


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
		setPreferredIndex(Cytoscape.getDesktop().getCyMenus().getEditMenu().getItemCount() - 2);
		setAcceleratorCombo(KeyEvent.VK_DELETE, 0);
		graphObj = obj;
	}


	/**
	 * Delete all selected nodes, edges, edges adjacent to the selected nodes,
	 * and any nodes/edges passed as arguments.
	 */
	public void actionPerformed(ActionEvent ae) {

		CyNetworkView myView = Cytoscape.getCurrentNetworkView();
		CyNetwork cyNet = myView.getNetwork();
		List edgeViews = myView.getSelectedEdges();
		List nodeViews = myView.getSelectedNodes();

		CyNode cyNode;
		CyEdge cyEdge;
		NodeView nv;
		EdgeView ev;

		// if an argument exists, add it to the appropriate list
		if (graphObj != null ) {
			if ( graphObj instanceof giny.model.Node) {
				cyNode = (CyNode) graphObj;
				nv = Cytoscape.getCurrentNetworkView().getNodeView(cyNode);
				if ( !nodeViews.contains(nv) )
					nodeViews.add(nv);
			} else if ( graphObj instanceof giny.model.Edge) {
				cyEdge = (CyEdge) graphObj;
				ev = Cytoscape.getCurrentNetworkView().getEdgeView(cyEdge);
				if ( !edgeViews.contains(ev) )
					edgeViews.add(ev);
			}
		}

		List<Integer> edgeIndices = new LinkedList<Integer>();
		List<Integer> nodeIndices = new LinkedList<Integer>();

		// add all node indices
		for (int i = 0; i < nodeViews.size(); i++) {
			nv = (NodeView) nodeViews.get(i);
			cyNode = (CyNode) nv.getNode();
			nodeIndices.add(cyNode.getRootGraphIndex());

			// add adjacent edge indices for each node 
			int[] edgesList = cyNet.getAdjacentEdgeIndicesArray(cyNode.getRootGraphIndex(),true,true,true);
			for ( int x = 0; x < edgesList.length; x++ )
				edgeIndices.add(edgesList[x]);
		}

		// add all selected edge indices
		for (int i = 0; i < edgeViews.size(); i++) {
			ev = (EdgeView) edgeViews.get(i); 
			cyEdge = (CyEdge) ev.getEdge();
			edgeIndices.add( cyEdge.getRootGraphIndex() );
		}

		// convert
		int[] edgeInd = new int[edgeIndices.size()];
		for (int i = 0; i < edgeInd.length; i++) 
			edgeInd[i] = edgeIndices.get(i).intValue();

		int[] nodeInd = new int[nodeIndices.size()];
		for (int i = 0; i < nodeInd.length; i++) 
			nodeInd[i] = nodeIndices.get(i).intValue();

		CyUndo.getUndoableEditSupport().postEdit( new DeleteEdit(cyNet,nodeInd,edgeInd) );

		// delete the actual nodes and edges
		cyNet.hideEdges(edgeInd);
		cyNet.hideNodes(nodeInd);

		Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED,
		                             CytoscapeEditorManager.CYTOSCAPE_EDITOR, cyNet);
	}

    public void menuSelected(MenuEvent me) {
        CyNetworkView currView = Cytoscape.getCurrentNetworkView();
        if ( currView == null || currView == Cytoscape.getNullNetworkView() )
            setEnabled(false);

        List n = currView.getSelectedNodes();
        List e = currView.getSelectedEdges();

        if ( (n != null && n.size() > 0 ) ||
             (e != null && e.size() > 0 ) )
            setEnabled(true);
        else
            setEnabled(false);
    }
}
