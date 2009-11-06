package org.cytoscape.work;

/**
 * Returns an instance of a Task.  Intended to be 
 * used as an OSGi server.
 */
public interface TaskFactory {

	Task getTask();
}
