package cytoscape.process.ui;

import cytoscape.process.PercentCompletedCallback;
import java.awt.EventQueue;
import java.awt.Frame;
import javax.swing.JDialog;

/**
 * Provides functionality to set percent completed, show, and dispose of
 * a progress dialog.
 **/
public final class ProgressUIControl implements PercentCompletedCallback
{

  private final Object[] m_monitor;
  private final JDialog m_dialog;
  private final PercentCompletedCallback m_percentHook;
  private final Frame m_parent;

  /* Package visible only. */
  ProgressUIControl(Object[] monitor,
                    JDialog dialog,
                    PercentCompletedCallback percentHook,
                    Frame parent)
  {
    m_monitor = monitor;
    m_dialog = dialog;
    m_percentHook = percentHook;
    m_parent = parent;
  }

  /**
   * This is a hook to set the percent completed in a progress bar UI.
   * This method can be called from any thread.  If this method is never
   * called, the progress UI will have an animating generic progress bar
   * with no percent completed marker.
   *
   * @param value represents percent completed of a task - must
   *   be in the range <nobr><code>[0, 100]</code></nobr>.
   * @exception IllegalArgumentException if <code>percent</code> is not in
   *   the interval <nobr><code>[0, 100]</code></nobr>.
   **/
  public void setPercentCompleted(int percent)
  {
    if (percent < 0 || percent > 100)
      throw new IllegalArgumentException
        ("percent must be in the range [0, 100]");
    m_percentHook.setPercentCompleted(percent);
  }

  /**
   * Shows the UI.  Shows the dialog, that is.
   * This method blocks until an asynchronous call to
   * <code>dispose()</code> is made.  This method <i>MUST</i> be called
   * from the AWT event dispatching thread.
   *
   * @exception IllegalThreadStateException if this method is not called from
   *   the AWT event dispatching thread.
   **/
  public void show()
  {
    if (!EventQueue.isDispatchThread())
      throw new IllegalThreadStateException
        ("show() required to be called from the AWT event dispatch thread");
    m_dialog.pack();
    m_dialog.move((m_parent.size().width - m_dialog.size().width) / 2 +
                  m_parent.location().x,
                  (m_parent.size().height - m_dialog.size().height) / 2 +
                  m_parent.location().y);
    m_dialog.show(); // This blocks until m_dialog.dispose() is called; see
                     // the JDK API spec.
  }

  /**
   * This will close the UI, causing <code>show()</code> to return if it
   * is currently blocked.  This method may be called from any thread.
   **/
  public void dispose()
  {
    // We want to be extra correct with calling all Swing code from the AWT
    // event dispatching thread.
    Runnable dispose = new Runnable() {
        public void run() {
          m_monitor[0] = null;
          m_dialog.dispose(); } };
    if (!EventQueue.isDispatchThread())
      EventQueue.invokeLater(dispose);
    else dispose.run();
  }

}
