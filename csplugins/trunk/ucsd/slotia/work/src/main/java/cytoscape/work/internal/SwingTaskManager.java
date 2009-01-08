package cytoscape.work.internal;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.ResourceBundle;
import java.util.Locale;
import java.io.*;

import cytoscape.work.TaskManager;
import cytoscape.work.Task;
import cytoscape.work.Progressable;

import cytoscape.work.internal.util.Interfaces;

public class SwingTaskManager implements TaskManager
{

	final Frame owner;

	public SwingTaskManager()
	{
		this.owner = null;
	}

	public SwingTaskManager(Frame owner)
	{
		this.owner = owner;
	}

	public void execute(Task task)
	{
		TaskThread taskThread = new TaskThread(task);
		taskThread.start();
	}
}

/**
 * Executes the task in its own thread and
 * manages a user interface that monitors
 * the task.
 *
 * The Swing dialog box and its associated components are specified in
 * TaskDialog. TaskDialogUpdater, which is called by javax.swing.Timer,
 * periodically updates the TaskDialog.
 *
 * One would expect TaskDialogUpdater to call Task methods like
 * getTitle() or cancel() to determine the state of the Task and to update
 * TaskDialog accordingly, but this not the case. TaskDialogUpdater
 * merely reads variables declared in TaskThread and updates TaskDialog.
 * TaskMonitor is a Thread that perodically calls getTitle() or getProgress().
 * It then stores the results of these methods in variables used by
 * TaskDialogUpdater.
 *
 * Why is TaskDialog updated in this seemingly byzantine way? The problem is that
 * TaskDialogUpdater is called by Swing, so it must return quickly. If it does
 * not, Swing will freeze. We cannot guarantee that Task methods like
 * getProgress() or cancel() will return quickly, so we create a separate
 * Thread called TaskMonitor to call these methods. That way, if Task's 
 * getStatusMessage() takes a long time to return, it will freeze up
 * TaskMonitor, but it won't affect TaskDialogUpdater, and it won't freeze Swing.
 */
class TaskThread extends Thread
{
	// Specifies how often TaskMonitor and TaskDialogUpdater should be executed
	static final int REFRESH_DELAY_IN_MILLISECONDS = 250;

	// The task to be executed
	final Task task;

	// Executes TaskDialogUpdater
	final Timer timer;

	// Displays the user interface for the task
	final TaskDialog taskDialog;

	// State variables that are shared between the nested classes
	String title = "";		// = task.getTitle()
	String statusMessage = "";	// = ((Progressable) task).getStatusMessage()
	int progress = 0;		// = ((Progressable) task).getProgress()
	boolean cancelTask = false;	// Becomes true when the user clicks "Cancel," so task.cancel() must be called
	Throwable exception = null;	// Becomes non-null when the Task throws an exception

	public TaskThread(Task task)
	{
		this.task = task;
		timer = new Timer(REFRESH_DELAY_IN_MILLISECONDS, new TaskDialogUpdater());
		taskDialog = new TaskDialog();
	}

	public void run()
	{
		TaskMonitor taskMonitor = new TaskMonitor();
		taskMonitor.start();
		timer.start();

		System.out.println("TaskThread: started");
		try
		{
			task.run();
		}
		catch (Throwable exception)
		{
			this.exception = exception;
		}
		System.out.println("TaskThread: finished");
	}

	/**
	 * Monitors the Task by periodically checking its state.
	 *
	 * This has the following chores:
	 * 1) Cancels the task by calling task.cancel() if TaskThread.cancelTask is true
	 * 2) Updates TaskThread.title by calling task.getTitle()
	 * 3) If Task implements Progressable, updates TaskThread.statusMessage by calling task.getStatusMessage()
	 * 4) If Task implements Progressable, updates TaskThread.progress by calling task.getProgress()
	 *
	 * This does not tell TaskDialog to showException()--this is the job of TaskDialogUpdater.
	 *
	 * TaskMonitor ends under the following conditions:
	 * 1) TaskThread's state is TERMINATED
	 * 2) cancelTask is true
	 */
	class TaskMonitor extends Thread
	{
		public void run()
		{
			System.out.println("TaskMonitor: started");

			// Keep monitoring the Task while it is running
			while (TaskThread.this.getState() != Thread.State.TERMINATED)
			{
				try
				{
					Thread.sleep(REFRESH_DELAY_IN_MILLISECONDS);
				}
				catch (InterruptedException e) {}

				// When the thread is not RUNNABLE, it is either sleeping or waiting
				// and is not making any progress, so it's pointless to check its state.
				if (TaskThread.this.getState() != Thread.State.RUNNABLE)
					continue;

				if (cancelTask)
				{
					task.cancel();
					// If the task needs to be cancelled, it does not need
					// to be monitored anymore.
					return;
				}

				// Update TaskThread.title
				if (title.length() == 0)
				{
					title = task.getTitle();
					if (title == null)
						title = "";
				}

				// Update TaskThread.progress and TaskThread.statusMessage
				if (Interfaces.implementsProgressable(task))
				{
					Progressable progressable = (Progressable) task;
					statusMessage = progressable.getStatusMessage();
					progress = (int) (progressable.getProgress() * 100);
				}
			}
			System.out.println("TaskMonitor: finished");
		}
	}

