package org.cytoscape.util.thread;

public class ExitRunAlerter implements Runnable
{

  private final Object m_lock;
  private final Runnable m_target;
  private final boolean[] m_running;

  /**
   * The constructor simply assigns member variables to values that were
   * passed into the constructor.  Please see <code>run()</code>
   * documentation for explanation of what an instance of this class does.
   * Note: <nobr><code>lock == running</code></nobr> is perfectly
   * reasonable.
   **/
  public ExitRunAlerter(Object lock, Runnable target, boolean[] running)
  {
    if (lock == null) throw new NullPointerException("lock is null");
    if (target == null) throw new NullPointerException("target is null");
    if (running == null) throw new NullPointerException("done is null");
    if (running.length < 1)
      throw new IllegalArgumentException("done.length < 1");
    m_lock = lock;
    m_target = target;
    m_running = running;
  }

  /**
   * <blockquote><pre>
   * target.run();
   * synchronized (lock) { running[0] = false; lock.notifyAll(); }
   * </pre></blockquote>
   **/ 
  public void run()
  {
    m_target.run();
    synchronized (m_lock) { m_running[0] = false; m_lock.notifyAll(); }
  }

}
