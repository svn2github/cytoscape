/*
 File: AddDeleteHandler.java

 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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


import org.cytoscape.view.model.events.AddedEdgeViewEvent;
import org.cytoscape.view.model.events.AddedNodeViewEvent;
//import org.cytoscape.view.model.events.AboutToRemoveEdgeEvent;
//import org.cytoscape.view.model.events.AboutToRemoveNodeEvent;

import org.cytoscape.view.model.events.AddedEdgeViewListener;
import org.cytoscape.view.model.events.AddedNodeViewListener;
//import org.cytoscape.view.model.events.AboutToRemoveEdgeListener;
//import org.cytoscape.view.model.events.AboutToRemoveNodeListener;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

import org.cytoscape.ding.GraphView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;


/**
 * Listens for Add/Delete Node/Edge events and updated a GraphView accordingly. 
 */
public class AddDeleteHandler 
	implements AddedEdgeViewListener, 
	           AddedNodeViewListener
			  /* , 
	           AboutToRemoveEdgeListener, 
	           AboutToRemoveNodeListener */{

	private final GraphView view;
	private final CyNetworkView networkView;

	public AddDeleteHandler(final GraphView view) {
		this.view = view;
		this.networkView = view.getViewModel();
	}

	public void handleEvent(final AddedEdgeViewEvent e) {
		if ( networkView != e.getSource() )
			return;

		final View<CyEdge> ev = e.getEdgeView();
		view.addEdgeView(ev.getSource());
		view.updateView();
	}

	public void handleEvent(final AddedNodeViewEvent e) {
		if ( networkView != e.getSource() )
			return;

		final View<CyNode> nv = e.getNodeView();
		view.addNodeView(nv.getSource());
		view.updateView();
	}
/*
	public void handleEvent(final AboutToRemoveEdgeEvent e) {
		if ( networkView != e.getSource() )
			return;

		final View<CyEdge> edge = e.getEdgeView();
		view.removeEdgeView(edge.getIndex());
		view.updateView();
	}

	public void handleEvent(final AboutToRemoveNodeEvent e) {
		if ( networkView != e.getSource() )
			return;

		final View<CyNode> node = e.getNodeView();
		view.removeNodeView(node.getIndex());
		view.updateView();
	}
	*/
}

