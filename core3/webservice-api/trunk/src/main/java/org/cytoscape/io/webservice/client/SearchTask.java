package org.cytoscape.io.webservice.client;

import org.cytoscape.work.Task;


/**
 * All web service clients which have search function should implement this interface.
 *
 */
public interface SearchTask<T> extends Task {
	T search();
}
