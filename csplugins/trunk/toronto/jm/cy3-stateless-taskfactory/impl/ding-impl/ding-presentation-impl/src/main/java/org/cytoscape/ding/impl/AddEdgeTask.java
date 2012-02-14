
package org.cytoscape.ding.impl; 

import java.awt.datatransfer.Transferable;

import org.cytoscape.dnd.DropNodeViewTaskContext;
import org.cytoscape.dnd.DropUtil;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNodeViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AddEdgeTask extends AbstractNodeViewTask {

	private final Transferable t;

	private static final Logger logger = LoggerFactory.getLogger(AddEdgeTask.class);

	public AddEdgeTask(DropNodeViewTaskContext context, Transferable t) {
		super(context.getNodeView(), context.getNetworkView());
		this.t = t;
	}

	@Override
	public void run(TaskMonitor tm) throws Exception {
		if ( !DropUtil.transferableMatches(t,"Edge") ) {
			logger.warn("Transferable object does not match expected type (Edge) for task.");
			return;
		}

		CyNode sourceNode = AddEdgeStateMonitor.getSourceNode(netView);
		if ( sourceNode == null ) {
			AddEdgeStateMonitor.setSourceNode(netView,nodeView.getModel());
		} else {
			CyNetwork net = netView.getModel();
			CyNode targetNode = nodeView.getModel();
			net.addEdge(sourceNode,targetNode,true);
			netView.updateView();
			AddEdgeStateMonitor.setSourceNode(netView,null);
		}
	}
}
