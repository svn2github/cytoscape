package org.cytoscape.io.webservice.client;

import org.cytoscape.work.TaskFactory;

/**
 * Task factory for ALL web service clients.
 * Search/fetch query parameter will be set through this factory.
 *
 */
public interface WebServiceClientTaskFactory extends TaskFactory {
	
	void setQuery(final Query quqey);
}
