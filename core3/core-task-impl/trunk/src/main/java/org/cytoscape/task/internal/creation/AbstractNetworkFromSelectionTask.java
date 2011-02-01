/*
 File: AbstractNetworkFromSelectionTask.java

 Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

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


import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.session.CyNetworkNaming;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.session.CyApplicationManager;

import java.util.Set;
import java.util.List;
import java.util.Collection;


abstract class AbstractNetworkFromSelectionTask extends AbstractCreationTask {

    protected final CyRootNetworkFactory cyroot;
    protected final CyNetworkViewFactory cnvf;
    protected final VisualMappingManager vmm;
    protected final CyNetworkNaming cyNetworkNaming;
	protected final RenderingEngineManager reManager;

    public AbstractNetworkFromSelectionTask(final CyNetwork net, final CyRootNetworkFactory cyroot,
                           final CyNetworkViewFactory cnvf, final CyNetworkManager netmgr,
                           final CyNetworkViewManager networkViewManager,
                           final CyNetworkNaming cyNetworkNaming,
                           final VisualMappingManager vmm, final RenderingEngineManager reManager)
	{
		super(net, netmgr, networkViewManager);
		this.cyroot = cyroot;
		this.cnvf = cnvf;
		this.cyNetworkNaming = cyNetworkNaming;
		this.vmm = vmm;
		this.reManager = reManager;
	}

	abstract Collection<CyEdge> getEdges(CyNetwork netx, List<CyNode> nodes);

	public void run(TaskMonitor tm) {
		if (net == null) 
			throw new NullPointerException("Null current network!");

		// rename network to keep code comprehensible
        CyNetwork currNet = net; 
        CyNetworkView currView = networkViewManager.getNetworkView(currNet.getSUID());
        final RenderingEngine<?> re = reManager.getRendringEngine(currView);

		// Get the selected nodes, but only create network if nodes are actually selected.
		List<CyNode> nodes = CyTableUtil.getNodesInState(currNet,"selected",true);

		if ( nodes.size() <= 0 )
			throw new IllegalArgumentException("No nodes selected!");

		// create subnetwork and add selected nodes and appropriate edges
		final CySubNetwork newNet = cyroot.convert(currNet).addSubNetwork();

		for ( CyNode node : nodes )
			newNet.addNode(node);

		for ( CyEdge edge : getEdges(currNet,nodes) )
			newNet.addEdge(edge);

		newNet.getCyRow().set(CyTableEntry.NAME,
				      cyNetworkNaming.getSuggestedSubnetworkTitle(currNet));

		networkManager.addNetwork(newNet);

		if (currView == null)
			return;

		// create new view
		CyNetworkView newView = cnvf.getNetworkView(newNet);
        

		networkViewManager.addNetworkView(newView);
	
		// copy node location only.
		for ( View<CyNode> newNodeView : newView.getNodeViews() ) {
			View<CyNode> origNodeView = currView.getNodeView( newNodeView.getModel() );
			newNodeView.setVisualProperty(TwoDVisualLexicon.NODE_X_LOCATION, origNodeView.getVisualProperty(TwoDVisualLexicon.NODE_X_LOCATION));
			newNodeView.setVisualProperty(TwoDVisualLexicon.NODE_Y_LOCATION, origNodeView.getVisualProperty(TwoDVisualLexicon.NODE_Y_LOCATION));

			// FIXME
//			// Set lock (if necessary)
//			for ( VisualProperty<?> vp : vpSet ) {
//				if (origNodeView.isValueLocked(vp) )
//					newNodeView.setLockedValue(vp, origNodeView.getVisualProperty(vp));
//			}
		}
        
		final VisualStyle style = vmm.getVisualStyle(currView);
        vmm.setVisualStyle(vmm.getVisualStyle(currView),newView);
        style.apply(newView);
		newView.fitContent();
	}
}
