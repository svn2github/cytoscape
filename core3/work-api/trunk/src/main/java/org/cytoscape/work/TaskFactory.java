package org.cytoscape.work;


/**
 * Returns an instance of a TaskIterator.  Intended to be 
 * used as an OSGi server.
 */
public interface TaskFactory {
	TaskIterator getTaskIterator();
}
