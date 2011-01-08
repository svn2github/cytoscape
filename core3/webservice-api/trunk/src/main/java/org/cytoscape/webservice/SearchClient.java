package org.cytoscape.webservice;


/**
 * All web service clients which have search function should implement this interface.
 *
 */
public interface SearchClient {
	Object search(Object query);
}
