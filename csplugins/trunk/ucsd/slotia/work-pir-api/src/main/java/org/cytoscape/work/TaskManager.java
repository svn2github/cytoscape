package org.cytoscape.work;

/**
 * Executes a <code>Task</code>.
 *
 * <p><code>TaskManager</code> has the following responsibilities:</p>
 * <ul>
 *
 * <li>It executes <code>Task</code> in a thread separate from the thread
 * calling <code>execute</code>.</li>
 *
 * <li>Sets up a user interface to display the progress of a
 * <code>Task</code></li>.
 *
 * <li>Provides a <code>TaskMonitor</code> so the <code>Task</code> can modify
 * its interface during its execution.</li>
 *
 * <li>Catches exceptions thrown by <code>Task</code> and displays it
 * in its user interface.</li>
 *
 * </ul>
 */
public interface TaskManager
{
	/**
	 * This method is called to execute a <code>Task</code>.
	 *
	 * This method should return when it has finished setting up
	 * <code>Task</code>. It will <i>not</i> return when the
	 * <code>Task</code> has finished execution.
	 */
	public void execute(Task task);
}
