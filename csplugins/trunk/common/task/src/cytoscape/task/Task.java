package cytoscape.task;

/**
 * Represents a task that can be terminated prematurely by the same
 * entity that started the task - tasks are started with
 * <code>Task.run()</code> and tasks are terminated prematurely
 * [and asynchronously] with <code>Task.halt()</code>.
 * <P>Tasks, by definition, can only be run once per instance.<p>
 * Because the same &quot;parent program&quot; that starts
 * <code>run()</code> will determine whether or not <code>halt()</code>
 * will be called at some point, there is no ambiguity in determining whether
 * or not a process was terminated prematurely when <code>run()</code>
 * returns.
 */
public interface Task extends Runnable, Haltable {

    /**
     * <code>run()</code> executes the task, and is called by an
     * external entity.
     * <P>
     * Task process computations should be executed in this method, by the same
     * thread that calls this method.
     * <P>
     * <code>run()</code> shall only be called once for a given instance.
     * If an asynchronous call to <code>halt()</code> is made while
     * <code>run()</code> is executing, <code>run()</code> should make an effort
     * to abort its operations and exit as soon as it is safe to do so.
     */
    public void run();

    /**
     * <code>halt()</code> is a non-blocking request to halt the task, and
     * is called by an external entity.
     * <P>
     * <code>halt()</code> should not block; it should return quickly.
     * <P>
     * If [an asynchronous] thread is executing <code>run()</code> when
     * <code>halt()</code> is called, a signal should be sent to the thread
     * that is executing <code>run()</code> to abort and exit
     * <code>run()</code>.
     * <P>
     * <code>halt()</code> may return long before <code>run()</code> exits;
     * <code>run()</code> will only return when it is safe to do so.
     * <P>
     * If <code>run()</code> has not been called at the time that
     * <code>halt()</code> is invoked, a later call to <code>run()</code>
     * should not actually &quot;run&quot; anything.
     * <P>
     * If <code>run()</code> has already been run and has exited by the time
     * <code>halt()</code> is called, <code>halt()</code> should do nothing.
     * There is no guarantee that <code>halt()</code> will be called on
     * an instance of this class.
     */
    public void halt();

    /**
     * Lets this <code>Task</code> know who it should report to regarding
     * task progress, errors, status description, etc. <code>monitor</code> may
     * be <code>null</code>, in which case the task will stop reporting.
     * <p/>
     * This method must be called exactly once, and before <code>run()</code>
     * is invoked.
     * <P>
     * If this method is called more than once, the method may throw an
     * <code>IllegalStateException</code>.
     * <P>
     * If this method is not called before <code>run()</code> then
     * <code>run()</code> may throw an <code>IllegalStateException</code>.
     * <P>
     * <code>TaskMonitor</code> methods can only be be called from the thread
     * that invokes <code>run()</code>.
     *
     * @param monitor TaskMonitor Object.
     * @throws IllegalThreadStateException Indicates that the TaskMonitor has
     *                                     already been set.
     */
    public void setTaskMonitor(TaskMonitor monitor)
            throws IllegalThreadStateException;

    /**
     * Gets a Human Readable Title of this Task.
     *
     * @return human readable title of task.
     */
    public String getTitle();
}
