package cytoscape.task.ui;

import cytoscape.task.TaskMonitor;

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
public class JTask extends JDialog implements TaskMonitor, ActionListener {

    /**
     * ProgressBar UI component.
     */
    private JProgressBar pBar;

    /**
     * Time Interval for updating the time elapsed field.
     */
    private static final int TIME_INTERVAL = 500;

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
     * Constructor.
     *
     * @param config JTaskConfig Object.
     */
    public JTask(String title, JTaskConfig config) {
        this.taskTitle = title;
        this.config = config;
        init();
    }

    /**
     * Sets Percentage Complete.
     * Called by a child task thread.
     * Safely queues changes to the Swing Event Dispatch Thread.
     *
     * @param percent Percentage Complete.
     */
    public void setPercentCompleted(final int percent) {
        if (percent < -1 || percent > 100) {
            throw new IllegalArgumentException
                    ("percent is outside range:  [-1, 100]");
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (percent == -1) {
                    pBar.setIndeterminate(true);
                    pBar.setStringPainted(false);
                } else {
                    pBar.setIndeterminate(false);
                    pBar.setStringPainted(true);
                    pBar.setValue(percent);
                }
            }
        });
    }

    /**
     * Sets Estimated Time Remaining.
     * Called by a child task thread.
     * Safely queues changes to the Swing Event Dispatch Thread.
     *
     * @param time Time Remaining, in milliseconds.
     */
    public void setEstimatedTimeRemaining(final long time) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                timeRemainingValue.setText
                        (StringUtils.getTimeString(time));
            }
        });
    }

    /**
     * Reports an Error in Task Processing.
     *
     * @param t                Throwable t.
     * @param userErrorMessage Human Readable Error Message.
     */
    public void setException(Throwable t, String userErrorMessage) {
        stopTimer();

        //  Create Error Panel
        JPanel errorPanel = new JErrorPanel
                ((Window) this, t, userErrorMessage);


        //  Add an Error Icon to the WEST
        Icon icon = UIManager.getIcon("OptionPane.errorIcon");
        JLabel l = new JLabel(icon);
        Container c = getContentPane();
        c.add(l, BorderLayout.WEST);

        //  Add Error Panel to the SOUTH
        c.add(errorPanel, BorderLayout.SOUTH);
        config.setAutoDispose(false);

        //  Now make JFrame Resizable
        setResizable(true);

        pack();
        validate();
        this.setTitle("An Error Has Occurred");
    }

    /**
     * Indicates that the worker task is done processing.
     */
    public void setDone() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                stopTimer();
                if (config.getAutoDispose()) {
                    dispose();
                } else {
                    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    cancelButton.setEnabled(false);
                    closeButton.setEnabled(true);
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
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                statusValue.setText
                        (StringUtils.truncateOrPadString(message));
            }
        });
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
        JPanel progressPanel = new JPanel(new GridBagLayout());
        progressPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        //  Create Description, Status, Time Left and Time Elapsed Fields
        int y = 0;
        addLabel("Description:  ", progressPanel, 0, y,
                GridBagConstraints.EAST, true);
        addLabel(StringUtils.truncateOrPadString(taskTitle),
                progressPanel, 1, y,
                GridBagConstraints.WEST, true);

        addLabel("Status:  ", progressPanel, 0, ++y,
                GridBagConstraints.EAST, config.getStatusFlag());
        statusValue = addLabel
                (StringUtils.truncateOrPadString("Starting..."),
                        progressPanel, 1, y, GridBagConstraints.WEST,
                        config.getStatusFlag());

        addLabel("Time Left:  ", progressPanel, 0, ++y,
                GridBagConstraints.EAST, config.getTimeRemainingFlag());
        timeRemainingValue = addLabel("", progressPanel, 1, y,
                GridBagConstraints.WEST, config.getTimeRemainingFlag());

        addLabel("Time Elapsed:  ", progressPanel, 0, ++y,
                GridBagConstraints.EAST, config.getTimeElapsedFlag());
        timeElapsedValue = addLabel(StringUtils.getTimeString(0),
                progressPanel, 1, y, GridBagConstraints.WEST,
                config.getTimeElapsedFlag());

        addLabel("Progress:  ", progressPanel, 0, ++y,
                GridBagConstraints.EAST, true);

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
        if (config.getMillisToDecideToPopup() == 0) {
            this.show();
        } else {
            autoPopUp();
        }

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

        if (config.getUserButtonFlag()) {
            footer.add(Box.createHorizontalGlue());
            footer.add(closeButton);
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
        pBar.setStringPainted(true);
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
        timer = new Timer(TIME_INTERVAL, new ActionListener() {

            /**
             * Update the Time Elapsed Field.
             *
             * @param e ActionEvent.
             */
            public void actionPerformed(ActionEvent e) {
                Date currentTime = new Date();
                long timeElapsed = currentTime.getTime() - startTime.getTime();
                timeElapsedValue.setText
                        (StringUtils.getTimeString(timeElapsed));
            }
        });
        timer.start();
    }

    /**
     * AutoPopUp after XXX milliseconds.
     */
    private void autoPopUp() {

        //  Create PopUpTimer to go off after XXX milliseconds
        final Timer popUpTimer = new Timer(config.getMillisToDecideToPopup(),
                new ActionListener() {

                    /**
                     * Shows the UI Component.
                     *
                     * @param e ActionEvent.
                     */
                    public void actionPerformed(ActionEvent e) {
                        show();
                    }
                });

        //  Only Fire Once
        popUpTimer.setRepeats(false);

        //  Start PopUp Timer
        popUpTimer.start();
    }

    /**
     * Stops the Internal Timer.
     */
    private void stopTimer() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }
}