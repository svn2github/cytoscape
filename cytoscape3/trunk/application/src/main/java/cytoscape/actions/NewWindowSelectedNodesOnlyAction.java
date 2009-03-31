/*
 File: NewWindowSelectedNodesOnlyAction.java

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
// $Revision: 13022 $
// $Date: 2008-02-11 13:59:26 -0800 (Mon, 11 Feb 2008) $
// $Author: mes $
//-------------------------------------------------------------------------
package cytoscape.actions;

import cytoscape.CyNetworkManager;

import cytoscape.util.CytoscapeAction;
import cytoscape.util.CyNetworkNaming;

import org.cytoscape.model.CyDataTableUtil;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.model.subnetwork.CySubNetwork;

import org.cytoscape.view.GraphView;
import org.cytoscape.view.GraphViewFactory;

import java.awt.event.ActionEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.MenuEvent;


/**
 *
 */
public class NewWindowSelectedNodesOnlyAction extends CytoscapeAction {
	private final static long serialVersionUID = 1202339870134859L;
	private final CyRootNetworkFactory cyroot;
	private final GraphViewFactory gvf;
	
	// TODO: use new VMM
//	private VisualMappingManager vmm;
	
	private CyNetworkNaming cyNetworkNaming;

	/**
	 * Creates a new NewWindowSelectedNodesOnlyAction object.
	 */
	public NewWindowSelectedNodesOnlyAction(final CyRootNetworkFactory r, final GraphViewFactory gvf, CyNetworkManager netmgr, CyNetworkNaming cyNetworkNaming) {
		super("From selected nodes, all edges",netmgr);
		setPreferredMenu("File.New.Network");
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_N, ActionEvent.CTRL_MASK);
		cyroot = r;
		this.gvf = gvf;
		this.cyNetworkNaming = cyNetworkNaming;
	}
	
	public void setNamingUtil(CyNetworkNaming namingUtil) {
		this.cyNetworkNaming = namingUtil;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		// save the vizmapper catalog
		CyNetwork current_network = netmgr.getCurrentNetwork();

		if (current_network == null)
			return;

		GraphView current_network_view = null;

		if (netmgr.viewExists(current_network.getSUID())) {
			current_network_view = netmgr.getNetworkView(current_network.getSUID());
		}

		List<CyNode> nodes = CyDataTableUtil.getNodesInState(current_network, "selected", true);

		Set<CyEdge> edges = new HashSet<CyEdge>();

		for (CyNode n1 : nodes) {
			for (CyNode n2 : nodes) {
				edges.addAll(current_network.getConnectingEdgeList(n1, n2, CyEdge.Type.ANY));
			}
		}

		CySubNetwork new_network = cyroot.convert(current_network)
		                                 .addSubNetwork(nodes, new ArrayList<CyEdge>(edges));
		new_network.attrs().set("name", cyNetworkNaming.getSuggestedSubnetworkTitle(current_network,netmgr));

		GraphView new_view = gvf.createGraphView(new_network);

		if (new_view == null) {
			return;
		}

		String vsName = "default";

		// keep the node positions
		if (current_network_view != null) {
			for (CyNode node : new_network.getNodeList()) {
				new_view.getNodeView(node)
				        .setOffset(current_network_view.getNodeView(node).getXPosition(),
				                   current_network_view.getNodeView(node).getYPosition());
			}

			// TODO NEED RENDERER
			new_view.fitContent();

			// Set visual style
//			VisualStyle newVS = vmm.getVisualStyleForView(current_network_view);
//
//			if (newVS != null) {
//				vsName = newVS.getName();
//				vmm.setVisualStyleForView(new_view, newVS);
//			}
		}

		//vmm.setVisualStyle(vsName);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void menuSelected(MenuEvent e) {
		CyNetwork n = netmgr.getCurrentNetwork();

		if (n == null) {
			setEnabled(false);

			return;
		}

		List<CyNode> nodes = CyDataTableUtil.getNodesInState(n, "selected", true);

		if (nodes.size() > 0)
			setEnabled(true);
		else
			setEnabled(false);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param vmm DOCUMENT ME!
	 */
//	public void setVmm(VisualMappingManager vmm) {
//		this.vmm = vmm;
//	}
}
