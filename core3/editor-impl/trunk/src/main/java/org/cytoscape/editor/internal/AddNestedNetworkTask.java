package org.cytoscape.editor.internal;


import java.awt.geom.Point2D;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import java.util.List;
import java.util.ArrayList;

import org.cytoscape.task.AbstractNodeViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;


public class AddNestedNetworkTask extends AbstractNodeViewTask {

	@Tunable(description="Select a Network")
	public ListSingleSelection<CyNetwork> nestedNetwork;

	public AddNestedNetworkTask(View<CyNode> nv, CyNetworkView view, CyNetworkManager mgr) {
		super(nv,view);
		nestedNetwork = new ListSingleSelection<CyNetwork>(new ArrayList<CyNetwork>(mgr.getNetworkSet()));
	}

	@Override
	public void run(TaskMonitor tm) throws Exception {
		CyNode n = nodeView.getModel();
		n.setNestedNetwork( nestedNetwork.getSelectedValue() );
		netView.updateView();
	}
}
