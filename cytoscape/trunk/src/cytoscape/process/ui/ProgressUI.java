package cytoscape.process.ui;

import cytoscape.process.Stoppable;

/**
 * This class is a utility for providing popup dialogs which display progress
 * on a lengthy process.  Historically, Cytoscape tasks such as loading a
 * graph from a file or laying out a large graph were done in the AWT event
 * handling thread.  Such tasks take on the order of minutes sometimes; because
 * these tasks were computed by the AWT event handling thread, the Cytoscape
 * desktop would become unresponsive while these tasks were executing.
 * This class was designed as a framework to ease the transition of
 * computing lengthy tasks in theads other than the AWT event handling
 * thread, as described in the next paragraph.<p>
 * Tasks which were initially forked as new threads to prevent the
 * unresponsive Cytoscape desktop problem touched parts of code which
 * invoked Swing and/or Piccolo libraries.  Therefore, it was necessary to
 * limit, as much as programatically possible, actions on the desktop which
 * would cause concurrent execution of similar code (Swing and Piccolo are
 * only single-thread safe).  To do this, modal dialogs were created which
 * could not be closed by the user, and which would block all user input
 * until the task in question had finished.
 **/
public class ProgressUI
{

  /**
   * Brings up a modal progress dialog.  This dialog blocks user input
   * and is not closeable by the user.  A progress dialog is global and
   * there should only be one background process running at a time which
   * corresponds to this dialog - the reasoning behind this is that
   * we want to stay as close as possible to a single-threaded model.<p>
   * A plain vanilla progress dialog has no stop button and has a generic
   * progress animation which knows nothing about percent completed of the
   * process.  The two options for a progress dialog are a stop button (which
   * can be made to appear by passing a non-<code>nulL</code>
   * <code>stop</code> parameter to this method) and a progress animation with
   * a percent completed (the percent completed animation is triggered by
   * using the returned <code>CompletionUIControl</code> object).
   *
   * @param title desired title of the dialog window; may not be
   *   <code>null</code>.
   * @param message brief message that will appear to the user;
   *   may not be <code>null</code>.
   * @param stop hook to allow a stop button to stop a process; if
   *   <code>null</code>, no stop button will appear in the dialog.
   * @return hook to set percent completed in dialog animation.
   * @exception IllegalStateException if this is called while another
   *   progress dialog is currently open.
   * 
   **/
  public static CompletionUIControl startProgress(String title,
                                                  String message,
                                                  Stoppable stop)
  {
    return null;
  }

  /**
   * Closes the global progress dialog if there is one open currently.
   **/
  public static void stopProgress()
  {
  }

}
