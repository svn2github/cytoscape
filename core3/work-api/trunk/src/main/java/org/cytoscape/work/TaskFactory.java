package org.cytoscape.work;


/**
 * Returns an instance of a TaskIterator.  Intended to be 
 * used as an OSGi service, singleton, ebery task has one.
 */
public interface TaskFactory {
	TaskIterator getTaskIterator();
}
