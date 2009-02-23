package org.cytoscape.work;

/**
 * Describes a unit of work to be executed in its own
 * <code>Thread</code> along with a user interface to
 * display its progress, provide a means for the user to
 * cancel the <code>Task</code>, and show information
 * about any <code>Exception</code>s thrown during its
 * execution.
 *
 * <code>Task</code> is executed by calling a <code>TaskManager</code>'s
 * <code>execute</code> method. <code>TaskManager</code> will setup
 * the <code>Task</code>'s user interface, create a <code>TaskMonitor</code>
 * for the <code>Task</code> that allows the <code>Task</code> to modify
 * the user interface, and run the <code>Task</code> in its own thread.
 */
public interface Task
{
	/**
	 * This method is called by the <code>Task</code>'s own thread
	 * created by <code>TaskManager</code>.
	 *
	 * This method should not be called directly by the programmer.
	 *
	 * @param taskMonitor This is provided by <code>TaskManager</code>
	 * to allow the <code>Task</code> to modify its user interface.
	 *
	 * @throws Exception The <code>Task</code> is at liberty to
	 * throw any exceptions in <code>run</code>. The exception is
	 * caught by <code>TaskManager</code> and the information contained
	 * by the exception is displayed in the interface.
	 */
	public void run(TaskMonitor taskMonitor) throws Exception;

	/**
	 * This method is called when the user chooses to cancel the
	 * <code>Task</code>.
	 *
	 * This method should not be called directly by the programmer.
	 *
	 * This method should inform the <code>Task</code> that it must
	 * terminate execution cleanly and do any necessary cleanup
	 * work required.
	 *
	 * <i>WARNING:</i> this method is called by a different
	 * thread than the thread executing <code>run</code>.
	 * The programmer <i>must</i> be aware of
	 * concurrency issues.
	 */
	public void cancel();
}
