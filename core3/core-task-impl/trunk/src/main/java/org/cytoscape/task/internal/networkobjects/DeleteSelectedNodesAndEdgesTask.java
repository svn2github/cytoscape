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
	private final UndoSupport undoSupport;
	private final CyRootNetworkFactory rootNetworkFactory;
	private final CyApplicationManager applicationManager;
	private final CyNetworkViewManager networkViewManager;

	public DeleteSelectedNodesAndEdgesTask(final UndoSupport undoSupport,
					       final CyRootNetworkFactory rootNetworkFactory,
					       final CyApplicationManager applicationManager,
					       final CyNetworkViewManager networkViewManager)
	{
		this.undoSupport = undoSupport;
		this.rootNetworkFactory = rootNetworkFactory;
		this.applicationManager = applicationManager;
		this.networkViewManager = networkViewManager;
	}

	@Override
	public void run(final TaskMonitor taskMonitor) {
		CyNetworkView myView = applicationManager.getCurrentNetworkView();

		// Delete from the base network so that our changes can be undone:
		CySubNetwork network = rootNetworkFactory.convert(myView.getModel()).getBaseNetwork();
		final List<CyNode> selectedNodes = CyTableUtil.getNodesInState(network, "selected", true); 
		final List<CyEdge> selectedEdges = CyTableUtil.getEdgesInState(network, "selected", true); 

		undoSupport.getUndoableEditSupport().postEdit(
			new DeleteEdit(network, selectedNodes, selectedEdges, this, networkViewManager));
		
		// Delete the actual nodes and edges:
		for (CyNode selectedNode : selectedNodes)
			network.removeNode(selectedNode);
		for (CyEdge selectedEdge : selectedEdges)
			network.removeEdge(selectedEdge);

		myView.updateView();
	}
}
