package org.cytoscape.task.internal.quickstart;

import org.cytoscape.task.internal.quickstart.subnetworkbuilder.SearchRelatedGenesTask;
import org.cytoscape.task.internal.quickstart.subnetworkbuilder.SubnetworkBuilderState;
import org.cytoscape.task.internal.quickstart.subnetworkbuilder.SubnetworkBuilderUtil;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

public class SelectNextTask extends AbstractTask {
	
	private static final String SEARCH_OPTION = "Search this network and create subnetwork";
	private static final String FINISH_OPTION = "Finished";
	
	@Tunable(description = "What would you like to do next?")
	public ListSingleSelection<String> selection = new ListSingleSelection<String>(SEARCH_OPTION, FINISH_OPTION);
	
	private final SubnetworkBuilderUtil subnetworkUtil;

	
	SelectNextTask(final SubnetworkBuilderUtil subnetworkUtil) {
		this.subnetworkUtil = subnetworkUtil;
	}
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		final String selected = selection.getSelectedValue();
		
		if(selected == SEARCH_OPTION)
			insertTasksAfterCurrentTask(new SearchRelatedGenesTask(subnetworkUtil, new SubnetworkBuilderState()));
	}

}
