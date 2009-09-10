package org.cytoscape.work;

/**
 * Executes a <code>Task</code>.
 *
 * @author Pasteur
 */
public interface TaskManager
{
	/**
	 * This method is called to execute a <code>Task</code>.
	 *
	 * This method returns once the <code>Task</code> has
	 * started execution. It does not wait for the
	 * <code>Task</code> to finish.
	 */
	void execute(Task task);
}
