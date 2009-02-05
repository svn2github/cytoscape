package org.cytoscape.work;

/**
 * This interface is identical to <code>Task</code>, except it provides the means
 * for <code>run</code> to produce a result. This interface is analogous to
 * <code>Future</code>.
 * Because a <code>ValuedTask</code> cannot be executed by a
 * <code>TaskManager</code>, an instance of this interface is typically wrapped
 * by an instance of <code>ValuedTaskExecutor</code> so that it can be
 * executed by a <code>TaskManager</code>.
 */
public interface ValuedTask<V>
{
	/**
	 * This method is eventually called by the <code>Task</code>'s own thread
	 * created by <code>TaskManager</code>.
	 *
	 * This method should not be called directly by the programmer.
	 *
	 * @return a useful result to be retrieved by another thread
	 * after the execution of this <code>ValuedTask</code> has completed.
	 *
	 * @param taskMonitor This is provided by <code>TaskManager</code>
	 * to allow the <code>ValuedTask</code> to modify its user interface.
	 *
	 * @throws Exception The <code>ValuedTask</code> is at liberty to
	 * throw any exceptions in <code>run</code>. The exception is
	 * caught by <code>TaskManager</code> and the information contained
	 * by the exception is displayed in the interface.
	 */
	public V run(TaskMonitor taskMonitor) throws Exception;

	/**
	 * This method is called when the user chooses to cancel the
	 * <code>Task</code>.
	 *
	 * This method should not be called directly by the programmer.
	 *
	 * This method should inform the <code>ValuedTask</code> that it must
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
