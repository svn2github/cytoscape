package cytoscape.process;

/**
 * Creates a <code>Stoppable</code> out of a <code>Task</code>.
 **/
public final class RunStoppable implements Runnable, Stoppable
{

  private final Task m_task;
  private final Object m_lock = new Object();
  private boolean m_ran = false;
  private boolean m_running = false;
  private boolean m_stop = false;

  public RunStoppable(Task task)
  {
    if (task == null)
      throw new NullPointerException("task is null");
    m_task = task;
  }

  /**
   * This method is guaranteed to run at most once - that is, it will call
   * the underlying <code>Task.run()</code> at most once.
   * If this method is invoked a second time, it will throw an
   * <code>IllegalStateException</code>.
   **/
  public void run()
  {
    synchronized (m_lock) {
      if (m_ran) throw new IllegalStateException("already running or ran");
      m_ran = true; }
    // Guaranteed to get to this line of code at most once.
    synchronized (m_lock) {
      if (m_stop) return;
      m_running = true; }
    try { m_task.run(); }
    finally
    {
      synchronized (m_lock) {
        m_running = false;
        m_lock.notifyAll(); }
    }
  }

  /**
   * Calls the underlying <code>Task.halt()</code> and blocks until
   * the underlying <code>Task.run()</code> has finished.
   * This method actually supports multiple threads; all calling threads
   * will block until the underlying <code>Task.run()</code> returns.
   **/
  public void stop()
  {
    boolean mustCallHalt = true;
    synchronized (m_lock)
    {
      if (m_stop) // Someone has called stop() before us; we therefore
                  // have already or will eventually call halt() somewhere
                  // else, maybe in another thread.
        mustCallHalt = false;
      else
        m_stop = true;
      if (!m_running) return; // We don't even bother calling halt().
                              // this.run() is guaranteed not to run() the
                              // underlying Runnable.
    }
    // By this line of code, regardless of thread, the run() method
    // will have been called.  It may or may not still be executing by this
    // time.
    if (mustCallHalt) m_task.halt(); // This isn't necessary, but we do it
                                     // anyways: limit calling halt() to 
                                     // at most once.
    synchronized (m_lock)
    {
      while (m_running) {
        try { m_lock.wait(); }
        catch (InterruptedException exc) {} }
    }
  }

}
