package cytoscape.process;

/**
 * Represents a process that can be terminated prematurely by the same
 * entity that started the process.
 **/
public interface Destroyable extends Runnable
{

  /**
   * <code>run()</code> is called by an external entity.
   * Process computations should be
   * executed in this method, by the same thread that calls this method.
   * <code>run()</code> shall only be called once for a given instance.
   * If an asynchronous call to <code>destroy()</code> is made while
   * <code>run()</code> is executing, <code>run()</code> should make an effort
   * to abort its operations and exit as soon as possible.
   */
  public abstract void run();

  /**
   * <code>destroy()</code> is called by an external entity.
   * <code>destroy()</code> should
   * not block; it should return quickly.  If [an asynchronous] thread is
   * executing <code>run()</code> when <code>destroy()</code> is called,
   * a signal should be sent to the thread that is executing <code>run()</code>
   * to abort and exit <code>run()</code>.  If <code>run()</code> has not
   * been called at the time that <code>destroy()</code> is invoked,
   * a later call to <code>run()</code> should not actually
   * &quot;run&quot; anything.  If <code>run()</code> has already been run
   * and has exited by the time <code>destroy()</code> is called,
   * <code>destroy()</code> should do nothing.
   * There is no guarantee that <code>destroy()</code> will be called on
   * and instance of this class.
   */
  public abstract void destroy();

}
