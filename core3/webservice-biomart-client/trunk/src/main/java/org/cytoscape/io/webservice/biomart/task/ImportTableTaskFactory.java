package org.cytoscape.io.webservice.biomart.task;

import org.cytoscape.io.webservice.biomart.BiomartClient;
import org.cytoscape.io.webservice.biomart.BiomartQuery;
import org.cytoscape.io.webservice.client.Query;
import org.cytoscape.io.webservice.client.WebServiceClientTaskFactory;
import org.cytoscape.work.TaskIterator;

public class ImportTableTaskFactory implements WebServiceClientTaskFactory {
	
	private final BiomartClient client;
	
	private BiomartQuery query;

	public ImportTableTaskFactory(final BiomartClient client) {
		this.client = client;
	}

	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new ImportTableTask(client, query));
	}

	@Override
	public void setQuery(Query query) {
		if(query instanceof BiomartQuery == false)
			throw new IllegalArgumentException("Query object is not compatible.");
		
		this.query = (BiomartQuery) query; 
	}

}
