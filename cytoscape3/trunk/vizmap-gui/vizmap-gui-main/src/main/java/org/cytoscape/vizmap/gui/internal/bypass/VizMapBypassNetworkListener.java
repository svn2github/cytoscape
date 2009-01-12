/*
 File: VizMapBypassNetworkListener.java

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
package org.cytoscape.vizmap.gui.internal.bypass;

import org.cytoscape.vizmap.gui.editors.EditorFactory;

import cytoscape.events.NetworkViewAddedEvent;
import cytoscape.events.NetworkViewAddedListener;

import cytoscape.CyNetworkManager;


/**
 * Adds NodeView and EdgeView vizmap bypass listeners to network views as
 * the views are created.
 */
public class VizMapBypassNetworkListener implements NetworkViewAddedListener {

	private EditorFactory ef;
	private CyNetworkManager cyNetworkManager;
	
	public VizMapBypassNetworkListener(EditorFactory ef, CyNetworkManager cyNetworkManager) {
		this.ef = ef;
	}

	/**
	 * Listens for network view added and if it hears one, it adds
	 * node and edge context menu listeners to the view.
	 * @param evnt The event we're hearing.
	 */
	public void handleEvent(NetworkViewAddedEvent event) {
		NodeBypassMenuListener node_menu_listener = new NodeBypassMenuListener(ef);
		event.getNetworkView().addNodeContextMenuListener(node_menu_listener);

		EdgeBypassMenuListener edge_menu_listener = new EdgeBypassMenuListener(ef);
		event.getNetworkView().addEdgeContextMenuListener(edge_menu_listener);
	}
}
