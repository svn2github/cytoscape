package cytoscape.work;

/**
 * Describes a <code>Task</code> that
 * has a progress bar and a status message
 * describing its current progress.
 *
 * If a <code>Task</code> wishes to have
 * a progress bar, it must implement this
 * interface along with <code>Task</code>,
 * like this:
 *
 * <code>class MyTask implements Task, Progressable { ... } </code>
 *
 * When the task is executed, <code>TaskManager</code> will
 * display a progress bar in its interface.
 */
public interface Progressable
{
	/**
	 * Returns how much of the <code>Task</code>
	 * has been completed.
	 *
	 * This method will not be called before
	 * <code>run()</code> is invoked.
	 *
	 * This method should return quickly. The interface
	 * does not get updated until this method returns.
	 *
	 * @return Must be between <code>0.0</code>, which
	 *   means that no progress has been completed, and
	 *   <code>1.0</code>, which means all progress has
	 *   been completed.
	 */
	public double getProgress();

	/**
	 * Returns a status message describing
	 * its current progress.
	 *
	 * This method will not be called before
	 * <code>run()</code> is invoked.
	 *
	 * This method should return quickly. The interface
	 * does not get updated until this method returns.
	 */
	public String getStatusMessage();
}
