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

package cytoscape.actions;

import org.cytoscape.GraphPerspective;
import org.cytoscape.Node;
import org.cytoscape.Edge;

import org.cytoscape.view.GraphView;

import cytoscape.Cytoscape;
import org.cytoscape.vizmap.VisualMappingManager;
import org.cytoscape.vizmap.VisualStyle;

import cytoscape.util.CyNetworkNaming;
import cytoscape.util.CytoscapeAction;

import java.awt.event.ActionEvent;

import java.util.Iterator;
import java.util.Set;

import javax.swing.event.MenuEvent;


/**
 *
 */
public class NewWindowSelectedNodesEdgesAction extends CytoscapeAction {
	private final static long serialVersionUID = 1202339869856400L;
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
        GraphPerspective current_network = Cytoscape.getCurrentNetwork();
        GraphView current_network_view = Cytoscape.getCurrentNetworkView();

		if ((current_network == null) || (current_network == Cytoscape.getNullNetwork()))
			return;

		Set<Node> nodes = current_network.getSelectedNodes();
		Set<Edge> edges = current_network.getSelectedEdges();

		GraphPerspective new_network = Cytoscape.createNetwork(nodes, edges,
		                                                CyNetworkNaming.getSuggestedSubnetworkTitle(current_network),
		                                                current_network);

		String title = " selection";
		GraphView new_network_view = Cytoscape.createNetworkView(new_network, title);
        
        // keep the node positions
        VisualStyle newVS = null;
        VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
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
            newVS = vmm.getVisualStyleForView( current_network_view );

        }
        if (newVS != null) {
            newVS = vmm.getCalculatorCatalog().getDefaultVisualStyle();
        }

        vmm.setVisualStyleForView(new_network_view, newVS);
	}

	public void menuSelected(MenuEvent e) {
        GraphPerspective n = Cytoscape.getCurrentNetwork();
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
