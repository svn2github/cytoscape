package org.cytoscape.work.internal.task;

import org.cytoscape.work.TaskManager;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import java.awt.Frame;

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
	static final long DELAY_BEFORE_SHOWING_DIALOG = 1;

	/**
	 * The time unit of <code>DELAY_BEFORE_SHOWING_DIALOG</code>.
	 */
	static final TimeUnit DELAY_TIMEUNIT = TimeUnit.SECONDS;

	/**
	 * Used for calling <code>Task.run()</code>.
	 */
	ExecutorService taskExecutorService;

	/**
	 * Used for opening dialogs after a specific amount of delay.
	 */
	ScheduledExecutorService timedDialogExecutorService;

	/**
	 * Used for calling <code>Task.cancel()</code>.
	 * <code>Task.cancel()</code> must be called in a different
	 * thread from the thread running Swing. This is done to
	 * prevent Swing from freezing if <code>Task.cancel()</code>
	 * takes too long to finish.
	 *
	 * This can be the same as <code>taskExecutorService</code>.
	 */
	ExecutorService cancelExecutorService;

	Frame owner;

	/**
	 * Construct with default behavior.
	 * <ul>
	 * <li><code>owner</code> is set to null.</li>
	 * <li><code>taskExecutorService</code> is a cached thread pool.</li>
	 * <li><code>timedExecutorService</code> is a single thread executor.</li>
	 * <li><code>cancelExecutorService</code> is the same as <code>taskExecutorService</code>.</li>
	 * </ul>
	 */
	public SwingTaskManager()
	{
		owner = null;
		taskExecutorService = Executors.newCachedThreadPool();
		addShutdownHook(taskExecutorService);
		timedDialogExecutorService = Executors.newSingleThreadScheduledExecutor();
		addShutdownHook(timedDialogExecutorService);
		cancelExecutorService = taskExecutorService;
	}

	/**
	 * Adds a shutdown hook to the JVM that shuts down an
	 * <code>ExecutorService</code>. <code>ExecutorService</code>s
	 * need to be told to shut down, otherwise the JVM won't 
	 * cleanly terminate.
	 */
	void addShutdownHook(final ExecutorService serviceToShutdown)
	{
		// Used to create a thread that is executed by the shutdown hook
		ThreadFactory threadFactory = Executors.defaultThreadFactory();

		Runnable shutdownHook = new Runnable()
		{
			public void run()
			{
				serviceToShutdown.shutdownNow();
			}
		};
		Runtime.getRuntime().addShutdownHook(threadFactory.newThread(shutdownHook));
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
		final SwingTaskMonitor taskMonitor = new SwingTaskMonitor(task, cancelExecutorService, owner);
		final Runnable executor = new Runnable()
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
		};
		final Future<?> executorFuture = taskExecutorService.submit(executor);

		final Runnable timedOpen = new Runnable()
		{
			public void run()
			{
				if (!(executorFuture.isDone() || executorFuture.isCancelled()))
					taskMonitor.open();
			}
		};
		timedDialogExecutorService.schedule(timedOpen, DELAY_BEFORE_SHOWING_DIALOG, DELAY_TIMEUNIT);
	}
}

class SwingTaskMonitor implements TaskMonitor
{
	final Task		task;
	final ExecutorService	cancelExecutorService;
	final Frame		owner;

	TaskDialog	dialog			= null;
	String		title			= null;
	String		statusMessage		= null;
	int		progress		= 0;

	public SwingTaskMonitor(Task task, ExecutorService cancelExecutorService, Frame owner)
	{
		this.task = task;
		this.cancelExecutorService = cancelExecutorService;
		this.owner = owner;
	}

	public synchronized void open()
	{
		if (dialog != null)
			return;

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
		// we issue the Task's cancel method in its own thread
		// to prevent Swing from freezing if the Tasks's cancel
		// method takes too long to finish
		Runnable cancel = new Runnable()
		{
			public void run()
			{
				task.cancel();
			}
		};
		cancelExecutorService.submit(cancel);
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
		this.progress = (int) Math.floor(progress * 100);
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
