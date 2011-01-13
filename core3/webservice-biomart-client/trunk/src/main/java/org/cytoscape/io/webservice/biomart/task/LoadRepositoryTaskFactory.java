package org.cytoscape.io.webservice.biomart.task;

import org.cytoscape.io.webservice.biomart.rest.BiomartRestClient;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class LoadRepositoryTaskFactory implements TaskFactory {
	
	private final BiomartRestClient client;
	
	public LoadRepositoryTaskFactory(final BiomartRestClient client) {
		this.client = client;
	}

	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new LoadRepositoryTask(client));
	}

}
