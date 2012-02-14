package org.cytoscape.io.webservice.biomart.task;

import org.cytoscape.io.webservice.biomart.rest.BiomartRestClient;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class ImportAttributeListTaskFactory extends AbstractTaskFactory {
	
	private String datasourceName;
	private BiomartRestClient client;

	public ImportAttributeListTaskFactory(final String datasourceName, final BiomartRestClient client) {
		this.datasourceName = datasourceName;
		this.client = client;
	}

	@Override
	public TaskIterator createTaskIterator(Object context) {
		return new TaskIterator(new ImportAttributeListTask(datasourceName, client));
	}

}
