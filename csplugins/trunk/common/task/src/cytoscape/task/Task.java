package cytoscape.task;

/**
 * Represents a task that can be terminated prematurely by the same
 * entity that started the task - tasks are started with
 * <code>Task.run()</code> and tasks are terminated prematurely
 * [and asynchronously] with <code>Task.halt()</code>.
 * Tasks, by definition, can only be run once per instance.<p>
 * Because the same &quot;parent program&quot; that starts
 * <code>run()</code> will determine whether or not <code>halt()</code>
 * will be called at some point, there is no ambiguity in determining whether
 * or not a process was terminated prematurely when <code>run()</code>
 * returns. 
 **/
public interface Task extends Runnable, Haltable
{

  /**
   * <code>run()</code> is called by an external entity.
   * Task process computations should be
   * executed in this method, by the same thread that calls this method.
   * <code>run()</code> shall only be called once for a given instance.
   * If an asynchronous call to <code>halt()</code> is made while
   * <code>run()</code> is executing, <code>run()</code> should make an effort
   * to abort its operations and exit as soon as it is safe to do so.
   */
  public void run();

  /**
   * <code>halt()</code> is called by an external entity.
   * <code>halt()</code> should
   * not block; it should return quickly.  If [an asynchronous] thread is
   * executing <code>run()</code> when <code>halt()</code> is called,
   * a signal should be sent to the thread that is executing <code>run()</code>
   * to abort and exit <code>run()</code>.  <code>halt()</code> may return
   * long before <code>run()</code> exits; <code>run()</code> will only
   * return when it is safe to do so.  If <code>run()</code> has not
   * been called at the time that <code>halt()</code> is invoked,
   * a later call to <code>run()</code> should not actually
   * &quot;run&quot; anything.  If <code>run()</code> has already been run
   * and has exited by the time <code>halt()</code> is called,
   * <code>halt()</code> should do nothing.
   * There is no guarantee that <code>halt()</code> will be called on
   * and instance of this class.
   */
  public void halt();

  /**
   * Lets this <code>Task</code> know who it should report to regarding
   * task progress, errors, status description, etc.
   * <code>monitor</code> may be <code>null</code>, in which case the task
   * will stop reporting.<p>
   * <b>Ethan:</b> What are the policies regarding when and how many times this
   * method may be called by an application using this task?  The strictest
   * policy I can imagine would state that this method must be called
   * exactly once, and before <code>run()</code> is invoked; if this method
   * is not called before <code>run()</code> then <code>run()</code> may
   * throw an <code>IllegalStateException</code>.  A slighly more lax version
   * of this policy would allow this method to be called at most once instead
   * of exactly once - a second call to this method would throw some sort of
   * exception, or calling this method after <code>run()</code> is called
   * would throw some sort of exception.  You get my drift.  We should define
   * a policy, whatever it may be.<p>
   * We also need to define threading policy.  From which thread can methods
   * on <code>TaskMonitor</code> be called?   I'm a big fan of stating a
   * policy which only allows methods on <code>TaskMonitor</code> to be
   * called from the thread that invokes <code>run()</code>.  What do you
   * think?  I'm not so concerned about our exact policy -  just want to make
   * sure that we <i>have</i> a well-defined policy.  This will take guesswork
   * out of programmers' jobs.
   */
  public void setTaskMonitor(TaskMonitor monitor);

  /**
   * Allows an application using this task to query the task's name or
   * title.<p>
   * <b>Ethan:</b> I originally defined this functionality in
   * <code>TaskMonitor.setTitle(String)</code> as you suggested, but I
   * quickly realized that an application using a task may freak out
   * if a task reported its title twice, a second time with a different name.
   * So, instead of going to great lengths to define a policy for
   * <code>setTitle(String)</code> usage, I decided that it's more
   * sensible to have an application poll a task's title.  What do you think?
   * I'm a little concerned that we're making <code>Task</code> a little
   * overbloated.  People won't want to implement <code>Task</code> if
   * it has too many methods - people <i>definitely</i> won't want to
   * implement <code>Task</code> if it has methods which don't directly
   * agree with functionality that a task is able to provide.  Some tasks
   * may wrap other functionality with this interface - they may not
   * always know an appropriate title.  Hmmm.
   */
  public String getTaskTitle();

}
