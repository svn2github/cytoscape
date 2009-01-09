/*
 File: NewWindowSelectedNodesEdgesAction.java 
 
 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
 
 The Cytoscape Consortium is: 
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Pasteur Institute
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

//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.util.CyNetworkNaming;
import cytoscape.util.CytoscapeAction;

//-------------------------------------------------------------------------
public class NewWindowSelectedNodesEdgesAction extends CytoscapeAction {

	public NewWindowSelectedNodesEdgesAction() {
		super("From selected nodes, selected edges");
		setPreferredMenu("File.New.Network");
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_N, ActionEvent.CTRL_MASK
				| ActionEvent.SHIFT_MASK);
	}

	public void actionPerformed(ActionEvent e) {
		// save the vizmapper catalog

		// CyNetworkView current_network_view =
		// Cytoscape.getCurrentNetworkView();
		// CyNetwork current_network = current_network_view.getNetwork();
		CyNetwork current_network = Cytoscape.getCurrentNetwork();
		int[] nodes = current_network.getFlaggedNodeIndicesArray();
		int[] edges = current_network.getFlaggedEdgeIndicesArray();

		// int[] nodes = current_network_view.getSelectedNodeIndices();
		// int[] edges = current_network_view.getSelectedEdgeIndices();

		// CyNetwork new_network = Cytoscape.createNetwork( nodes, edges );
		CyNetwork new_network = Cytoscape.createNetwork(nodes, edges,
				CyNetworkNaming.getSuggestedSubnetworkTitle(current_network),
				current_network);
		new_network.setExpressionData(current_network.getExpressionData());

		String title = " selection";
		Cytoscape.createNetworkView(new_network, title);
		
		// Set visual style
		Cytoscape.getNetworkView(new_network.getIdentifier())
			.setVisualStyle(Cytoscape.getCurrentNetworkView().getVisualStyle().getName());

	}
}
