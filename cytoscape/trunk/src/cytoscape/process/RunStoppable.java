package cytoscape.process;

/**
 * Creates a <code>Stoppable</code> out of a <code>Haltable</code>.
 **/
public final class RunStoppable implements Runnable, Stoppable
{

  private final Haltable m_halt;
  private final Object m_lock = new Object();
  private boolean m_ran = false;
  private boolean m_running = false;
  private boolean m_stop = false;

  public RunStoppable(Haltable haltable)
  {
    if (haltable == null)
      throw new NullPointerException("haltable is null");
    m_halt = haltable;
  }

  /**
   * This method is guaranteed to only run once - that is, it will call
   * the underlying <code>Runnable</code>'s <code>run()</code> only once.
   * If this is invoked a second time, it will throw a
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
    try { m_halt.run(); }
    finally
    {
      synchronized (m_lock) {
        m_running = false;
        m_lock.notifyAll(); }
    }
  }

  public void stop()
  {
    synchronized (m_lock)
    {
      m_stop = true;
      if (!m_running) return;
    }
    m_halt.halt();
    synchronized (m_lock)
    {
      while (m_running) {
        try { m_lock.wait(); }
        catch (InterruptedException exc) {} }
    }
  }

}
