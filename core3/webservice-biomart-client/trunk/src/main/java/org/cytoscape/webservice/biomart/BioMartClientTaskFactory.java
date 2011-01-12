package org.cytoscape.webservice.biomart;

import java.io.IOException;

import org.cytoscape.io.webservice.client.Query;
import org.cytoscape.io.webservice.client.WebServiceClientTaskFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.webservice.biomart.rest.BiomartRestClient;
import org.cytoscape.work.TaskIterator;

public class BioMartClientTaskFactory implements WebServiceClientTaskFactory {
	
	private final BiomartClient client;
	
	public BioMartClientTaskFactory(final CyTableFactory tableFactory, final CyNetworkManager manager,
			final String uri, final String displayName, final String description) throws IOException {
		
		if (tableFactory == null)
			throw new NullPointerException("tableFactory is null");
		
		this.client = new BiomartClient(uri, displayName, description, new BiomartRestClient(uri), tableFactory, manager);
	}

	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator();
	}

	@Override
	public void setQuery(final Query query) {
		if(query == null)
			throw new NullPointerException("Query is null.");
		
		client.setQuery(query);
	}
	
	

}
