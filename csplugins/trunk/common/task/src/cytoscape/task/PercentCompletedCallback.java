package cytoscape.process;

                                                                             
/**
 * This class is used by running processes to voluntarily report to the
 * governing application how much of the process they've completed.  Having
 * a child process voluntarily report progress completed
 * is preferred over having a governing application poll a child process for
 * percent completed on some time interval.<p>
 * In general, a governing application will make an instance of this class
 * available to a child process if reporting percent completed is part of a
 * contract.
 **/
public interface PercentCompletedCallback
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
   * A word regarding threads.  Most importantly, this method <i>MUST</i> be
   * called from a single thread only.  However, this single thread may be
   * <i>ANY</i> thread (e.g., the AWT event dispatch thread or a custom
   * thread that has been started), unless the parent application has a
   * specific contract with a child process which states a policy regarding
   * which thread is allowed to call this method.<p>
   * This method should not block; it should return quickly.
   *
   * @exception IllegalThreadStateException
   *   <blockquote>may be thrown if more than one
   *   thread is trying to access this method on a given instance; note that
   *   <code>IllegalThreadStateException</code> extends the more general
   *   <code>IllegalArgumentException</code>, which is thrown when a bad
   *   integer argument is passed.</blockquote>
   * @exception IllegalArgumentException
   *   <blockquote>if <code>percent</code> is not in
   *   the interval <nobr><code>[0, 100]</code></nobr>.</blockquote>
   **/
  void setPercentCompleted(int percent);

}
