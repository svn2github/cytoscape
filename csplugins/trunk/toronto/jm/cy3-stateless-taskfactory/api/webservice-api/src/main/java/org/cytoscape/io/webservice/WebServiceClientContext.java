package org.cytoscape.io.webservice;

import java.awt.Container;


public interface WebServiceClientContext {
	/**
	 * Returns query builder UI.  Since this is a TaskFactory, 
	 * createTaskIterator() method should use parameters from this GUI.
	 * 
	 * @return query builder UI.
	 */
	Container getQueryBuilderGUI();

	void setQueryBuilderGUI(Container gui);
	
	/**
	 * Set query for the tasks to be executed.
	 * 
	 * @param query query object.  This is client-dependent.
	 */
	void setQuery(Object query);
	
	Object getQuery();
}
