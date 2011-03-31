/*
  File: CloneNetworkTask.java

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


import java.util.HashMap;
import java.util.Map;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.model.events.RowsAboutToChangeEvent;
import org.cytoscape.model.events.RowsFinishedChangingEvent;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.presentation.property.MinimalVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskMonitor;


public class CloneNetworkTask extends AbstractCreationTask {
	private Map<CyNode,CyNode> origNewNodeMap;
	private Map<CyEdge,CyEdge> origNewEdgeMap;
	private final VisualMappingManager vmm;
	private final CyNetworkFactory netFactory;
	private final CyNetworkViewFactory netViewFactory;
	private final RenderingEngineManager reManager;
	private final CyNetworkNaming naming;
	private final CyEventHelper eventHelper;

	public CloneNetworkTask(final CyNetwork net, final CyNetworkManager netmgr,
    		                final CyNetworkViewManager networkViewManager,
				final VisualMappingManager vmm, final CyNetworkFactory netFactory,
				final CyNetworkViewFactory netViewFactory,
    		                final RenderingEngineManager reManager,
				final CyNetworkNaming naming, final CyEventHelper eventHelper)
	{
		super(net, netmgr, networkViewManager);
		this.vmm            = vmm;
		this.netFactory     = netFactory;
		this.netViewFactory = netViewFactory;
		this.reManager      = reManager;
		this.naming         = naming;
		this.eventHelper    = eventHelper;
	}

	public void run(TaskMonitor tm) {
		CyNetwork newNet = cloneNetwork(net);
		CyNetworkView origView = networkViewManager.getNetworkView(net.getSUID());
		final RenderingEngine<?> re = reManager.getRendringEngine(origView);

		networkManager.addNetwork(newNet);
		if ( origView != null ) {
			CyNetworkView newView = netViewFactory.getNetworkView(newNet);

			// Copy locaitons since this is controlled outside of visual style.
			for (final View<CyNode> newNodeView : newView.getNodeViews()) {
				final View<CyNode> origNodeView = origView.getNodeView( newNodeView.getModel() );
				newNodeView.setVisualProperty(MinimalVisualLexicon.NODE_X_LOCATION,
							      origNodeView.getVisualProperty(MinimalVisualLexicon.NODE_X_LOCATION));
				newNodeView.setVisualProperty(MinimalVisualLexicon.NODE_Y_LOCATION,
							      origNodeView.getVisualProperty(MinimalVisualLexicon.NODE_Y_LOCATION));
			}

			vmm.setVisualStyle(vmm.getVisualStyle(origView), newView );
			vmm.getVisualStyle(origView).apply(newView);
			networkViewManager.addNetworkView(newView);
			newView.fitContent();
		}
	}

	private CyNetwork cloneNetwork(CyNetwork origNet) {
		final CyNetwork newNet = netFactory.getInstance();

		// copy default columns
		cloneColumns( origNet.getDefaultNodeTable(), newNet.getDefaultNodeTable() );
		cloneColumns( origNet.getDefaultEdgeTable(), newNet.getDefaultEdgeTable() );
		cloneColumns( origNet.getDefaultNetworkTable(), newNet.getDefaultNetworkTable() );

		cloneNodes( origNet, newNet );
		cloneEdges( origNet, newNet );

		newNet.getCyRow().set(CyTableEntry.NAME,
				      naming.getSuggestedNetworkTitle(origNet.getCyRow().get(CyTableEntry.NAME, String.class)));

		return newNet;
	}

	private void cloneNodes( CyNetwork origNet, CyNetwork newNet ) {
		origNewNodeMap = new HashMap<CyNode,CyNode>();
		for ( final CyNode origNode : origNet.getNodeList() ) {
			final CyNode newNode = newNet.addNode();
			origNewNodeMap.put( origNode, newNode );
			cloneRow( origNode.getCyRow(), newNode.getCyRow() );
		}
	}

	private void cloneEdges( CyNetwork origNet, CyNetwork newNet ) {
		origNewEdgeMap = new HashMap<CyEdge,CyEdge>();
		for ( final CyEdge origEdge : origNet.getEdgeList() ) {
			final CyNode newSource = origNewNodeMap.get( origEdge.getSource() );
			final CyNode newTarget = origNewNodeMap.get( origEdge.getTarget() );
			final boolean newDirected = origEdge.isDirected();
			final CyEdge newEdge = newNet.addEdge( newSource, newTarget, newDirected );
			origNewEdgeMap.put( origEdge, newEdge );
			cloneRow( origEdge.getCyRow(), newEdge.getCyRow() );
		}
	}

	private void cloneColumns(final CyTable from, final CyTable to) {
		for (final CyColumn fromColumn : from.getColumns()) {
			final CyColumn toColumn = to.getColumn(fromColumn.getName());
			if (toColumn == null )
				to.createColumn(fromColumn.getName(), fromColumn.getType(), false);
			else if (toColumn.getType() == fromColumn.getType()) {
				continue;
			} else {
				throw new IllegalArgumentException("column of same name: "
								   + fromColumn.getName() +
								   "but types don't match (orig): " +
								   fromColumn.getType().getName()
								   + " (new): "
								   + toColumn.getType().getName());
			}
		}
	}

	private void cloneRow(final CyRow from, final CyRow to) {
		try {
			eventHelper.fireSynchronousEvent(new RowsAboutToChangeEvent(this, to.getTable()));

			for (final CyColumn column : from.getTable().getColumns())
				to.set(column.getName(), from.getRaw(column.getName()));
		} finally {
			eventHelper.fireSynchronousEvent(new RowsFinishedChangingEvent(this, to.getTable()));
		}
	}
}
