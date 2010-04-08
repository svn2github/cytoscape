
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.task;


/**
 * Interface for monitoring the progress of a task.
 */
public interface TaskMonitor {
	/**
	 * This is a hook for a child process to report to its parent application
	 * what percentage of a task it has completed.
	 * <P>
	 * Typically, the parent application implements this method and offers
	 * it as a hook to its child process.  A child process may make an educated
	 * guess as to what percentage of the task it has completed; repeated calls
	 * to this method do not guarantee ascending values of <code>percent</code>.
	 * <P>
	 * The parent application will use this information submitted by the child
	 * process to present a percent completed progress bar to a user, for
	 * example.  Some tasks may choose to go from 0 to 100 on all subtasks
	 * which are executed in order by a task.  For example,
	 * <blockquote><pre>
	 * m_taskMonitor.setPercentCompleted(0);
	 * m_taskMonitor.setStatus("fetching URL");
	 * // fetch URL
	 * m_taskMonitor.setPercentCompleted(100);
	 * m_taskMonitor.setPercentCompleted(0);
	 * m_taskMonitor.setStatus("parsing webpage");
	 * // parse webpage
	 * m_taskMonitor.setPercentCompleted(100);
	 * </pre></blockquote>
	 * <p/>
	 * This method should not block; it should return quickly.<p>
	 *
	 * @param percent a value between <code>0</code> and <code>100</code>
	 *                representing what [guessed] percentage of a task has
	 *                completed; or <code>-1</code> to indicate that a task is
	 *                indeterminate.
	 * @throws IllegalThreadStateException <code>TaskMonitor</code> can only
	 *                                     be be called from the thread that
	 *                                     invokes the task <code>run()</code>.
	 * @throws IllegalArgumentException    <blockquote>if <code>percent</code>
	 *                                     is not in the interval <nobr>
	 *                                     <code>[-1, 100]</code></nobr>.
	 *                                     </blockquote>
	 */
	public void setPercentCompleted(int percent)
	    throws IllegalThreadStateException, IllegalArgumentException;

	/**
	 * This is a hook for a child process to report to its parent application
	 * estimated time until task completion.
	 * <P>
	 * This hook is primarily useful for very long-running processes.  For
	 * example, if a user initiates 100 queries to a database, it may be
	 * useful to report back to the user that the task will not complete
	 * for 5 minutes and 25 seconds.
	 * <P>
	 * Tasks are not required to report estimated time remaining.  If a
	 * task does not to report this value, or has no way of determining it,
	 * the task can safely choose not to invoke this method.
	 *
	 * @param time estimated time until task completion, in milliseconds.
	 * @throws IllegalThreadStateException <code>TaskMonitor</code> can only
	 *                                     be be called from the thread that
	 *                                     invokes the task <code>run()</code>.
	 */
	public void setEstimatedTimeRemaining(long time) throws IllegalThreadStateException;

	/**
	 * Indicates to a parent application that a task has encountered an error
	 * while processing.
	 * <P>
	 * This method provides a convenient mechanism for reporting errors
	 * back to the end-user.
	 * <P>
	 * This method is used to report non-recoverable fatal errors,
	 * and must be called at the very end of a run() method (for example,
	 * in a catch block).
	 *
	 * @param t                an exception that occurred while processing of
	 *                         the task.
	 * @param userErrorMessage a user-presentable error message describing the
	 *                         nature of the exception; may be
	 *                         <code>null</code>.
	 * @throws IllegalThreadStateException <code>TaskMonitor</code> can only
	 *                                     be be called from the thread that
	 *                                     invokes the task <code>run()</code>.
	 */
	public void setException(Throwable t, String userErrorMessage)
	    throws IllegalThreadStateException;

	/**
	 * Indicates to a parent application that a task has encountered an error
	 * while processing.
	 * <P>
	 * This method provides a convenient mechanism for reporting errors
	 * back to the end-user.
	 * <P>
	 * This method is used to report non-recoverable fatal errors,
	 * and must be called at the very end of a run() method (for example,
	 * in a catch block).
	 *
	 * @param t                an exception that occurred while processing of
	 *                         the task.
	 * @param userErrorMessage a user-presentable error message describing the
	 *                         nature of the exception; may be
	 *                         <code>null</code>.
     * @param recoveryTip          a use-presentable tip on how to recover from the error.
	 * @throws IllegalThreadStateException <code>TaskMonitor</code> can only
	 *                                     be be called from the thread that
	 *                                     invokes the task <code>run()</code>.
	 */
	public void setException(Throwable t, String userErrorMessage, String recoveryTip)
	    throws IllegalThreadStateException;

    /**
	 * This is a hook for a child process to report to its parent application
	 * a short one-line text description (not exceeding, say, 60 characters,
	 * even though that is not enforced) of the current phase of processing.
	 * <P>
	 * For example, a spring embedded layout algorithm on a graph, if it were
	 * a task, could report a status string such as
	 * <code>&quot;Calculating node distances&quot;</code> and could later
	 * report a status string as
	 * <code>&quot;Calculating partial derivatives&quot;</code>.
	 * <p/>
	 *
	 * @param message a non-<code>null</code> status message that describes the
	 *                current state of a task's processing; use the empty string
	 *                (<code>&quot;&quot;</code>) to unset the current status
	 *                message.
	 * @throws NullPointerException        if <code>message</code> is
	 *                                     <code>null</code>.
	 * @throws IllegalThreadStateException <code>TaskMonitor</code> can only
	 *                                     be be called from the thread that
	 *                                     invokes the task <code>run()</code>.
	 */
	public void setStatus(String message) throws IllegalThreadStateException, NullPointerException;
}
