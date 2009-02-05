package org.cytoscape.work.internal.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.ResourceBundle;
import java.util.Locale;

import org.cytoscape.work.TaskManager;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Uses Swing components to create a user interface for the <code>Task</code>.
 *
 * This will not work if the application is running in headless mode.
 */
public class SwingTaskManager implements TaskManager
{
	/**
	 * The delay between the execution of the <code>Task</code> and
	 * showing its task dialog.
	 *
	 * When a <code>Task</code> is executed, <code>SwingTaskManager</code>
	 * will not show its task dialog immediately. It will delay for a
	 * period of time before showing the dialog. This way, short lived
	 * <code>Task</code>s won't have a dialog box.
	 */
	static final int DELAY_IN_MILLISECONDS_BEFORE_SHOWING_DIALOG = 1000;
	Frame owner;
	Locale locale;

	/**
	 * @throws MissingResourceException when the SwingTaskManager
	 * resource bundle is missing
	 */
	public SwingTaskManager()
	{
		this(null, Locale.getDefault());
	}

	/**
	 * @param owner JDialogs created by this object
	 * will have its owner set to this parameter.
	 * @param locale The locale to display messages in
	 * @throws MissingResourceException when the SwingTaskManager
	 * resource bundle is missing
	 */
	public SwingTaskManager(Frame owner, Locale locale)
	{
		setOwner(owner);
		this.locale = locale;

		// We call this so we get MissingResourceException thrown
		// as early as possible in case the bundle is missing
		ResourceBundle.getBundle("SwingTaskManager", locale);
	}

	/**
	 * @param owner JDialogs created by this <code>TaskManager</code>
	 * will have its owner set to this parameter.
	 */
	public void setOwner(Frame owner)
	{
		this.owner = owner;
	}

	public void execute(final Task task)
	{
		final SwingTaskMonitor taskMonitor = new SwingTaskMonitor(task, owner, locale);
		final Thread executor = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					task.run(taskMonitor);
				}
				catch (Exception exception)
				{
					taskMonitor.showException(exception);
				}
				if (taskMonitor.isOpened() && !taskMonitor.isShowingException())
					taskMonitor.close();
			}
		});

		final ActionListener delayedDisplayer = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (executor.getState() != Thread.State.TERMINATED)
					taskMonitor.open();
			}
		};
		Timer timer = new Timer(DELAY_IN_MILLISECONDS_BEFORE_SHOWING_DIALOG, delayedDisplayer);
		timer.setRepeats(false);

		executor.start();
		timer.start();
	}
}

class SwingTaskMonitor implements TaskMonitor
{
	final Task		task;
	final ResourceBundle	messages;
	final Frame		owner;

	TaskDialog	dialog			= null;
	String		title			= null;
	String		statusMessage		= null;
	int		progress		= 0;

	public SwingTaskMonitor(Task task, Frame owner, Locale locale)
	{
		this.task = task;
		this.messages = ResourceBundle.getBundle("SwingTaskManager", locale);
		this.owner = owner;
	}

	public synchronized void open()
	{
		dialog = new TaskDialog(owner, this);
		if (title != null)
			dialog.setTaskTitle(title);
		if (statusMessage != null)
			dialog.setStatus(statusMessage);
		if (progress > 0)
			dialog.setPercentCompleted(progress);
	}

	public void close()
	{
		if (dialog != null)
		{
			dialog.dispose();
			dialog = null;
		}
	}

	public void cancel()
	{
		// The Close button has two possible states that we need
		// to take into account:
		// 1. Cancel: the user has requested to cancel the task
		// 2. Close:  the Task threw an exception, and the
		//            dialog should close
		
		if (isShowingException())
			close();
		else
		{
			// we need to inform the Task to cancel

			// change the UI to show that we are cancelling the Task
			//closeButton.setEnabled(false);
			//closeButton.setText(messages.getString("canceling"));

			// we issue the Task's cancel method in its own thread
			// to prevent Swing from freezing if the Tasks's cancel
			// method takes too long to finish
			Thread cancelThread = new Thread(new Runnable()
			{
				public void run()
				{
					task.cancel();
				}
			});
			cancelThread.start();
		}
	}

	public void setTitle(String title)
	{
		this.title = title;
		if (dialog != null)
			dialog.setTaskTitle(title);
	}

	public void setStatusMessage(String statusMessage)
	{
		this.statusMessage = statusMessage;
		if (dialog != null)
			dialog.setStatus(statusMessage);
	}

	public void setProgress(double progress)
	{
		this.progress = (int) (progress * 100);
		if (dialog != null)
			dialog.setPercentCompleted(this.progress);
	}

	public synchronized void showException(Exception exception)
	{
		// force the dialog box to be created if
		// the Task throws an exception
		if (dialog == null)
			open();
		dialog.setException(exception, "The task could not be completed because an error has occurred.");
	}

	public boolean isShowingException()
	{
		return dialog.errorOccurred();
	}

	public boolean isOpened()
	{
		return dialog != null;
	}
}
