/*
 File: NewNetworkSelectedNodesOnlyTask.java

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

package org.cytoscape.task.internal.creation;

import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_LOCATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cytoscape.model.CyDataTableUtil;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.View;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.session.CyNetworkNaming;

public class NewNetworkSelectedNodesOnlyTask extends AbstractCreationTask {

	private final CyRootNetworkFactory cyroot;
	private final CyNetworkViewFactory cnvf;
	private final VisualMappingManager vmm;
	private final CyNetworkNaming cyNetworkNaming;

	public NewNetworkSelectedNodesOnlyTask(final CyRootNetworkFactory cyroot, final CyNetworkViewFactory cnvf, final CyNetworkManager netmgr, final CyNetworkNaming cyNetworkNaming, final VisualMappingManager vmm) {
		super(netmgr);
		this.cyroot = cyroot;
		this.cnvf = cnvf;
		this.cyNetworkNaming = cyNetworkNaming;
		this.vmm = vmm;
	}
	
	public void run(TaskMonitor tm) {

		CyNetwork current_network = netmgr.getCurrentNetwork();

		if (current_network == null)
			return;

		CyNetworkView current_network_view = null;

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
		new_network.attrs().set("name", cyNetworkNaming.getSuggestedSubnetworkTitle(current_network));

		CyNetworkView new_view = cnvf.getNetworkViewFor(new_network);

		if (new_view == null) {
			return;
		}

		String vsName = "default";

		// keep the node positions
		if (current_network_view != null) {
			for (CyNode node : new_network.getNodeList()) {
				View<CyNode> nv = new_view.getNodeView(node);
				nv.setVisualProperty(NODE_X_LOCATION, current_network_view.getNodeView(node).getVisualProperty(NODE_X_LOCATION));
				nv.setVisualProperty(NODE_Y_LOCATION, current_network_view.getNodeView(node).getVisualProperty(NODE_Y_LOCATION));
			}

			// TODO NEED RENDERER
			new_view.fitContent();

			// Set visual style
			VisualStyle newVS = vmm.getVisualStyle(current_network_view);
			if (newVS != null) 
				vmm.setVisualStyle(newVS,new_view);
		}
	}
}
