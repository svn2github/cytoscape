// $Id: DisplayBioPaxDetails.java,v 1.10 2006/07/21 17:05:50 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package cytoscape.coreplugins.biopax.action;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.coreplugins.biopax.mapping.MapBioPaxToCytoscape;
import cytoscape.coreplugins.biopax.util.cytoscape.CySessionUtil;
import cytoscape.coreplugins.biopax.util.cytoscape.CytoscapeWrapper;
import cytoscape.coreplugins.biopax.view.BioPaxContainer;
import cytoscape.coreplugins.biopax.view.BioPaxDetailsPanel;
import cytoscape.data.SelectEvent;
import cytoscape.data.SelectEventListener;


import java.awt.*;

import java.util.HashSet;
import java.util.Iterator;

import javax.swing.*;


/**
 * Displays BioPAX Details when user clicks on a Node.
 * <p/>
 * When a user selects a node, the specified BioPaxDetailsPanel Object
 * will display the node details.  Additionally, if the BioPaxDetailsPanel
 * Object is embedded inside a CytoPanel, its tab is automatically
 * made active.
 *
 * @author Ethan Cerami
 */
public class DisplayBioPaxDetails implements SelectEventListener {
	private int totalNumNodesSelected = 0;
	private BioPaxDetailsPanel bpPanel;

	/**
	 * Constructor.
	 *
	 * @param bpPanel BioPaxDetailsPanel Object that will actually display
	 *                the BioPax details.
	 */
	public DisplayBioPaxDetails(BioPaxDetailsPanel bpPanel) {
		this.bpPanel = bpPanel;
	}

	/**
	 * User has selected/unselected one or more nodes.
	 *
	 * @param event Select Event.
	 */
	@SuppressWarnings("unchecked")
	public void onSelectEvent(SelectEvent event) {
		int targetType = event.getTargetType();

		//  Only show details when exactly one node/edge is selected.
		//  This is done by keeping a running total of number of nodes/edges
		//  currently selected.

		//  A simpler option would be to obtain a SelectFilter object from
		//  the current network, and simply query it for a list of selected
		//  nodes/edges.  However, we want the listener to work on multiple
		//  networks.  For example, we want to display node/edge details
		//  for a parent network and any of its subnetworks.
		if (targetType == SelectEvent.NODE_SET) {
			HashSet set = (HashSet) event.getTarget();
			trackTotalNumberNodesSelected(event, set);

			if (event.getEventType() && (totalNumNodesSelected == 1)) {
				Iterator iterator = set.iterator();
				CyNode node = (CyNode) iterator.next();

				//  Get the BioPAX Util Object from the current network
				CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
				String id = node.getIdentifier();

				if (id != null && !CySessionUtil.isSessionReadingInProgress()) {
					//  Conditionally, set up BP UI
			        //  If we are reading a session file, ignore the node selection event.
		            CytoscapeWrapper.initBioPaxPlugInUI();
		            //  Show the details
		            bpPanel.showDetails(id);
		            //  If legend is showing, show details
		            BioPaxContainer bpContainer = BioPaxContainer.getInstance();
		            bpContainer.showDetails();
				}
			}
		}

		// update custom nodes
		MapBioPaxToCytoscape.customNodes(Cytoscape.getCurrentNetworkView());
	}

	/**
	 * Recursive Method for Walking up a Containment Tree, looking for
	 * a CytoPanel
	 *
	 * @param c Container Object.
	 */
	private void activateTabs(Container c) {
		Container parent = c.getParent();

		if (parent != null) {
			if (parent instanceof JTabbedPane) {
				JTabbedPane parentTabbedPane = (JTabbedPane) parent;
				int index = parentTabbedPane.indexOfComponent(c);
				parentTabbedPane.setSelectedIndex(index);
			}

			activateTabs(parent);
		}
	}

	/**
	 * Keeps track of total number of Nodes currently selected by the user.
	 */
	private void trackTotalNumberNodesSelected(SelectEvent event, HashSet set) {
		if (event.getEventType()) {
			totalNumNodesSelected += set.size();
		} else {
			totalNumNodesSelected -= set.size();

			if (totalNumNodesSelected < 0) {
				totalNumNodesSelected = 0;
			}
		}
	}
}
