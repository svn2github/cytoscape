package org.cytoscape.work;

/**
 * An instance of this interface is provided by <code>TaskManager</code>
 * to a <code>Task</code> so that the
 * <code>Task</code> can modify its user interface during its execution.
 */
public interface TaskMonitor
{
	/**
	 * Tells the <code>TaskManager</code> to set the title of the
	 * <code>Task</code>.
	 * The title is a succinct description of the <code>Task</code>'s
	 * purpose.
	 */
	public void setTitle(String title);

	/**
	 * Tells the <code>TaskManager</code> how much progress has been
	 * completed by the <code>Task</code>.
	 *
	 * @param progress Must be between <code>0.0</code> and <code>1.0</code>.
	 * A value of <code>0.0</code> specifies an indefinite progress bar.
	 */
	public void setProgress(double progress);

	/**
	 * Gives the <code>TaskManager</code> a description of what the
	 * <code>Task</code> is currently doing.
	 */
	public void setStatusMessage(String statusMessage);
}
