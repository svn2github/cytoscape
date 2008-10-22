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

import cytoscape.Cytoscape;
import cytoscape.util.CyNetworkNaming;
import cytoscape.util.CytoscapeAction;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyDataTableUtil;
import org.cytoscape.view.GraphView;
import org.cytoscape.vizmap.VisualMappingManager;
import org.cytoscape.vizmap.VisualStyle;

import javax.swing.event.MenuEvent;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Set;
import java.util.List;


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
		//TODO
		System.out.println("NOT implemented");
		/*
        CyNetwork current_network = Cytoscape.getCurrentNetwork();
        GraphView current_network_view = Cytoscape.getCurrentNetworkView();

		if ((current_network == null) || (current_network == Cytoscape.getNullNetwork()))
			return;

		List<CyNode> nodes = CyDataTableUtil.getNodesInState(current_network,"selected",true); 
		List<CyEdge> edges = CyDataTableUtil.getEdgesInState(current_network,"selected",true); 

		CyNetwork new_network = Cytoscape.createNetwork(nodes, edges,
		                                                CyNetworkNaming.getSuggestedSubnetworkTitle(current_network),
		                                                current_network);

		String title = " selection";
		GraphView new_network_view = Cytoscape.createNetworkView(new_network, title);
        
        String vsName = "default";
        
        // keep the node positions
        VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
        if (current_network_view != Cytoscape.getNullNetworkView()) {
			for ( CyNode node : new_network.getNodeList() ) {
                new_network_view.getNodeView(node)
                        .setOffset(current_network_view.getNodeView(node).getXPosition(),
                                   current_network_view.getNodeView(node).getYPosition());
            }

            new_network_view.fitContent();

            // Set visual style
            VisualStyle newVS = vmm.getVisualStyleForView( current_network_view );

            if (newVS != null) {
                vsName = newVS.getName();
            }
        }
        vmm.setVisualStyle(vsName);
		*/
	}

	public void menuSelected(MenuEvent e) {
        CyNetwork n = Cytoscape.getCurrentNetwork();
        if ( n == null || n == Cytoscape.getNullNetwork() ) {
           	setEnabled(false); 
			return;
		}

        List<CyNode> nodes = CyDataTableUtil.getNodesInState(n,"selected",true);
        List<CyEdge> edges = CyDataTableUtil.getEdgesInState(n,"selected",true);

        if ( nodes.size() > 0 || edges.size() > 0 )
            setEnabled(true);
        else
            setEnabled(false);

	}
}
