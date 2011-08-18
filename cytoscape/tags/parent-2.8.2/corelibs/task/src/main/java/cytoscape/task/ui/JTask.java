
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

package cytoscape.task.ui;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.Date;

import javax.swing.*;
import javax.swing.border.EmptyBorder;


/**
 * Common UI element for visually monitoring task progress.
 */
public class JTask extends JDialog implements TaskMonitor, ActionListener {
	private static final long serialVersionUID = 333614801L;

	/**
	 * ProgressBar UI component.
	 */
	private JProgressBar pBar;

	/**
	 * Time Interval for updating the time elapsed field.
	 */
	private static final int TIME_INTERVAL = 500;

	/**
	 * Description value label.
	 */
	private JLabel descriptionValue;

	/**
	 * Status value label.
	 */
	private JTextArea statusValue;

	/**
	 * Time remaining label.
	 */
	private JLabel timeRemainingValue;

	/**
	 * Time elapsed label.
	 */
	private JLabel timeElapsedValue;

	/**
	 * Progress Value.
	 */
	private JLabel progressValue;

	/**
	 * Internal Timer.
	 */
	private Timer timer;

	/**
	 * Action Cancel Message.
	 */
	private static final String ACTION_CANCEL = "CANCEL";

	/**
	 * Action Close Message.
	 */
	private static final String ACTION_CLOSE = "CLOSE";

	/**
	 * Cancel Button.
	 */
	private JButton cancelButton;

	/**
	 * Close Button.
	 */
	private JButton closeButton;

	/**
	 * Time Stamp when Component was constructed.
	 */
	private Date startTime;

	/**
	 * Title Value.
	 */
	private String taskTitle;

	/**
	 * Used to Configure the JTask UI.
	 */
	private JTaskConfig config;

	/**
	 * Task we are monitoring.
	 */
	private Task task;

	/**
	 * Contains all the Progress Fields.
	 */
	private JPanel progressPanel;

	/**
	 * Flag to Indicate that user has requested that task halt.
	 */
	private boolean haltRequested = false;

	/**
	 * Flag to Indicate that error has occurred within task.
	 */
	private boolean errorOccurred = false;

	/**
	 * Estimated Time Remaining.
	 */
	private long timeRemaining;

	/**
	 * Constructor.
	 *
	 * @param task   Task we are monitoring, and may need to cancel.
	 * @param config JTaskConfig Object.
	 */
	public JTask(Task task, JTaskConfig config) {
		super((Frame) config.getOwner(), task.getTitle(), config.getModal());
		this.task = task;
		this.taskTitle = task.getTitle();
		this.config = config;
		init();
	}

