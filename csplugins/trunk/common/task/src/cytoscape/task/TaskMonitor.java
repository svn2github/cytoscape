package cytoscape.task;

/**
 * <b>Ethan:</b> I've gotten rid of
 * <code>setEstimatedTimeRemaining(long)</code> because it is superfluous;
 * an application is able to compute this if it knows
 * <code>setPercentCompleted(int)</code>.  Also, I moved
 * <code>setTitle(String)</code> into the <code>Task</code> interface; see
 * my comment there.  I have serious doubts about
 * <code>setException(Throwable, String)</code> - see my comments below.
 */
public interface TaskMonitor
{

  /**
   * This is a hook for a child process to report to its parent application
   * what percentage of a task it has completed.  Typically, the parent
   * application implements this method and offers it as a hook to its
   * child process.  A child process may make an educated guess as to what
   * percentage of the task it has completed; repeated calls to this
   * method do not guarantee ascending values of <code>percent</code>.  The
   * parent application will use this information submitted by the child
   * process to present a percent completed progress bar to a user, for
   * example.  Other uses may include having a parent application
   * programmatically abort a child process if it determines that the child
   * process will take too much time.<p>
   * This method should not block; it should return quickly.<p>
   * <b>Ethan:</b> I've gotten rid of <code>setIndeterminate(boolean)</code>
   * by allowing <code>-1</code> to be passed to this method.  How do ya like
   * that?  Let methods to implement = happy programmer who is trying to
   * wrap existing functionality.
   *
   * @param percent a value between <code>0</code> and <code>100</code>
   *   representing what [guessed] percentage of a task has completed;
   *   or <code>-1</code> to indicate that a task is indeterminate.
   * @exception IllegalThreadStateException
   *   <b>Ethan:</b> thread policy must be defined.
   * @exception IllegalArgumentException
   *   <blockquote>if <code>percent</code> is not in
   *   the interval <nobr><code>[-1, 100]</code></nobr>.</blockquote>
   */
  public void setPercentCompleted(int percent);

  /**
   * Indicates to a parent application that a task has encountered an error
   * while processing.<p>
   * <b>Ethan:</b> Is it OK to allow <code>null</code> for both
   * <code>t</code> and <code>userErrorMessage</code> simultaneously?
   * I'm really worried about this method because policies surrounding it
   * are very unclear: for example, does this method trigger a parent
   * application to call <code>Task.halt()</code>?  What if
   * methods on <code>TaskMonitor</code> are continued to be called after
   * a call to this method?  Is this exception recoverable or fatal?
   * When will the <code>Task</code>'s <code>run()</code> method return
   * if this is a fatal exception?  You see, there are many unclarities that
   * make this method ill-defined.  I agree that a task monitor absolutely
   * needs to know when an error has occurred in a task.  The API needs to
   * be elegant and simple.
   *
   * @param t an exception that occurred while processing of the task;
   *   may be <code>null</code>.
   * @param userErrorMessage a user-presentable error message describing the
   *   nature of the exception; may be <code>null</code>.
   * @exception IllegalThreadStateException
   *   <b>Ethan:</b> thread policy must be defined.
   */
  public void setException(Throwable t, String userErrorMessage);

  /**
   * This is a hook for a child process to report to its parent application
   * a short one-line text description (not exceeding, say, 60 characters, even
   * though that is not enforced) of the current phase of processing.
   * For example, a spring embedded layout algorithm on a graph, if it were
   * a task, could report a status string such as
   * <code>&quot;Calculating node distances&quot;</code> and could later
   * report a status string as
   * <code>&quot;Calculating partial derivatives&quot;</code>.<p>
   * <b>Ethan:</b> I like this method.  It's one that I really think belongs
   * here.  I don't suppose the task speaks more than one language, say
   * English and Lithuanian.  I don't really want it to speak more than one
   * language - it would complicate interfaces too much.
   *
   * @param message a non-<code>null</code> status message that describes the
   *   current state of a task's processing; use the empty string
   *   (<code>&quot;&quot;</code>) to unset the current status message.
   * @exception NullPointerException if <code>message</code> is
   *   <code>null</code>.
   * @exception IllegalThreadStateException
   *   <b>Ethan:</b> thread policy must be defined.
   */
  public void setStatus(String message);

}
