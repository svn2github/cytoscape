/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package csplugins.task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User Interface (UI) Component for Monitoring Long Term Tasks.
 * <P>
 * <H3>About the TaskMonitorUI Class</H3>
 * The TaskMonitorUI is a JFrame component for monitoring / managing long term
 * tasks.  It is designed to be used in conjuction with the
 * {@link Task Task} interface and the {@link BaseTask BaseTask} implementation.
 * <P>
 * A sample screenshot of TaskMonitorUI in action is shown below:
 * <P>
 * <IMG SRC="doc-files/taskMonitorUI.png" BORDER=0/>
 * <p/>
 * <H3>Using the TaskMonitorUI Class</H3>
 * To use the TaskMonitorUI class, you must first instantiate a
 * {@link Task Task} of your choosing.  You then use this task to instantiate
 * a copy of TaskMonitorUI.  For example:
 * <PRE>
 * Task task = new SampleTask(100, 2000, 2000, 50);
 * TaskMonitorUI monitor = new TaskMonitorUI(task);
 * task.start();
 * </PRE>
 * <H3>Features</H3>
 * <UL>
 * <LI>Shows a progress message, estimated time left, and total
 * time elapsed.
 * <LI>Provides support for indeterminate/determinate tasks.
 * <LI>Provides a Cancel button (available by default) for cancelling long-
 * running tasks.
 * <LI>Provides a Close button (available by default) for closing the
 * component.
 * <LI>Time Fields, e.g. Time Remaining and Time Elapsed Fields are optional.
 * <LI>Component can be automatically disposed / hidden when task completes.
 * </UL>
 * <H3>To Do Items:</H3>
 * This component is currently under construction.  To do items include:
 * <UL>
 * <LI>Add a millisecond delay;  e.g. don't show component if the task
 * completes in under XX milliseconds.  This would be useful for potentially
 * fast running tasks.  For example, if something only takes .5 seconds
 * to complete, why bother showing a whole progress bar.
 * <LI>Center Component Relative to Parent Frame (if available.)
 * </UL>
 *
 * @author Ethan Cerami
 */
public class TaskMonitorUI extends JFrame implements ActionListener {
    /**
     * The task we are monitoring.
     */
    private Task task;

    /**
     * ProgressBar UI component.
     */
    private JProgressBar pBar;

    /**
     * Time Interval for updating progress bar and progress messages.
     */
    private static final int TIME_INTERVAL = 50;

    /**
     * Status value label.
     */
    private JLabel statusValue;

    /**
     * Time remaining label.
     */
    private JLabel timeRemainingValue;

    /**
     * Time elapsed label.
     */
    private JLabel timeElapsedValue;

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
     * Show Time Fields Flag.
     * By default, show Time Fields.
     */
    private boolean showTimeFields = true;

    /**
     * Show User Cancel / Close Buttons.
     * By default, show buttons.
     */
    private boolean showUserButtons = true;

    /**
     * Disposes of Component When Task Completes.
     * By default, do not automatically dispose of component.
     */
    private boolean disposeWhenTaskCompletes = false;

    /**
     * Constructor.
     *
     * @param task The task to monitor.
     */
    public TaskMonitorUI(Task task) {
        this.task = task;
        initUI();
    }

    /**
     * Constructor.
     *
     * @param task                     The task to monitor.
     * @param showTimeFields           Show Time Fields, e.g. Time Left and Time
     *                                 Elapsed (by default time fields are
     *                                 shown.)
     * @param showUserButtons          Show User Buttons, e.g. Cancel and Close
     *                                 (by default, user buttons are shown.)
     * @param disposeWhenTaskCompletes Automatically dispose/hide component when
     *                                 task completes (by default, automatic
     *                                 disposal is not enabled).
     */
    public TaskMonitorUI(Task task, boolean showTimeFields, boolean
            showUserButtons, boolean disposeWhenTaskCompletes) {
        this.showTimeFields = showTimeFields;
        this.showUserButtons = showUserButtons;
        this.disposeWhenTaskCompletes = disposeWhenTaskCompletes;
        this.task = task;
        initUI();
    }

