package org.cytoscape.task.internal.quickstart;

import org.cytoscape.task.internal.quickstart.subnetworkbuilder.SubnetworkBuilderUtil;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class SimplifiedQuickStartTask extends AbstractTask {
	
	private final SubnetworkBuilderUtil subnetworkUtil;
	
	SimplifiedQuickStartTask(final SubnetworkBuilderUtil subnetworkUtil) {
		this.subnetworkUtil = subnetworkUtil;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		insertTasksAfterCurrentTask(new SelectNextTask(subnetworkUtil));
		insertTasksAfterCurrentTask(subnetworkUtil.getWebServiceImportTask());
	}

}
