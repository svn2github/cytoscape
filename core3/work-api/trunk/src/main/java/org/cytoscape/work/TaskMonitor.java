package org.cytoscape.work;

/**
 * Used by <code>Task</code>s to modify its user interface.
 *
 * @author Pasteur
 */
public interface TaskMonitor
{
	/**
	 * Sets the title of the <code>Task</code>.
	 * The title is a succinct description of the <code>Task</code>'s
	 * purpose. This method should only be called once and at the beginning
	 * of the <code>run</code> method.
	 */
	public void setTitle(String title);

	/**
	 * Sets the progress completed by the <code>Task</code>.
	 *
	 * @param progress Must be between <code>0.0</code> and <code>1.0</code>.
	 * A value of <code>0.0</code> specifies an indefinite progress bar.
	 */
	public void setProgress(double progress);

	/**
	 * Sets the status message that describes what <code>Task</code> is currently doing.
	 * This method can be called throughout the course of the <code>run</code> method.
	 */
	public void setStatusMessage(String statusMessage);


	/**
	 * This method returns a TaskMonitor for use by embedded Tasks.  The parent ("this")
	 * TaskMonitor will allocate the specified fraction of itself to the sub TaskMonitor.
	 * The sub TaskMonitor returned from this method will itself have a full range of
	 * of 0 to 1.  This range will be mapped by the parent TaskMonitor to match the specified
	 * fraction.  The effect of specifying a fraction is to increase the progress of the
	 * parent ("this") Task by the amount of the fraction.  For example, if the current
	 * progress of the parent TaskMonitor is 0.1 and this method is called with a fraction of
	 * 0.5, then once the sub Task has been executed and the sub TaskMonitor successfully 
	 * updated then the progress of the parent TaskMonitor will be set to 0.6. If the fractions
	 * add up to more than 1, then the progress will be simply be set to 1. 
	 * 
	 * @param fraction A value between 0 and 1 exclusive. This represents the fraction
	 * of the parent TaskMonitor that will be allotted to the sub TaskMonitor.
	 * @return A TaskMonitor object suitable for monitoring the progress of embedded Tasks.
	public TaskMonitor getSubTaskMonitor(double fraction);
	 */
}
