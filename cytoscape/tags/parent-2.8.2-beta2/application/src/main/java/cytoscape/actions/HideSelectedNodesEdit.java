/*
  File: HideSelectedNodesEdit.java

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
package cytoscape.actions;


import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import giny.view.NodeView;

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.util.undo.CyAbstractEdit;


/**
 * An undoable edit that will undo and redo hiding of selected nodes.
 */ 
class HideSelectedNodesEdit extends CyAbstractEdit {
	private static final long serialVersionUID = -1146181528012954334L;

	private final CyNetworkView networkView;
	private final List<NodeView> hiddenNodeViews;
	private final HideSelectedNodesAction hideSelectedNodesAction;

	HideSelectedNodesEdit(final CyNetworkView networkView, final List<NodeView> hiddenNodeViews,
			      final HideSelectedNodesAction hideSelectedNodesAction)
	{
		super(HideSelectedNodesAction.MENU_LABEL);

		this.networkView = networkView;
		this.hiddenNodeViews = hiddenNodeViews;
		this.hideSelectedNodesAction = hideSelectedNodesAction;
	}

	public void redo() {
		super.redo();

		final CyNetworkView view = Cytoscape.getCurrentNetworkView();
		final List<NodeView> hiddenNodeViews = new ArrayList<NodeView>();
		for (Iterator i = view.getSelectedNodes().iterator(); i.hasNext(); /* Empty! */)
			hiddenNodeViews.add((NodeView)i.next());
		GinyUtils.hideSelectedNodes(view);


		hideSelectedNodesAction.setEnabled(false);
	}

	public void undo() {
	 	super.undo();

		for (final NodeView nodeView : hiddenNodeViews) {
			networkView.showGraphObject(nodeView);
			nodeView.setSelected(true);
			GinyUtils.showEdges(networkView, nodeView);
		}

		networkView.updateView();

		hideSelectedNodesAction.setEnabled(true);
	}
}
