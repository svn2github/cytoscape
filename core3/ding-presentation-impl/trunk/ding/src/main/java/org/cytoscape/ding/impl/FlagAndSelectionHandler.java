/*
 File: FlagAndSelectionHandler.java

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

//---------------------------------------------------------------------------
//  $Revision: 13022 $ 
//  $Date: 2008-02-11 13:59:26 -0800 (Mon, 11 Feb 2008) $
//  $Author: mes $
//---------------------------------------------------------------------------
package org.cytoscape.ding.impl;


import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

//import org.cytoscape.data.SelectEvent;
//import org.cytoscape.data.SelectEventListener;
//import org.cytoscape.data.SelectFilter;

import org.cytoscape.model.events.SelectedNodesEvent;
import org.cytoscape.model.events.SelectedEdgesEvent;
import org.cytoscape.model.events.UnselectedNodesEvent;
import org.cytoscape.model.events.UnselectedEdgesEvent;
import org.cytoscape.model.events.SelectedNodesListener;
import org.cytoscape.model.events.SelectedEdgesListener;
import org.cytoscape.model.events.UnselectedNodesListener;
import org.cytoscape.model.events.UnselectedEdgesListener;

import org.cytoscape.ding.EdgeView;
import org.cytoscape.ding.GraphView;
import org.cytoscape.ding.GraphViewChangeEvent;
import org.cytoscape.ding.GraphViewChangeListener;
import org.cytoscape.ding.NodeView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;


/**
 * This class synchronizes the flagged status of nodes and edges as held by a
 * SelectFilter object of a network with the selection status of the corresponding
 * node and edge views in a GraphView. An object will be selected in the view
 * iff the matching object is flagged in the SelectFilter. This class is only used
 * by PhoebeNetworkView, which no longer used anywhere.
 */
