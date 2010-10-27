package org.cytoscape.work;


/**
 * Executes a <code>Task</code>.
 *
 * @author Pasteur
 */
public interface TaskManager {
	/** Tests an object for having tunable annotations.
	 *
	 *  @return true if "o" has tunable annotations and else false.
	 */
	boolean hasTunables(final Object o);

	/**
	 * This method is called to execute a <code>Task</code>.
	 *
	 * This method returns once the <code>Task</code>s derived from the <code>TaskIterator</code>
	 * returned by the <code>TaskFactory</code>'s <code>getTaskIterator()</code> method started execution.
	 * It does not wait for the <code>Task</code>s to finish.
	 *
	 *  @param factory  the <code>TaskFactory</code> whose tasks returned by its getTaskIterator()
	 *         method's iterator will be executed
	 */
	void execute(TaskFactory factory);
}
