/*
 File: NewWindowSelectedNodesEdgesAction.java

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

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.util.CyNetworkNaming;
import cytoscape.util.CytoscapeAction;

import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualStyle;

//-------------------------------------------------------------------------
import giny.model.Node;
import java.awt.event.ActionEvent;

import java.util.Iterator;
import java.util.Set;

import javax.swing.event.MenuEvent;


//-------------------------------------------------------------------------
/**
 *
 */
public class NewWindowSelectedNodesEdgesAction extends CytoscapeAction {
	/**
	 * Creates a new NewWindowSelectedNodesEdgesAction object.
	 */
	public NewWindowSelectedNodesEdgesAction() {
		super("From selected nodes, selected edges");
		setPreferredMenu("File.New.Network");
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_N,
		                    ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		// keep ref to current state
        CyNetwork current_network = Cytoscape.getCurrentNetwork();
        CyNetworkView current_network_view = Cytoscape.getCurrentNetworkView();

		if ((current_network == null) || (current_network == Cytoscape.getNullNetwork()))
			return;

		Set nodes = current_network.getSelectedNodes();
		Set edges = current_network.getSelectedEdges();

		CyNetwork new_network = Cytoscape.createNetwork(nodes, edges,
		                                                CyNetworkNaming.getSuggestedSubnetworkTitle(current_network),
		                                                current_network);

		String title = " selection";
		CyNetworkView new_network_view = Cytoscape.createNetworkView(new_network, title);
        
        String vsName = "default";
        
        // keep the node positions
        if (current_network_view != Cytoscape.getNullNetworkView()) {
            Iterator i = new_network.nodesIterator();

            while (i.hasNext()) {
                Node node = (Node) i.next();
                new_network_view.getNodeView(node)
                        .setOffset(current_network_view.getNodeView(node).getXPosition(),
                                   current_network_view.getNodeView(node).getYPosition());
            }

            new_network_view.fitContent();

            // Set visual style
            VisualStyle newVS = current_network_view.getVisualStyle();

            if (newVS != null) {
                vsName = newVS.getName();
            }
        }
        Cytoscape.getVisualMappingManager().setVisualStyle(vsName);
	}

	public void menuSelected(MenuEvent e) {
        CyNetwork n = Cytoscape.getCurrentNetwork();
        if ( n == null || n == Cytoscape.getNullNetwork() ) {
           	setEnabled(false); 
			return;
		}

        java.util.Set edges = n.getSelectedEdges();
        java.util.Set nodes = n.getSelectedNodes();

        if ( ( nodes != null && nodes.size() > 0 ) ||
             ( edges != null && edges.size() > 0 ) )
            setEnabled(true);
        else
            setEnabled(false);

	}
}