    /**
     * Initializes UI.
     */
    private void initUI() {
        this.setTitle(task.getTaskDescription());
        Container container = this.getContentPane();
        container.setLayout(new BorderLayout());
        JPanel progressPanel = new JPanel(new GridBagLayout());
        progressPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        int y = 0;

        addLabel("Description:  ", progressPanel, 0, y,
                GridBagConstraints.EAST, true);
        addLabel(task.getTaskDescription(), progressPanel, 1, y,
                GridBagConstraints.WEST, true);

        addLabel("Status:  ", progressPanel, 0, ++y,
                GridBagConstraints.EAST, true);
        statusValue = addLabel("Starting...", progressPanel, 1, y,
                GridBagConstraints.WEST, true);

        addLabel("Time Left:  ", progressPanel, 0, ++y,
                GridBagConstraints.EAST, showTimeFields);
        timeRemainingValue = addLabel("", progressPanel, 1, y,
                GridBagConstraints.WEST, showTimeFields);

        addLabel("Time Elapsed:  ", progressPanel, 0, ++y,
                GridBagConstraints.EAST, showTimeFields);
        timeElapsedValue = addLabel("", progressPanel, 1, y,
                GridBagConstraints.WEST, showTimeFields);

        addLabel("Progress:  ", progressPanel, 0, ++y,
                GridBagConstraints.EAST, true);

        initProgressBar(progressPanel, y);
        container.add(progressPanel, BorderLayout.CENTER);
        createFooter(container);

        this.initTimer();
        this.pack();
        this.setResizable(false);
        setLocationRelativeTo(null); 
        this.show();
    }

    /**
     * Creates Footer with Close, Cancel Buttons.
     *
     * @param container Container Object.
     */
    private void createFooter(Container container) {
        JPanel footer = new JPanel();
        footer.setLayout(new GridLayout(1, 2));
        closeButton = new JButton("Close");
        closeButton.setEnabled(false);
        closeButton.setActionCommand(ACTION_CLOSE);
        closeButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand(ACTION_CANCEL);
        cancelButton.addActionListener(this);

        if (showUserButtons) {
            footer.add(closeButton);
            footer.add(cancelButton);
        }
        container.add(footer, BorderLayout.SOUTH);
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
        pBar.setStringPainted(true);
        pBar.setMaximum(Integer.MAX_VALUE);
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
    private JLabel addLabel(String text, JPanel panel, int gridx, int gridy,
            int alignment, boolean addToPanel) {
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
     * Capture All Action Events.
     * This  method is called by the Timer and by User Buttons.
     * This method is called on the Swing Event Dispatch Thread.
     *
     * @param e Timer Event.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() != null) {
            if (e.getActionCommand().equals(ACTION_CANCEL)) {
                cancelButton.setEnabled(false);
                task.interrupt();
            } else if (e.getActionCommand().equals(ACTION_CLOSE)) {
                this.dispose();
            }
        }
        updateProgress();
    }

    /**
     * Updates Progress Bar and Various Progress Fields.
     * While Task is Running (not yet done), update the UI
     */
    private void updateProgress() {
        if (!task.isDone()) {
            if (task.isIndeterminate()) {
                pBar.setIndeterminate(true);
                pBar.setStringPainted(false);
                timeRemainingValue.setText
                        (TaskUtil.getTimeString(-1L));
            } else {
                pBar.setIndeterminate(false);
                pBar.setStringPainted(true);
                pBar.setValue(task.getProgressValue());
                pBar.setMaximum(task.getMaxProgressValue());
                timeRemainingValue.setText
                        (TaskUtil.getTimeString
                        (task.getEstimatedTimeRemaining()));
            }
            statusValue.setText(task.getProgressMessage());
        } else {
            taskIsFinished();
        }
        timeElapsedValue.setText
                (TaskUtil.getTimeString(task.getTimeElapsed()));
        this.pack();
    }

    /**
     * Task is now Finished.
     * Stop the Timer and Stop UI Updates.
     */
    private void taskIsFinished() {
        timer.stop();
        pBar.setIndeterminate(false);
        pBar.setValue(Integer.MAX_VALUE);
        if (task.isInterrupted()) {
            statusValue.setText("Task Canceled by User.");
        } else {
            statusValue.setText("Done.");
        }
        timeRemainingValue.setText(TaskUtil.getTimeString(0L));
        closeButton.setEnabled(true);
        cancelButton.setEnabled(false);

        //  Optionally Dispose of Component when Task Completes.
        if (disposeWhenTaskCompletes) {
            this.hide();
            this.dispose();
        }
    }

    /**
     * Initialize the Timer Object.
     */
    private void initTimer() {
        timer = new Timer(TIME_INTERVAL, this);
        timer.start();
    }
}