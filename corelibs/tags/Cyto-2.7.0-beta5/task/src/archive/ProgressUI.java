
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package archive;


// Notice the dependency list here - no deps on legacy cytoscape.* code.
import cytoscape.task.TaskMonitor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;


/**
 * This class is a utility for providing a popup dialog which displays progress
 * on a lengthy process.  Historically, Cytoscape tasks such as loading a
 * graph from a file or laying out a large graph were done in the AWT event
 * handling thread.  Such tasks take on the order of minutes sometimes; because
 * these tasks were computed by the AWT event handling thread, the Cytoscape
 * desktop would become unresponsive while these tasks were executing.
 * <code>ProgressUI</code> was designed as a framework to ease the transition
 * of computing lengthy tasks in theads other than the AWT event handling
 * thread.<p>
 * Tasks which were initially forked as new threads to prevent the
 * unresponsive Cytoscape desktop problem touched parts of code which
 * invoked Swing and/or Piccolo libraries.  Therefore, it was necessary to
 * limit, as much as programatically possible, actions on the desktop which
 * would cause concurrent execution of similar code (Swing and Piccolo are
 * only single-thread safe).  To do this, modal dialogs were created which
 * could not be closed by the user, and which would block all user input
 * until the task in question had finished.  Although this approach does
 * not guarantee thread safety for Swing and Piccolo libraries (after all
 * an executing task which is started in a custom thread could be calling
 * Swing and Piccolo code), it does handle the situation as elegantly as
 * possible without resorting to the 100% correct approach which in many
 * cases would involve deep rearchitecting of code.
 */
public final class ProgressUI {
	private static final boolean[] s_contrl = new boolean[1];

	// No constructor for this class.
	private ProgressUI() {
	}

	/**
	 * Creates a modal progress dialog.  (But does not show it, yet.)
	 * A progress dialog is global and
	 * there should only be one background process running at a time which
	 * corresponds to this dialog - the reasoning behind this is that
	 * we want to stay as close as possible to a single-threaded model.<p>
	 * A plain vanilla progress dialog has no stop button and has a generic
	 * progress animation which knows nothing about percent completed of the
	 * process.  The two options for a progress dialog are a stop button (which
	 * can be made to appear by passing a non-<code>null</code>
	 * <code>stop</code> parameter to this method) and a progress animation with
	 * a percent completed (the percent completed animation is triggered by
	 * using the returned <code>ProgressUIControl</code> object).<p>
	 * This method <i>MUST</i> be called from the AWT queue handling thread.<p>
	 * A progress UI can only be created once all previous progress UIs have been
	 * disposed of.  This method will throw an <code>IllegalStateException</code>
	 * if previous progress UI has not been disposed of at the time this method
	 * is called.<p>
	 * A few notes on the <code>stop</code> parameter passed to this method.
	 * If you're using a <code>Stoppable</code>, a &quot;Stop&quot; button will
	 * appear on the modal dialog.  pushing this button will trigger, in the
	 * AWT event dispatch thread, <code>stop.stop()</code> to be called.
	 * Therefore, if <code>stop()</code> blocks for a while, the UI will become
	 * unresponsive during this time.  Programmers should be aware of ths and
	 * should prevent passing <code>Stoppable</code> objects which block for
	 * long periods.
	 *
	 * @param parent <blockquote>the parent frame that will show this modal dialog; in most
	 *               cases this will be <code>Cytoscape.getDesktop()</code>; this class uses
	 *               an input parameter instead of using <code>cytoscape.Cytoscape</code>
	 *               to avoid dependencies - this keeps code more modular.</blockquote>
	 * @param title  <blockquote>desired title of the dialog window; may not be
	 *               <code>null</code>.</blockquote>
	 * @param stop   <blockquote>hook to allow a stop button to stop a process; if
	 *               <code>null</code>, no stop button will appear in the dialog;
	 *               <code>stop.stop()</code> is called by this framework if and only if
	 *               the &quot;Stop&quot; button is pushed by a user; disposing of the
	 *               returned <code>ProgressUIControl</code> does <i>not</i> cause
	 *               <code>stop()</code> to be called.</blockquote>
	 * @return hook for controlling this UI.
	 * @throws IllegalStateException       <blockquote>if this is called while another
	 *                                     progress dialog is currently open.</blockquote>
	 * @throws IllegalThreadStateException <blockquote>if this is called from a thread
	 *                                     that is not the AWT event handling thread
	 *                                     (<nobr><code>java.awt.EventQueue.isDispatchThread()</code></nobr>).
	 *                                     </blockquote>
	 */
	public static ProgressUIControl startProgress(Frame parent, String title, final Stoppable stop) {
		if (!EventQueue.isDispatchThread())
			throw new IllegalThreadStateException("startProgress() required to be called from AWT dispatch thread");

		if (parent == null)
			throw new NullPointerException("parent is null");

		if (title == null)
			throw new NullPointerException("title is null");

		String message = " ";
		JDialog busyDialog = new JDialog(parent, title, true);
		busyDialog.setResizable(false);
		busyDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new EmptyBorder(20, 20, 20, 20));

