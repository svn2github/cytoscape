package cytoscape.process;

/**
 * Represents a task that can be terminated prematurely by the same
 * entity that started the process.
 **/
public interface Task extends Runnable
{

  /**
   * <code>run()</code> is called by an external entity.
   * Task process computations should be
   * executed in this method, by the same thread that calls this method.
   * <code>run()</code> shall only be called once for a given instance.
   * If an asynchronous call to <code>halt()</code> is made while
   * <code>run()</code> is executing, <code>run()</code> should make an effort
   * to abort its operations and exit as soon as possible.
   */
  public abstract void run();

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
  public abstract void halt();

}
