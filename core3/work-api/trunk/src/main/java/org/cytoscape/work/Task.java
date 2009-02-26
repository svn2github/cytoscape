package org.cytoscape.work;

/**
 * This interface specifies a unit of work to be executed
 * asynchronously in its own
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
 *
 * <p>Some hints for writing a <code>Task</code>:</p>
 * <p><ul>
 *
 * <li>When an exception is thrown, the <code>Task</code>
 * should not catch it and set a status message or the progress,
 * even to provide explanatory messages for the user.
 * A <code>TaskManager</code> can disregard status message
 * and progress updates once an exception is thrown.</li>
 *
 * <li>Any helpful user messages regarding the exception should
 * be contained solely in the exception. If a <code>Task</code> throws
 * a low level exception, it should catch it and throw
 * an exception with a high level description. For example:
 * <p><code>
 * try<br>
 * {<br>
 *   ...<br>
 * }<br>
 * catch (IOException exception) // Low level exception<br>
 * {<br>
 *   // Throw a high level exception that gives a high level explanation<br>
 *   // that makes sense for a non-technical user.<br>
 *   throw new Exception("Oops! Looks like you specified an invalid file.", exception)<br>
 * }<br>
 * </code></p>
 * Any helpful messages for the user should be contained in
 * an exception.</li>
 *
 * <li>When a <code>Task</code> encounters an error
 * that is not in the form of an exception, like an invalid
 * variable or incorrectly formatted parameter,
 * the <code>Task</code> should not
 * set the status message giving an explanation of the
 * error and exit. Instead, it should throw an exception.
 * <p>The wrong way:</p>
 * <p><code>
 * public void run(TaskMonitor taskMonitor)<br>
 * {<br>
 *   if (myParameter == null)<br>
 *   {<br>
 *     taskMonitor.setStatusMessage("Whoa, looks like you didn't specified the parameter!");<br>
 *     return;<br>
 *   }<br>
 * }<br>
 * </code></p>
 * <p>The right way:</p>
 * <p><code>
 * public void run(TaskMonitor taskMonitor) throws Exception<br>
 * {<br>
 *   if (myParameter == null)<br>
 *     throw new Exception("Whoa, looks like you didn't specified the parameter!");<br>
 * }<br>
 * </code></p>
 * This is done because it is possible for the <code>TaskManager</code> to close
 * the <code>Task</code>'s user interface when the <code>Task</code> returns
 * before the user can read the message. Throwing an exception ensures that
 * the user will see the message.</li>
 *
 * <li>The <code>Task</code> should not set the status message or progress
 * immediately before the <code>Task</code> finishes. This is because the
 * <code>TaskManager</code> may close the <code>Task</code>'s user interface
 * before the user has a chance to read it. For example:
 * <p><code>
 * public void run(TaskMonitor taskMonitor) throws Exception<br>
 * {<br>
 *   ... // Some complicated calculation<br>
 *   <br>
 *   // This is unnecessary:<br>
 *   taskMonitor.setStatusMessage("We're all done!");<br>
 *   taskMonitor.setProgress(1.0);<br>
 * }<br>
 * </code></p>
 * </li>
 * </ul></p>
 *
 * @author Samad Lotia
 */
public interface Task
{
	/**
	 * This method is called by the <code>Task</code>'s own thread
	 * created by <code>TaskManager</code>.
	 *
	 * If one has a <code>Task</code> object, this method should not be called,
	 * since it will be called by the <code>TaskManager</code>.
	 *
	 * @param taskMonitor This is provided by <code>TaskManager</code>
	 * to allow the <code>Task</code> to modify its user interface.
	 *
	 * @throws Exception The <code>Task</code> is at liberty to
	 * throw any exceptions in <code>run</code>. The exception is
	 * caught by <code>TaskManager</code> and is displayed in the interface.
	 * If a <code>Task</code> does not throw an exception,
	 * the <code>Task</code> implementation does <i>not</i>
	 * need to specify the <code>throws Exception</code> clause 
	 * for the <code>run</code> method.
	 */
	public void run(TaskMonitor taskMonitor) throws Exception;

	/**
	 * This method is called when the user chooses to cancel the
	 * <code>Task</code>.
	 *
	 * If one has a <code>Task</code> object, this method should not be called,
	 * since it might be called by the <code>TaskManager</code>.
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
