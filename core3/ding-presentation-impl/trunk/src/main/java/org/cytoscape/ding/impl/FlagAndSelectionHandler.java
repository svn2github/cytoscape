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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cytoscape.ding.EdgeView;
import org.cytoscape.ding.GraphView;
import org.cytoscape.ding.GraphViewChangeEvent;
import org.cytoscape.ding.GraphViewChangeListener;
import org.cytoscape.ding.NodeView;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.events.SelectedEdgesEvent;
import org.cytoscape.model.events.SelectedEdgesListener;
import org.cytoscape.model.events.SelectedNodesEvent;
import org.cytoscape.model.events.SelectedNodesListener;
import org.cytoscape.model.events.UnselectedEdgesEvent;
import org.cytoscape.model.events.UnselectedEdgesListener;
import org.cytoscape.model.events.UnselectedNodesEvent;
import org.cytoscape.model.events.UnselectedNodesListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class synchronizes the flagged status of nodes and edges as held by a
 * SelectFilter object of a network with the selection status of the
 * corresponding node and edge views in a GraphView. An object will be selected
 * in the view iff the matching object is flagged in the SelectFilter. This
 * class is only used by PhoebeNetworkView, which no longer used anywhere.
 * 
 */
public class FlagAndSelectionHandler implements SelectedNodesListener,
		UnselectedNodesListener, SelectedEdgesListener,
		UnselectedEdgesListener, GraphViewChangeListener {

	private static final Logger logger = LoggerFactory.getLogger(FlagAndSelectionHandler.class);

	private static final String SELECT_ATTR = "selected";

	private final GraphView view;

	/**
	 * Standard constructor takes the flag filter and the view that should be
	 * synchronized. On construction, this object will synchronize the filter
	 * and view by turning on flags or selections that are currently on in one
	 * of the two objects.
	 */
	public FlagAndSelectionHandler(final GraphView view) {
		this.view = view;
		syncFilterAndView();
		view.addGraphViewChangeListener(this);
	}

	private Set<CyNode> getSelectedNodes() {
		final Set<CyNode> selectedNodes = new HashSet<CyNode>();

		for (final CyNode n : view.getNetwork().getNodeList())
			if (n.getCyRow().get(SELECT_ATTR, Boolean.class))
				selectedNodes.add(n);

		return selectedNodes;
	}

	private Set<CyEdge> getSelectedEdges() {
		final Set<CyEdge> selectedEdges = new HashSet<CyEdge>();

		for (final CyEdge n : view.getNetwork().getEdgeList())
			if (n.getCyRow().get(SELECT_ATTR, Boolean.class))
				selectedEdges.add(n);

		return selectedEdges;
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
		for (final CyNode node : flaggedNodes) {
			final NodeView nv = view.getNodeView(node);

			if ((nv == null) || nv.isSelected())
				continue;

			nv.setSelected(true);
		}

		// select all edges that are flagged but not currently selected
		for (final CyEdge edge : flaggedEdges) {
			final EdgeView ev = view.getEdgeView(edge);

			if ((ev == null) || ev.isSelected())
				continue;

			ev.setSelected(true);
		}

		// flag all nodes that are selected but not currently flagged
		for (final CyNode node : selectedNodes)
			node.getCyRow().set(SELECT_ATTR, true);

		// flag all edges that are selected but not currently flagged
		for (final CyEdge edge : selectedEdges)
			edge.getCyRow().set(SELECT_ATTR, true);
	}

	/**
	 * Responds to selection events from the view by setting the matching
	 * flagged state in the SelectFilter object.
	 */
	public void graphViewChanged(final GraphViewChangeEvent event) {

		// GINY bug: the event we get frequently has the correct indices
		// but incorrect Node and Edge objects. For now we get around this
		// by converting indices to graph objects ourselves
		final GraphView source = (GraphView) event.getSource();

		logger.debug("DING got GraphViewChangeEvent: Source = " + source);

		final long start = System.currentTimeMillis();

		if (event.isNodesSelectedType()) {
			// Nodes are selected.
			final CyNode[] selectedNodes = event.getSelectedNodes();

			for (final CyNode node : selectedNodes)
				node.getCyRow().set(SELECT_ATTR, true);
			
		} else if (event.isNodesUnselectedType() || event.isNodesHiddenType()) {
			final CyNode[] objIndecies;
			if (event.isNodesUnselectedType())
				objIndecies = event.getUnselectedNodes();
			else
				objIndecies = event.getHiddenNodes();

			for (final CyNode n : objIndecies)
				n.getCyRow().set(SELECT_ATTR, false);
		} else if (event.isEdgesSelectedType()) {
			final CyEdge[] objIndecies = event.getSelectedEdges();

			for (final CyEdge n : objIndecies)
				n.getCyRow().set(SELECT_ATTR, true);
		} else if (event.isEdgesUnselectedType() || event.isEdgesHiddenType()) {
			final CyEdge[] objIndecies;
			if (event.isEdgesUnselectedType())
				objIndecies = event.getUnselectedEdges();
			else
				objIndecies = event.getHiddenEdges();

			for (final CyEdge n : objIndecies) {
				n.getCyRow().set(SELECT_ATTR, false);
			}
		}

		logger.debug("Finished select operation: Time = "
				+ (System.currentTimeMillis() - start) + " msec.");
	}

	/**
	 * Responds to events indicating a change in the flagged state of one or
	 * more nodes or edges. Sets the corresponding selection state for views of
	 * those objects in the graph view.
	 */

	public void handleEvent(final SelectedNodesEvent event) {
		if (event.getSource() != view.getNetwork())
			return;

		for (final CyNode n : event.getNodeList())
			setNodeSelected(n, true);
	}

	public void handleEvent(final SelectedEdgesEvent event) {
		if (event.getSource() != view.getNetwork())
			return;

		for (final CyEdge n : event.getEdgeList())
			setEdgeSelected(n, true);
	}

	public void handleEvent(final UnselectedNodesEvent event) {
		if (event.getSource() != view.getNetwork())
			return;

		for (final CyNode n : event.getNodeList())
			setNodeSelected(n, false);
	}

	public void handleEvent(final UnselectedEdgesEvent event) {
		if (event.getSource() != view.getNetwork())
			return;

		for (final CyEdge n : event.getEdgeList())
			setEdgeSelected(n, false);
	}

	/**
	 * Helper method to set selection for a node view.
	 */
	private void setNodeSelected(final CyNode node, boolean selectOn) {
		final NodeView nodeView = view.getNodeView(node);

		if (nodeView == null)
			return;

		// sanity check
		// Giny fires a selection event even if there's no change in state
		// we trap this by only requesting a selection if there's a change

		if (nodeView.isSelected() != selectOn)
			nodeView.setSelected(selectOn);
	}

	/**
	 * Helper method to set selection for an edge view.
	 */
	private void setEdgeSelected(final CyEdge edge, boolean selectOn) {
		EdgeView edgeView = view.getEdgeView(edge);

		if (edgeView == null)
			return;
		// sanity check
		// Giny fires a selection event even if there's no change in state
		// we trap this by only requesting a selection if there's a change

		if (edgeView.isSelected() != selectOn)
			edgeView.setSelected(selectOn);
	}
}
