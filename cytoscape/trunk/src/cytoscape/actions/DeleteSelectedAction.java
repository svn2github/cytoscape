/*
  File: DeleteSelectedAction.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;

import cytoscape.Cytoscape;

import cytoscape.view.CyNetworkView;

//-------------------------------------------------------------------------
import giny.model.GraphPerspective;

import giny.view.GraphView;
import giny.view.NodeView;

import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.event.MenuEvent;
import javax.swing.undo.AbstractUndoableEdit;

import cytoscape.util.undo.CyUndo; 

//-------------------------------------------------------------------------
/**
 * Giny version of the original class. Note that the original version was
 * only available when editing mode was enabled, and caused the selected
 * nodes to be permanently removed from the graph (and, necessarily, the view).
 * This version hides the selected nodes from both the graph and the view,
 * as there are currently no methods to remove a node view from the graph view
 * in Giny. The semantics of this and related classes for modifying the
 * graph and view should be clarified.
 */
public class DeleteSelectedAction extends AbstractAction {
	CyNetworkView networkView;

	/**
	 * Creates a new DeleteSelectedAction object.
	 *
	 * @param networkView  DOCUMENT ME!
	 */
	public DeleteSelectedAction(CyNetworkView networkView) {
		super("Delete Selected Nodes and Edges");
		this.networkView = networkView;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		String callerID = "DeleteSelectedAction.actionPerformed";

		//   make this action undo-able
		final List nodes = networkView.getSelectedNodes();
		final List edges = networkView.getSelectedEdges();

		GraphView view = networkView.getView();
		GraphPerspective perspective = view.getGraphPerspective();

		// get the Selected node and edge indices
		final int[] node_indicies = view.getSelectedNodeIndices();
		final int[] edge_indicies = view.getSelectedEdgeIndices();

		//and the node/edge vew objects
		final List selected_nodeViews = view.getSelectedNodes();
		final List selected_edgeViews = view.getSelectedEdges();

		// Hide the viewable things and the perspective refs
		view.hideGraphObjects(selected_nodeViews);
		view.hideGraphObjects(selected_edgeViews);
		perspective.hideEdges(edge_indicies);
		perspective.hideNodes(node_indicies);

		networkView.redrawGraph(false, false);

		CyUndo.getUndoableEditSupport().postEdit(new AbstractUndoableEdit() {
				final String network_id = networkView.getNetwork().getIdentifier();

				public String getPresentationName() {
					// AJK: 10/21/05 return null as presentation name because we are using iconic buttons
					//				return "Delete";
					return "Remove";
				}

				public String getRedoPresentationName() {
					if (edges.size() == 0)

						// AJK: 10/21/05 return null as presentation name because we are using iconic buttons
						return "Redo: Removed Nodes";

					//					return " ";
					else

						// AJK: 10/21/05 return null as presentation name because we are using iconic buttons
						return "Redo: Removed Nodes and Edges";

					//					return " ";
				}

				public String getUndoPresentationName() {
					if (edges.size() == 0)

						// AJK: 10/21/05 return null as presentation name because we are using iconic buttons
						return "Undo: Removed Nodes";

					//					return null;
					else

						// AJK: 10/21/05 return null as presentation name because we are using iconic buttons
						return "Undo: Removed Nodes and Edges";

					//					return null;
				}

				public void redo() {
					super.redo();

					GraphView view = networkView.getView();
					GraphPerspective perspective = view.getGraphPerspective();
					view.hideGraphObjects(selected_nodeViews);
					view.hideGraphObjects(selected_edgeViews);
					perspective.hideEdges(edge_indicies);
					perspective.hideNodes(node_indicies);
				}

				public void undo() {
					super.undo();

					GraphView view = networkView.getView();
					GraphPerspective perspective = view.getGraphPerspective();
					view.showGraphObjects(selected_nodeViews);
					view.showGraphObjects(selected_edgeViews);
					perspective.restoreEdges(edge_indicies);
					perspective.restoreNodes(node_indicies);

				}
			});
		Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, networkView.getNetwork());
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
