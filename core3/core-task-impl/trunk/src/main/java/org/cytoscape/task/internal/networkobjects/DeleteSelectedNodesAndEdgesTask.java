/*
  File: DeleteSelectedNodesAndEdgesTask.java

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
package org.cytoscape.task.internal.networkobjects;


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.MenuEvent;
import javax.swing.KeyStroke;

import org.cytoscape.model.CyTableUtil;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.undo.UndoSupport;


public class DeleteSelectedNodesAndEdgesTask extends AbstractTask {
	private final CyTableEntry graphObj;
	private final UndoSupport undoSupport;
	private final CyRootNetworkFactory rootNetworkFactory;
	private final CyApplicationManager applicationManager;
	private final CyNetworkViewManager networkViewManager;

	/**
	 * perform deletion on the input object. if object is a Node, then this will
	 * result in also deleting the edges adjacent to the node
	 *
	 * @param obj the object to be deleted
	 */
	public DeleteSelectedNodesAndEdgesTask(final CyTableEntry obj, final UndoSupport undoSupport,
					       final CyRootNetworkFactory rootNetworkFactory,
					       final CyApplicationManager applicationManager,
					       final CyNetworkViewManager networkViewManager)
	{
		this.undoSupport = undoSupport;
		this.rootNetworkFactory = rootNetworkFactory;
		this.applicationManager = applicationManager;
		this.networkViewManager = networkViewManager;
		this.graphObj = obj;
	}

	public DeleteSelectedNodesAndEdgesTask(final UndoSupport undoSupport,
					       final CyRootNetworkFactory rootNetworkFactory,
					       final CyApplicationManager applicationManager,
					       final CyNetworkViewManager networkViewManager)
	{
		this(null, undoSupport, rootNetworkFactory, applicationManager, networkViewManager);
	}

	@Override
	public void run(final TaskMonitor taskMonitor) {
		CyNetworkView myView = applicationManager.getCurrentNetworkView();

		// delete from the base CySubNetwork so that our changes can be undone 
		CySubNetwork cyNet = rootNetworkFactory.convert(myView.getModel()).getBaseNetwork();
		List<CyEdge> selEdges = CyTableUtil.getEdgesInState(cyNet, "selected", true); 
		List<CyNode> selNodes = CyTableUtil.getNodesInState(cyNet, "selected", true); 
		CyNode cyNode;
		CyEdge cyEdge;

		// if an argument exists, add it to the appropriate list
		if (graphObj != null ) {
			if (graphObj instanceof CyNode) {
				cyNode = (CyNode) graphObj;
				if (!selNodes.contains(cyNode))
					selNodes.add(cyNode);
			} else if (graphObj instanceof CyEdge) {
				cyEdge = (CyEdge) graphObj;
				if (!selEdges.contains(cyEdge))
					selEdges.add(cyEdge);
			}
		}

		Set<CyEdge> edges = new HashSet<CyEdge>();
		Set<CyNode> nodes = new HashSet<CyNode>();

		// add all node indices
		for (int i = 0; i < selNodes.size(); i++) {
			cyNode = selNodes.get(i);
			nodes.add(cyNode);
		}

		// add all selected edge indices
		for (int i = 0; i < selEdges.size(); i++) {
			cyEdge = selEdges.get(i); 
			edges.add( cyEdge );
		}

		undoSupport.getUndoableEditSupport().postEdit(
			new DeleteEdit(cyNet, nodes, edges, this, networkViewManager));
		
		// delete the actual nodes and edges
		for (CyNode node : nodes)
			cyNet.removeNode(node);
		for (CyEdge edge : edges)
			cyNet.removeEdge(edge);

		myView.updateView();
	}
}
