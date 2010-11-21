package org.cytoscape.editor.internal;

import org.cytoscape.task.AbstractNodeViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskMonitor;


public class AddEdgeTask extends AbstractNodeViewTask {

	private static CyNode sourceNode = null;

	
	public AddEdgeTask(View<CyNode> nv, CyNetworkView view) {
		super(nv,view);
	}

	@Override
	public void run(TaskMonitor tm) throws Exception {
		System.out.println("Running add edge - sourceNode: " + sourceNode);
		if ( sourceNode == null ) {
			sourceNode = nodeView.getModel();
		} else {
			CyNetwork net = netView.getModel();
			CyNode targetNode = nodeView.getModel();
			net.addEdge(sourceNode,targetNode,true);
			netView.updateView();
			sourceNode = null;
		}
	}
}
