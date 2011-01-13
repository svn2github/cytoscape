package org.cytoscape.io.webservice.biomart.task;

import org.cytoscape.io.webservice.biomart.BiomartClient;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;

public final class ShowBiomartGUITaskFactory implements TaskFactory {

	final BiomartClient client;
	final TaskManager taskManager;
	final CyApplicationManager appManager;
	final CyTableManager tblManager;

	public ShowBiomartGUITaskFactory(final BiomartClient client,
			final TaskManager taskManager,
			final CyApplicationManager appManager,
			final CyTableManager tblManager) {
		this.client = client;
		this.taskManager = taskManager;
		this.appManager = appManager;
		this.tblManager = tblManager;

	}

	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new ShowBiomartGUITask(client, taskManager, appManager, tblManager));
	}

}
