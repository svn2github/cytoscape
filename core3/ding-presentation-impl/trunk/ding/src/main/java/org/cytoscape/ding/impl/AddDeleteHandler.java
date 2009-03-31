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


import org.cytoscape.model.events.AddedEdgeEvent;
import org.cytoscape.model.events.AddedNodeEvent;
import org.cytoscape.model.events.AboutToRemoveEdgeEvent;
import org.cytoscape.model.events.AboutToRemoveNodeEvent;

import org.cytoscape.model.events.AddedEdgeListener;
import org.cytoscape.model.events.AddedNodeListener;
import org.cytoscape.model.events.AboutToRemoveEdgeListener;
import org.cytoscape.model.events.AboutToRemoveNodeListener;

import org.cytoscape.model.CyNetwork;

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
	implements AddedEdgeListener, 
	           AddedNodeListener, 
	           AboutToRemoveEdgeListener, 
	           AboutToRemoveNodeListener {

	private GraphView view;
	private CyNetwork net;

	public AddDeleteHandler(GraphView view) {
		this.view = view;
		net = view.getNetwork();
	}

	public void handleEvent(AddedEdgeEvent e) {
		if ( net != e.getSource() )
			return;

		CyEdge edge = e.getEdge();
		view.addEdgeView(edge);
		view.updateView();
	}

	public void handleEvent(AddedNodeEvent e) {
		if ( net != e.getSource() )
			return;

		CyNode node = e.getNode();
		view.addNodeView(node);
		view.updateView();
	}

	public void handleEvent(AboutToRemoveEdgeEvent e) {
		if ( net != e.getSource() )
			return;

		CyEdge edge = e.getEdge();
		view.removeEdgeView(edge.getIndex());
		view.updateView();
	}

	public void handleEvent(AboutToRemoveNodeEvent e) {
		if ( net != e.getSource() )
			return;

		CyNode node = e.getNode();
		view.removeNodeView(node.getIndex());
		view.updateView();
	}
}