	/**
	 * Sets Percentage Complete.
	 * Called by a child task thread.
	 *
	 * @param percent Percentage Complete.
	 */
	public void setPercentCompleted(final int percent) {
		//  Ignore events if user has requested to halt task.
		if (!haltRequested) {
			if ((percent < -1) || (percent > 100)) {
				throw new IllegalArgumentException("percent parameter is outside range:  [-1, 100]");
			}

			//  Update the UI
			//  Only update when the new value != old value
			//  This dramatically cuts down on the number of new events added
			//  to the Event Dispatch Thread.
			if (percent != pBar.getValue()) {
				SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							if (percent == -1) {
								pBar.setIndeterminate(true);
							} else {
								if (pBar.isIndeterminate()) {
									pBar.setIndeterminate(false);
								}

								pBar.setValue(percent);
							}
						}
					});
			}
		}
	}

	/**
	 * Sets Estimated Time Remaining.
	 * Called by a child task thread.
	 * Safely queues changes to the Swing Event Dispatch Thread.
	 *
	 * @param time Time Remaining, in milliseconds.
	 */
	public void setEstimatedTimeRemaining(final long time) {
		//  Ignore events if user has requested to halt task.
		if (!haltRequested) {
			//  Update the UI
			//  Only update when the new value != old value
			//  This dramatically cuts down on the number of new events added
			//  to the Event Dispatch Thread.
			if (timeRemaining != time) {
				SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							timeRemainingValue.setText(StringUtils.getTimeString(time));
						}
					});
			}

			timeRemaining = time;
		}
	}

	/**
	 * Reports an Error in Task Processing.
	 *
	 * @param t                Throwable t.
	 * @param userErrorMessage Human Readable Error Message.
	 */
	public void setException(final Throwable t, final String userErrorMessage) {
		//  Ignore events if user has requested to halt task.
		if (!haltRequested) {
			this.errorOccurred = true;
			stopTimer();
            showErrorPanel(t, userErrorMessage, null);
        }
	}

	public void setException(Throwable t, String userErrorMessage, String recoveryTip)
	    throws IllegalThreadStateException {
        //  Ignore events if user has requested to halt task.
        if (!haltRequested) {
            this.errorOccurred = true;
            stopTimer();
            showErrorPanel(t, userErrorMessage, recoveryTip);
        }
    }


    private void showErrorPanel(final Throwable t, final String userErrorMessage,
            final String recoveryTip) {
        //  Update the UI
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    //  Hide All Existing UI Components
                    Container c = getContentPane();
                    closeButton.setVisible(false);
                    cancelButton.setVisible(false);
                    progressPanel.setVisible(false);

                    //  Create Error Panel
                    JPanel errorPanel = new JErrorPanel(JTask.this, t, userErrorMessage,
                            recoveryTip);
                    c.add(errorPanel, BorderLayout.CENTER);
                    config.setAutoDispose(false);
                    pack();
                    setTitle("An Error Has Occurred");

                    //  Make sure JTask is actually visible
                    if (!JTask.this.isShowing()) {
                        JTask.this.setVisible(true);
                    }
                }
            });
    }

    /**
	 * Indicates that the worker task is done processing.
	 */
	public void setDone() {
		//  Update the UI
		SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					stopTimer();

					if (config.getAutoDispose()) {
						dispose();
					} else {
						setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						removeProgressBar();
						cancelButton.setEnabled(false);
						closeButton.setEnabled(true);

						// If we are done because user hit the cancel button,
						// say so explicitly.
						if (haltRequested) {
							setCancelStatusMsg("Canceled by User");
						}
						pack();
					}
				}
			});
	}

	/**
	 * Call to externally control whether the cancel button is enabled
	 * or disabled.  This can be used by threads which have some sections
	 * that can be canceled and some that can not.
	 *
	 * @param enable if true, enable the cancel button
	 */
	public void setCancel(boolean enable) {
		cancelButton.setEnabled(enable);
	}

	/**
	 * Sets the Status Message.
	 * Called by a child task thread.
	 * Safely queues changes to the Swing Event Dispatch Thread.
	 *
	 * @param message status message.
	 */
	public void setStatus(final String message) {
		if (!haltRequested) {
			//  Update the UI
			SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						statusValue.setText(message);
						pack();
					}
				});
		}
	}

	/**
	 * Returns true if Task Has Encountered An Error.
	 *
	 * @return boolean value.
	 */
	public boolean errorOccurred() {
		return errorOccurred;
	}

	/**
	 * Returns true if User Has Requested to Halt the Task.
	 *
	 * @return boolean value.
	 */
	public boolean haltRequested() {
		return haltRequested;
	}

	/**
	 * Initializes UI.
	 */
	private void init() {
		//  Set Frame Title
		this.setTitle(taskTitle);

		//  Init User Interface
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		progressPanel = new JPanel(new GridBagLayout());
		progressPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		//  Create Description, Status, Time Left and Time Elapsed Fields
		int y = 0;
		addLabel("Description:  ", progressPanel, 0, y, GridBagConstraints.NORTHEAST, true);
		descriptionValue = addLabel(StringUtils.truncateOrPadString(taskTitle), progressPanel, 1,
		                            y, GridBagConstraints.NORTHWEST, true);

		addLabel("Status:  ", progressPanel, 0, ++y, GridBagConstraints.NORTHEAST,
		         config.getStatusFlag());
		statusValue = addTextArea(StringUtils.truncateOrPadString("Starting..."), progressPanel, 1,
		                          y, GridBagConstraints.NORTHWEST, config.getStatusFlag());

		addLabel("Time Left:  ", progressPanel, 0, ++y, GridBagConstraints.NORTHEAST,
		         config.getTimeRemainingFlag());
		timeRemainingValue = addLabel("", progressPanel, 1, y, GridBagConstraints.NORTHWEST,
		                              config.getTimeRemainingFlag());

		addLabel("Time Elapsed:  ", progressPanel, 0, ++y, GridBagConstraints.NORTHEAST,
		         config.getTimeElapsedFlag());
		timeElapsedValue = addLabel(StringUtils.getTimeString(0), progressPanel, 1, y,
		                            GridBagConstraints.NORTHWEST, config.getTimeElapsedFlag());

		progressValue = addLabel("Progress:  ", progressPanel, 0, ++y,
		                         GridBagConstraints.NORTHEAST, true);

		initProgressBar(progressPanel, y);
		container.add(progressPanel, BorderLayout.CENTER);
		createFooter(container);

		//  Disable close operation until task is done.
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		//  Stop timer when window is finally closed.
		this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					stopTimer();
				}
			});
		this.pack();
		this.setResizable(false);

		//  Center component relative to parent component
		//  or relative to user's screen.
		setLocationRelativeTo(config.getOwner());

		//  Conditionally Show / AutoPopUp the Component.
		//  Call to show must be done on the event-dispatch thread
		//  Otherwise, a modal window will block.
		//  autoPopUp();

		//  Initialize timer only if we want to display the time elapsed field.
		if (config.getTimeElapsedFlag()) {
			initTimer();
		}
	}

	/**
	 * Creates Footer with Close, Cancel Buttons.
	 *
	 * @param container Container Object.
	 */
	private void createFooter(Container container) {
		JPanel footer = new JPanel();
		footer.setBorder(new EmptyBorder(5, 5, 5, 5));
		footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
		closeButton = new JButton("    Close    ");
		closeButton.setEnabled(false);
		closeButton.setActionCommand(ACTION_CLOSE);
		closeButton.addActionListener(this);
		cancelButton = new JButton("    Cancel   ");
		cancelButton.setActionCommand(ACTION_CANCEL);
		cancelButton.addActionListener(this);

		if (config.getCloseButtonFlag()) {
			footer.add(closeButton);
			footer.add(Box.createHorizontalGlue());
		}

		if (config.getCancelButtonFlag()) {
			footer.add(cancelButton);
		}

		container.add(footer, BorderLayout.EAST);
	}

	/**
	 * Initializes the JProgressBar.
	 *
	 * @param progressPanel JPanel Object.
	 */
	private void initProgressBar(JPanel progressPanel, int y) {
		GridBagConstraints c = new GridBagConstraints();
		pBar = new JProgressBar();
		pBar.setIndeterminate(true);
		pBar.setMaximum(100);
		pBar.setValue(0);
		pBar.setBorder(new EmptyBorder(5, 0, 5, 0));
		pBar.setDoubleBuffered(true);
		c.gridx = 1;
		c.gridy = y;
		c.fill = GridBagConstraints.HORIZONTAL;
		progressPanel.add(pBar, c);
	}

	/**
	 * Add New Label to Specified JPanel.
	 *
	 * @param text  Label Text.
	 * @param panel Container Panel.
	 * @param gridx X Location.
	 * @param gridy Y Location.
	 * @return JLabel Object.
	 */
	private JLabel addLabel(String text, JPanel panel, int gridx, int gridy, int alignment,
	                        boolean addToPanel) {
		JLabel label = new JLabel(text);
		label.setHorizontalAlignment(JLabel.LEFT);
		label.setBorder(new EmptyBorder(5, 5, 5, 5));
		label.setFont(new Font(null, Font.PLAIN, 13));

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = gridx;
		c.gridy = gridy;
		c.anchor = alignment;

		if (addToPanel) {
			panel.add(label, c);
		}

		return label;
	}

	/**
	 * Add New TextArea to Specified JPanel.
	 *
	 * @param text  Label Text.
	 * @param panel Container Panel.
	 * @param gridx X Location.
	 * @param gridy Y Location.
	 * @return JLabel Object.
	 */
	private JTextArea addTextArea(String text, JPanel panel, int gridx, int gridy, int alignment,
	                              boolean addToPanel) {
		JTextArea textArea = new JTextArea(text, 1, 25);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setBorder(new EmptyBorder(5, 5, 5, 5));

		textArea.setBackground((Color) UIManager.get("Label.background"));
		textArea.setForeground((Color) UIManager.get("Label.foreground"));
		textArea.setFont(new Font(null, Font.PLAIN, 13));

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = gridx;
		c.gridy = gridy;
		c.anchor = alignment;

		if (addToPanel) {
			panel.add(textArea, c);
		}

		return textArea;
	}

	/**
	 * Capture All Action Events.
	 * This  method is called by the Timer and by User Buttons.
	 * This method is called on the Swing Event Dispatch Thread.
	 *
	 * @param e Timer Event.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() != null) {
			if (e.getActionCommand().equals(ACTION_CANCEL)) {
				this.haltRequested = true;
				cancelButton.setEnabled(false);

				//  Immediately inform the user that we are canceling the task
				this.setCancelStatusMsg("Canceling...");

				//  Request that Task Halt
				task.halt();
			} else if (e.getActionCommand().equals(ACTION_CLOSE)) {
				dispose();
			}
		}
	}

	/**
	 * Initialize the Timer Object.
	 * Note that timer events are sent to the Swing Event-Dispatch Thread.
	 */
	private void initTimer() {
		//  Record Start Timestamp
		startTime = new Date();

		//  Create Auto-Timer
		timer = new Timer(TIME_INTERVAL,
		                  new ActionListener() {
				/**
				 * Update the Time Elapsed Field.
				 *
				 * @param e ActionEvent.
				 */
				public void actionPerformed(ActionEvent e) {
					Date currentTime = new Date();
					long timeElapsed = currentTime.getTime() - startTime.getTime();
					timeElapsedValue.setText(StringUtils.getTimeString(timeElapsed));
				}
			});
		timer.start();
	}

	/**
	 * Stops the Internal Timer.
	 */
	private void stopTimer() {
		if ((timer != null) && timer.isRunning()) {
			timer.stop();
		}
	}

	/**
	 * Sets a Cancel Status Message.
	 * <p/>
	 * If we are displaying the status field, put the message there.
	 * Otherwise, put the message in the description field.
	 *
	 * @param msg cancel message.
	 */
	private void setCancelStatusMsg(String msg) {
		if (config.getStatusFlag()) {
			statusValue.setText(msg);
		} else {
			descriptionValue.setText(msg);
		}
	}

	/**
	 * Removes ProgressBar from the UI.
	 */
	private void removeProgressBar() {
		progressPanel.remove(pBar);
		progressPanel.remove(progressValue);
		progressPanel.validate();
	}
}
