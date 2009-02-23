
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

package org.cytoscape.work.internal.swing;

import org.cytoscape.work.internal.swing.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;


/**
 * Common UI element for visually monitoring task progress.
 */
class TaskDialog extends JDialog implements ActionListener {
	static final long serialVersionUID = 333614801L;
	static final int TIME_INTERVAL = 500;

	final SwingTaskMonitor parent;

	JProgressBar	pBar;
	JLabel		descriptionValue;
	JTextArea	statusValue;
	JLabel		statusLabel;
	JLabel		timeLabel;
	Timer		timer;
	JButton		closeButton;
	JPanel		progressPanel;

	Date		startTime;
	boolean		haltRequested = false;
	boolean		errorOccurred = false;

	/**
	 * Constructor.
	 */
	public TaskDialog(Frame frame, SwingTaskMonitor parent) {
		super(frame, false);
		this.parent = parent;

		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		progressPanel = new JPanel(new GridBagLayout());
		progressPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		int y = 0;
		addLabel("Description:  ", progressPanel, 0, y, GridBagConstraints.NORTHEAST, true);
		descriptionValue = addLabel("", progressPanel, 1, y++, GridBagConstraints.NORTHWEST, true);

		addLabel("Status:  ", progressPanel, 0, y, GridBagConstraints.NORTHEAST, true);
		statusValue = addTextArea("", progressPanel, 1, y++, GridBagConstraints.NORTHWEST, true);

		addLabel("Progress:  ", progressPanel, 0, y, GridBagConstraints.NORTHEAST, true);
		initProgressBar(progressPanel, y++);

		timeLabel = addLabel("", progressPanel, 1, y++, GridBagConstraints.NORTHWEST, true);

		closeButton = addButton("   Cancel   ", progressPanel, 1, y, GridBagConstraints.NORTHEAST, true);
		closeButton.addActionListener(this);

		container.add(progressPanel, BorderLayout.PAGE_START);
		//createFooter(container);

		initTimer();

		//  Disable close operation until task is done.
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		this.setVisible(true);

		//  Center component relative to parent component
		//  or relative to user's screen.
		if (frame != null)
			setLocationRelativeTo(frame);
	}

	public void setTaskTitle(String taskTitle)
	{
		setTitle(taskTitle);
		descriptionValue.setText(StringUtils.truncateOrPadString(taskTitle));
	}

	public void setPercentCompleted(final int percent) {
		if (haltRequested || errorOccurred) return;

		if (percent != pBar.getValue())
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run() {
					if (pBar.isIndeterminate()) {
						pBar.setIndeterminate(false);
					}

					pBar.setValue(percent);
				}
			});
		}
	}

	public void setException(final Throwable t, final String userErrorMessage)
	{
		this.errorOccurred = true;
		stopTimer();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run() {
			    closeButton.setEnabled(true);
			    closeButton.setText("   Close   ");
			    statusValue.setEnabled(false);
			    progressPanel.setEnabled(false);

			    //  Create Error Panel
			    JPanel errorPanel = new ErrorPanel(TaskDialog.this, t, userErrorMessage, null);
			    getContentPane().add(errorPanel, BorderLayout.CENTER);
			    pack();

			    //  Make sure JTask is actually visible
			    if (!TaskDialog.this.isShowing()) {
				TaskDialog.this.setVisible(true);
			    }
		}
	    });
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
						statusValue.setVisible(true);
						statusValue.setText(StringUtils.truncateOrPadString(message));
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
	 * Creates Footer with Close, Cancel Buttons.
	 *
	 * @param container Container Object.
	 */
	private void createFooter(Container container) {
	/*
		JPanel footer = new JPanel();
		footer.setBorder(new EmptyBorder(5, 5, 5, 5));
		footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
		closeButton = new JButton("    Cancel    ");
		closeButton.addActionListener(this);
		footer.add(closeButton);
		footer.add(Box.createHorizontalGlue());

		container.add(footer, BorderLayout.EAST);
		*/
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

	private JButton addButton(String text, JPanel parentPanel, int gridx, int gridy, int alignment,
	                              boolean addToPanel) {
		JButton button = new JButton(text);
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(button);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = gridx;
		c.gridy = gridy;
		c.anchor = alignment;

		if (addToPanel) {
			parentPanel.add(panel, c);
		}

		return button;
	}

	/**
	 * Capture All Action Events.
	 * This  method is called by the Timer and by User Buttons.
	 * This method is called on the Swing Event Dispatch Thread.
	 *
	 * @param e Timer Event.
	 */
	public void actionPerformed(ActionEvent e) {
		stopTimer();
		if (!errorOccurred)
		{
			this.haltRequested = true;
			closeButton.setEnabled(false);
			closeButton.setText("  Cancelling...  ");
		}
		parent.cancel();
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
				public void actionPerformed(ActionEvent e) {
					Date currentTime = new Date();
					long timeElapsed = currentTime.getTime() - startTime.getTime();
					String timeElapsedString = StringUtils.getTimeString(timeElapsed);
					if (!pBar.isIndeterminate() && pBar.getValue() != 0)
					{
						long timeRemaining = (long) ((100.0 / pBar.getValue() - 1.0) * timeElapsed);
						String timeRemainingString = StringUtils.getTimeString(timeRemaining);
						timeLabel.setText(String.format("%s elapsed, %s remaining", timeElapsedString, timeRemainingString));
					}
					else
					{
						timeLabel.setText(String.format("%s elapsed", timeElapsedString));
					}
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
}
