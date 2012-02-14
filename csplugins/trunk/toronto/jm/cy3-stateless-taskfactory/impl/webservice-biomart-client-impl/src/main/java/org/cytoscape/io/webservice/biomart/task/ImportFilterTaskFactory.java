package org.cytoscape.io.webservice.biomart.task;

import org.cytoscape.io.webservice.biomart.rest.BiomartRestClient;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class ImportFilterTaskFactory extends AbstractTaskFactory {

	private final BiomartRestClient client;
	private final String datasourceName;

	public ImportFilterTaskFactory(final String datasourceName, final BiomartRestClient client) {
		this.client = client;
		this.datasourceName = datasourceName;
	}

	@Override
	public TaskIterator createTaskIterator(Object context) {
		return new TaskIterator(new ImportFilterTask(datasourceName, client));
	}
}