		final JLabel label = new JLabel(message);
		panel.add(label, BorderLayout.CENTER);

		final JProgressBar progress = new JProgressBar(0, 100);
		progress.setIndeterminate(true);
		progress.setStringPainted(true);
		progress.setString("");
		panel.add(progress, BorderLayout.SOUTH);
		busyDialog.getContentPane().add(panel, BorderLayout.CENTER);

		final ProgressUIControl returnThis = new ProgressUIControl(s_contrl, busyDialog,
		                                                           new TaskMonitor() {
				public void setPercentCompleted(final int percent) {
					EventQueue.invokeLater(new Runnable() {
							public void run() {
								if (percent < 0) {
									progress.setIndeterminate(true);
								} else {
									progress.setIndeterminate(false);
									progress.setString(null);
									progress.setValue(percent);
								}
							}
						});
				}

				/**
				 *  DOCUMENT ME!
				 *
				 * @param t DOCUMENT ME!
				 * @param errorMessage DOCUMENT ME!
				 */
				public void setException(Throwable t, String errorMessage) {
					throw new IllegalStateException("not yet implemented");
				}

				public void setException (Throwable t, String s1, String s2) {
					throw new IllegalStateException("not yet implemented");
				}

				/**
				 *  DOCUMENT ME!
				 *
				 * @param message DOCUMENT ME!
				 */
				public void setStatus(String message) {
					label.setText(message);
				}

				/**
				 *  DOCUMENT ME!
				 *
				 * @param time DOCUMENT ME!
				 */
				public void setEstimatedTimeRemaining(long time) {
					throw new IllegalStateException("not yet implemented");
				}
			}, parent);

		if (stop != null) {
			JButton button = new JButton("Stop");
			button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							stop.stop();
						} // AWT thread will block here!
						finally {
							returnThis.dispose();
						}
					}
				});

			JPanel panel2 = new JPanel(new FlowLayout());
			panel2.setBorder(new EmptyBorder(0, 20, 20, 20));
			panel2.add(button);
			busyDialog.getContentPane().add(panel2, BorderLayout.SOUTH);
		} else {
			busyDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}

		synchronized (s_contrl) {
			if (!s_contrl[0])
				s_contrl[0] = true;
			else
				throw new IllegalStateException("another progress dialog is currently being shown");
		}

		return returnThis;
	}

	/**
	 * Creates a modal progress dialog.  (But does not show it, yet.)
	 * A progress dialog is global and
	 * there should only be one background process running at a time which
	 * corresponds to this dialog - the reasoning behind this is that
	 * we want to stay as close as possible to a single-threaded model.<p>
	 * A plain vanilla progress dialog has no stop button and has a generic
	 * progress animation which knows nothing about percent completed of the
	 * process.  The two options for a progress dialog are a stop button (which
	 * can be made to appear by passing a non-<code>null</code>
	 * <code>stop</code> parameter to this method) and a progress animation with
	 * a percent completed (the percent completed animation is triggered by
	 * using the returned <code>ProgressUIControl</code> object).<p>
	 * This method <i>MUST</i> be called from the AWT queue handling thread.<p>
	 * A progress UI can only be created once all previous progress UIs have been
	 * disposed of.  This method will throw an <code>IllegalStateException</code>
	 * if previous progress UI has not been disposed of at the time this method
	 * is called.<p>
	 * A few notes on the <code>stop</code> parameter passed to this method.
	 * If you're using a <code>Stoppable</code>, a &quot;Stop&quot; button will
	 * appear on the modal dialog.  pushing this button will trigger, in the
	 * AWT event dispatch thread, <code>stop.stop()</code> to be called.
	 * Therefore, if <code>stop()</code> blocks for a while, the UI will become
	 * unresponsive during this time.  Programmers should be aware of ths and
	 * should prevent passing <code>Stoppable</code> objects which block for
	 * long periods.
	 *
	 * @param parent <blockquote>the parent frame that will show this modal dialog; in most
	 *               cases this will be <code>Cytoscape.getDesktop()</code>; this class uses
	 *               an input parameter instead of using <code>cytoscape.Cytoscape</code>
	 *               to avoid dependencies - this keeps code more modular.</blockquote>
	 * @param title  <blockquote>desired title of the dialog window; may not be
	 *               <code>null</code>.</blockquote>
	 * @return hook for controlling this UI.
	 * @throws IllegalStateException       <blockquote>if this is called while another
	 *                                     progress dialog is currently open.</blockquote>
	 * @throws IllegalThreadStateException <blockquote>if this is called from a thread
	 *                                     that is not the AWT event handling thread
	 *                                     (<nobr><code>java.awt.EventQueue.isDispatchThread()</code></nobr>).
	 *                                     </blockquote>
	 */
	public static ProgressUIControl startProgress(Frame parent, String title) {
		return startProgress(parent, title, null);
	}
}
