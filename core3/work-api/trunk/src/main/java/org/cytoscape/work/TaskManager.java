package org.cytoscape.work;

/**
 * Executes a <code>Task</code>.
 *
 * <p><code>TaskManager</code> has the following responsibilities:</p>
 * <ul>
 *
 * <li>executes <code>Task</code> in a thread separate from the thread
 * calling <code>execute</code>;</li>
 *
 * <li>sets up a user interface to display the progress of a
 * <code>Task</code>;</li>
 *
 * <li>provides a <code>TaskMonitor</code> so the <code>Task</code> can modify
 * its interface during its execution;</li>
 *
 * <li>catches exceptions thrown by <code>Task</code> and displays it
 * in its user interface.</li>
 *
 * </ul>
 *
 * @author Samad Lotia
 */
public interface TaskManager
{
	/**
	 * This method is called to execute a <code>Task</code>.
	 *
	 * This method returns once the <code>Task</code> has
	 * started execution.
	 */
	public void execute(Task task);
}
