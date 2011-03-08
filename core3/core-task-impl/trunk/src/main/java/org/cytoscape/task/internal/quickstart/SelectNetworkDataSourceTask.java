package org.cytoscape.task.internal.quickstart;

import org.cytoscape.task.internal.quickstart.SelectNetworkDataSourceTask.DataSourceType;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

public class SelectNetworkDataSourceTask extends AbstractTask {

	enum DataSourceType {
		FILE_OR_URL, WEB_SERVICE;
	}

	@Tunable(description = "Select Data Source Type")
	public ListSingleSelection<DataSourceType> dataSource = new ListSingleSelection<DataSourceType>(
			DataSourceType.FILE_OR_URL, DataSourceType.WEB_SERVICE);

	private final QuickStartState state;

	SelectNetworkDataSourceTask(final QuickStartState state) {
		this.state = state;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		final DataSourceType selected = dataSource.getSelectedValue();
		if(selected == DataSourceType.FILE_OR_URL)
			insertTasksAfterCurrentTask(new SelectNetworkIdTypeTask(state));
		else {
			
		}
	}

}