public class FlagAndSelectionHandler 
	implements SelectedNodesListener, UnselectedNodesListener, SelectedEdgesListener, 
	           UnselectedEdgesListener, GraphViewChangeListener {
	private GraphView view;

	/**
	 * Standard constructor takes the flag filter and the view that should be
	 * synchronized. On construction, this object will synchronize the filter
	 * and view by turning on flags or selections that are currently on in one
	 * of the two objects.
	 */
	public FlagAndSelectionHandler(GraphView view) {
		this.view = view;
		syncFilterAndView();
		view.addGraphViewChangeListener(this);
	}

	private Set<CyNode> getSelectedNodes() {
		Set<CyNode> sel = new HashSet<CyNode>();
		for ( CyNode n : view.getNetwork().getNodeList() )
			if ( n.attrs().get("selected", Boolean.class).booleanValue() )
				sel.add(n);

		return sel;
	}

	private Set<CyEdge> getSelectedEdges() {
		Set<CyEdge> sel = new HashSet<CyEdge>();
		for ( CyEdge n : view.getNetwork().getEdgeList() )
			if ( n.attrs().get("selected", Boolean.class).booleanValue() )
				sel.add(n);

		return sel;
	}

	/**
	 * Synchronizes the filter and view of this object by selecting every object
	 * that is currently flagged and vice versa.
	 */
	private void syncFilterAndView() {
		final Set<CyNode> flaggedNodes = getSelectedNodes();
		final Set<CyEdge> flaggedEdges = getSelectedEdges();

		final List<CyNode> selectedNodes = view.getSelectedNodes();
		final List<CyEdge> selectedEdges = view.getSelectedEdges();

		// select all nodes that are flagged but not currently selected
		for (Iterator iter = flaggedNodes.iterator(); iter.hasNext();) {
			CyNode node = (CyNode) iter.next();
			NodeView nv = view.getNodeView(node);

			if ((nv == null) || nv.isSelected()) {
				continue;
			}

			nv.setSelected(true);
		}

		// select all edges that are flagged but not currently selected
		for (Iterator iter = flaggedEdges.iterator(); iter.hasNext();) {
			CyEdge edge = (CyEdge) iter.next();
			EdgeView ev = view.getEdgeView(edge);

			if ((ev == null) || ev.isSelected()) {
				continue;
			}

			ev.setSelected(true);
		}

		// flag all nodes that are selected but not currently flagged
		for (CyNode node : selectedNodes) {
			node.attrs().set("selected",true);
		}

		// flag all edges that are selected but not currently flagged
		for (CyEdge edge : selectedEdges) {
			edge.attrs().set("selected",true);
		}
	}

	/**
	 * Responds to selection events from the view by setting the matching
	 * flagged state in the SelectFilter object.
	 */
	public void graphViewChanged(GraphViewChangeEvent event) {
		// GINY bug: the event we get frequently has the correct indices
		// but incorrect Node and Edge objects. For now we get around this
		// by converting indices to graph objects ourselves
		final GraphView source = (GraphView) event.getSource();


		if (event.isNodesSelectedType()) {
			CyNode[] objIndecies = event.getSelectedNodes();

			for ( CyNode n : objIndecies )
				n.attrs().set("selected",true);

		} else if (event.isNodesUnselectedType() || event.isNodesHiddenType()) {
			CyNode[] objIndecies;
			if (event.isNodesUnselectedType()) {
				objIndecies = event.getUnselectedNodes();
			} else {
				objIndecies = event.getHiddenNodes();
			}

			for ( CyNode n : objIndecies ) { 
				n.attrs().set("selected",false);
			}
		} else if (event.isEdgesSelectedType()) {
			CyEdge[] objIndecies = event.getSelectedEdges();

			for ( CyEdge n : objIndecies ) { 
				n.attrs().set("selected",true);
			}
		} else if (event.isEdgesUnselectedType() || event.isEdgesHiddenType()) {
			CyEdge[] objIndecies; 
			if (event.isEdgesUnselectedType()) {
				objIndecies = event.getUnselectedEdges();
			} else {
				objIndecies = event.getHiddenEdges();
			}

			for ( CyEdge n : objIndecies ) { 
				n.attrs().set("selected",false);
			}
		}
	}

	/**
	 * Responds to events indicating a change in the flagged state of one or
	 * more nodes or edges. Sets the corresponding selection state for views of
	 * those objects in the graph view.
	 */

	public void handleEvent(SelectedNodesEvent event) {
		if ( event.getSource() != view.getNetwork() )
			return;

		for ( CyNode n : event.getNodeList() ) {
			setNodeSelected( n, true );
		}
	}

	public void handleEvent(SelectedEdgesEvent event) {
		if ( event.getSource() != view.getNetwork() )
			return;

		for ( CyEdge n : event.getEdgeList() ) {
			setEdgeSelected( n, true );
		}
	}

	public void handleEvent(UnselectedNodesEvent event) {
		if ( event.getSource() != view.getNetwork() )
			return;

		for ( CyNode n : event.getNodeList() ) {
			setNodeSelected( n, false );
		}
	}

	public void handleEvent(UnselectedEdgesEvent event) {
		if ( event.getSource() != view.getNetwork() )
			return;

		for ( CyEdge n : event.getEdgeList() ) {
			setEdgeSelected( n, false );
		}
	}

	/**
	 * Helper method to set selection for a node view.
	 */
	private void setNodeSelected(CyNode node, boolean selectOn) {
		NodeView nodeView = view.getNodeView(node);

		if (nodeView == null) {
			return;
		} // sanity check
		  // Giny fires a selection event even if there's no change in state
		  // we trap this by only requesting a selection if there's a change

		if (nodeView.isSelected() != selectOn) {
			nodeView.setSelected(selectOn);
		}
	}

	/**
	 * Helper method to set selection for an edge view.
	 */
	private void setEdgeSelected(CyEdge edge, boolean selectOn) {
		EdgeView edgeView = view.getEdgeView(edge);

		if (edgeView == null) {
			return;
		} // sanity check
		  // Giny fires a selection event even if there's no change in state
		  // we trap this by only requesting a selection if there's a change

		if (edgeView.isSelected() != selectOn) {
			edgeView.setSelected(selectOn);
		}
	}
}
