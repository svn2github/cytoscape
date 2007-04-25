package archive;

import cytoscape.task.TaskMonitor;

import javax.swing.*;
import java.awt.*;

/**
 * Provides functionality to set percent completed (or set indeterminate),
 * show status string, show that an error has occurred,
 * show the dialog, and dispose of the progress dialog.
 */
public final class ProgressUIControl implements TaskMonitor {

    private final boolean[] m_monitor;
    private final JDialog m_dialog;
    private final TaskMonitor m_monitorHook;
    private final Frame m_parent;

    /* Package visible only. */
    ProgressUIControl(boolean[] monitor,
            JDialog dialog,
            TaskMonitor monitorHook,
            Frame parent) {
        m_monitor = monitor;
        m_dialog = dialog;
        m_monitorHook = monitorHook;
        m_parent = parent;
    }

    /**
     * This is a hook to set the percent completed in a progress bar UI.
     *
     * @param percent represents percent completed of a task - must
     *                be in the range <nobr><code>[0, 100]</code></nobr>, or <code>-1</code>
     *                to set the progress bar to indeterminate.
     * @throws IllegalArgumentException if <code>percent</code> is not in
     *                                  the interval <nobr><code>[-1, 100]</code></nobr>.
     */
    public void setPercentCompleted(int percent) {
        if (percent < -1 || percent > 100)
            throw new IllegalArgumentException
                    ("percent must be in the range [-1, 100]");
        m_monitorHook.setPercentCompleted(percent);
    }

    public void setEstimatedTimeRemaining(long time) {
        m_monitorHook.setEstimatedTimeRemaining(time);
    }

    public void setException(Throwable t, String userErrorMessage) {
        m_monitorHook.setException(t, userErrorMessage);
    }

    public void setStatus(String message) {
        if (message == null) throw new NullPointerException("message is null");
        m_monitorHook.setStatus(message);
    }

    /**
     * Shows the UI.  Shows the dialog, that is.
     * This method blocks until an asynchronous call to
     * <code>dispose()</code> is made.  This method <i>MUST</i> be called
     * from the AWT event dispatching thread.
     *
     * @throws IllegalThreadStateException if this method is not called from
     *                                     the AWT event dispatching thread.
     */
    public void show() {
        if (!EventQueue.isDispatchThread())
            throw new IllegalThreadStateException
                    ("show() required to be called from the AWT event dispatch thread");
        m_dialog.pack();
        m_dialog.setLocation((m_parent.getSize().width - m_dialog.getSize().width) / 2 +
                m_parent.getLocation().x,
                (m_parent.getSize().height - m_dialog.getSize().height) / 2 +
                m_parent.getLocation().y);
        m_dialog.setVisible(true); // This blocks until m_dialog.dispose() is called; see
        // the JDK API spec.
    }

    /**
     * This will close the UI, causing <code>show()</code> to return if it
     * is currently blocked.  This method may be called from any thread.
     */
    public void dispose() {
        // We want to be extra correct with calling all Swing code from the AWT
        // event dispatching thread.
        Runnable dispose = new Runnable() {
            public void run() {
                m_monitor[0] = false;
                m_dialog.dispose();
            }
        };
        if (!EventQueue.isDispatchThread())
            EventQueue.invokeLater(dispose);
        else
            dispose.run();
    }

}
