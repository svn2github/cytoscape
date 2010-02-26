/*
 File: VizMapBypassNetworkListener.java

 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.visual.ui;

import cytoscape.Cytoscape;

import cytoscape.view.CytoscapeDesktop;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * Adds NodeView and EdgeView vizmap bypass listeners to network views as
 * the views are created.
 */
public class VizMapBypassNetworkListener implements PropertyChangeListener {
	/**
	 * Listens for NETWORK_VIEW_CREATED events and if it hears one, it adds
	 * node and edge context menu listeners to the view.
	 * @param evnt The event we're hearing.
	 */
	public void propertyChange(final PropertyChangeEvent evnt) {
		if (CytoscapeDesktop.NETWORK_VIEW_CREATED.equals(evnt.getPropertyName())) {
			final NodeBypassMenuListener nodeBypassMenuListener = new NodeBypassMenuListener();
			Cytoscape.getCurrentNetworkView().addNodeContextMenuListener(nodeBypassMenuListener);

			final EdgeBypassMenuListener edgeBypassMenuListener = new EdgeBypassMenuListener();
			Cytoscape.getCurrentNetworkView().addEdgeContextMenuListener(edgeBypassMenuListener);
		}
	}
}
