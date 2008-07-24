
package org.cytoscape.work;

/**
 * An interface that indicates that a particular implementation 
 * can accept and use a {@link TaskMonitor} object to report
 * the progress of a task.
 */
public interface Monitorable {

	public void setTaskMonitor( TaskMonitor t );

	public TaskMonitor getTaskMonitor();
}
