package org.cytoscape.webservice.biomart.task;

import org.cytoscape.webservice.biomart.rest.BiomartRestClient;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class ImportFilterTaskFactory implements TaskFactory {

	private final BiomartRestClient client;
	private final String datasourceName;

	public ImportFilterTaskFactory(final String datasourceName, final BiomartRestClient client) {
		this.client = client;
		this.datasourceName = datasourceName;
	}

	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new ImportFilterTask(datasourceName, client));
	}

}