	/**
	 * Displays the Swing dialog.
	 *
	 * This has the following chores:
	 * 1) Deals with all Swing component stuff
	 * 2) Sets TaskThread.cancelTask to true if the user clicks "Cancel"
	 */
	class TaskDialog
	{
		final JDialog		dialog;
		final JLabel		statusMessageLabel;
		final JProgressBar	progressBar;
		final JButton		closeButton;
		final JPanel		exceptionPanel;
		final JLabel		exceptionLabel;
		final JTextArea		exceptionArea;

		public TaskDialog()
		{
			dialog = new JDialog();
			dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.PAGE_AXIS));
			statusMessageLabel = new JLabel();
			progressBar = new JProgressBar(0, 100);
			if (Interfaces.implementsProgressable(task))
				progressBar.setStringPainted(true);
			else
				progressBar.setIndeterminate(true);
			closeButton = new JButton("Cancel");
			closeButton.addActionListener(new CloseAction());

			exceptionPanel = new JPanel();
			exceptionPanel.setLayout(new BoxLayout(exceptionPanel, BoxLayout.PAGE_AXIS));
			exceptionLabel = new JLabel();
			exceptionArea = new JTextArea();
			exceptionPanel.add(exceptionLabel);
			exceptionPanel.add(exceptionArea);
			exceptionPanel.setVisible(false);
			
			dialog.add(statusMessageLabel);
			dialog.add(progressBar);
			dialog.add(closeButton);
			dialog.add(exceptionPanel);

			dialog.pack();
			dialog.setVisible(true);
		}

		public void close()
		{
			dialog.dispose();
		}

		class CloseAction implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				if (closeButton.getText().equals("Close"))
					close();
				else
				{
					cancelTask = true;
					closeButton.setEnabled(false);
					closeButton.setText("Canceling...");
				}
			}
		}

		public void setTitle(String title)
		{
			dialog.setTitle(title);
		}

		public String getTitle()
		{
			return dialog.getTitle();
		}

		public void setStatusMessage(String statusMessage)
		{
			statusMessageLabel.setText(statusMessage);
		}

		public void setProgress(int progress)
		{
			progressBar.setValue(progress);
		}

		public void showException()
		{
			closeButton.setText("Close");
			closeButton.setEnabled(true);
			StringWriter stringWriter = new StringWriter();
			exception.printStackTrace(new PrintWriter(stringWriter));
			exceptionLabel.setText(exception.getMessage());
			exceptionArea.setText(stringWriter.toString());
			exceptionPanel.setVisible(true);
			pack();
		}
	}

	/**
	 * Periodically updates TaskDialog according the variables
	 * defined in TaskThread.
	 *
	 * This has the following chores:
	 * 1) If TaskThread.exception is not null, call TaskDialog's showException()
	 * 2) Stops the Timer that calls TaskDialogUpdater and closes TaskDialog
	 *    when the Task has finished
	 * 3) Updates TaskDialog's title according to TaskThread.title
	 * 4) If Task implements Progressable, update TaskDialog's progress and statusMessage.
	 */
	class TaskDialogUpdater implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (exception != null)
				taskDialog.showException();

			if (TaskThread.this.getState() == Thread.State.TERMINATED)
			{
				// Stop calling TaskDialogUpdater
				timer.stop();
				// Close TaskDialog unless it's showing an exception.
				if (exception == null)
					taskDialog.close();
			}

			// Only update TaskDialog's title if a title has not been set
			if (taskDialog.getTitle() == null && title.length() != 0)
				taskDialog.setTitle(title);

			if (Interfaces.implementsProgressable(task))
			{
				taskDialog.setProgress(progress);
				taskDialog.setStatusMessage(statusMessage);
			}
		}
	}
}
