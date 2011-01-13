package org.cytoscape.io.webservice.biomart.task;

import org.cytoscape.io.webservice.biomart.rest.BiomartRestClient;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class ImportAttributeListTaskFactory implements TaskFactory {
	
	private final BiomartRestClient client;
	private final String datasourceName;

	public ImportAttributeListTaskFactory(final String datasourceName, final BiomartRestClient client) {
		this.client = client;
		this.datasourceName = datasourceName;
	}

	@Override
	public TaskIterator getTaskIterator() {
		
		return new TaskIterator(new ImportAttributeListTask(datasourceName, client));
	}

}
