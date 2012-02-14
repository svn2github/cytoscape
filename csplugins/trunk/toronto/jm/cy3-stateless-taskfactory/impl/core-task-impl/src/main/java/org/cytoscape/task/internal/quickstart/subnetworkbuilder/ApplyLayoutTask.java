package org.cytoscape.task.internal.quickstart.subnetworkbuilder;

import org.cytoscape.task.NetworkViewTaskContext;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class ApplyLayoutTask extends AbstractTask {

    private final SubnetworkBuilderUtil util;
    private final CyLayoutAlgorithmManager layouts;

    ApplyLayoutTask(final SubnetworkBuilderUtil util, final CyLayoutAlgorithmManager layouts) {
	this.layouts = layouts;
	this.util = util;
    }

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setProgress(0.0);
//	final CyLayoutAlgorithm layout = layouts.getLayout(CyLayoutAlgorithmManager.DEFAULT_LAYOUT_NAME);
	final CyLayoutAlgorithm<NetworkViewTaskContext> layout = layouts.getDefaultLayout();
	taskMonitor.setProgress(0.1);
	NetworkViewTaskContext context = layout.createTaskContext();
	context.setNetworkView(util.appManager.getCurrentNetworkView());
	taskMonitor.setProgress(0.2);
	insertTasksAfterCurrentTask(layout.createTaskIterator(context));
	
	taskMonitor.setProgress(1.0);
    }

}
