package org.cytoscape.work;


/**
 *  Returns an instance of a TaskIterator.  Intended to be 
 *  used as an OSGi service, singleton, every type of <code>Task</code> has one.
 */
public interface TaskFactory {
	/** @return an iterator returning a sequence of <code>Task</code>s.
	 *
	 * N.B.: Most factory's returned iterator only yields a single <code>Task</code>.
	 */
	TaskIterator getTaskIterator();
}
