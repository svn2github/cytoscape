package cytoscape.work.internal;

import java.util.Locale;
import java.util.ResourceBundle;

import java.io.PrintStream;
import java.io.IOException;

import cytoscape.work.TaskManager;
import cytoscape.work.Task;
import cytoscape.work.Progressable;

import cytoscape.work.internal.util.Interfaces;

public class ConsoleTaskManager implements TaskManager
{
	final PrintStream output;
	final ResourceBundle messages;

	public ConsoleTaskManager()
	{
		this(System.out, Locale.getDefault());
	}
	
	public ConsoleTaskManager(PrintStream output, Locale locale)
	{
		this.output = output;
		messages = ResourceBundle.getBundle("ConsoleTaskManager", locale);
	}

	public void execute(Task task)
	{
		Thread taskExecutor = new Thread(new TaskExecutor(task));
		Thread taskMonitor = new Thread(new TaskMonitor(task, taskExecutor));

		taskExecutor.start();
		taskMonitor.start();
	}

	/**
	 * Wraps a <code>Task</code> so that it can be
	 * executed by a <code>Thread</code>.
	 * This class catches any exception thrown
	 * by the <code>Task</code> and prints out
	 * information about the exception.
	 */
	class TaskExecutor implements Runnable
	{
		final Task task;

		public TaskExecutor(Task task)
		{
			this.task = task;
		}

		public void run()
		{
			final String taskTitle = task.getTitle();

			try
			{
				output.println(String.format(messages.getString("task_started"), taskTitle));
				task.run();
				output.println(String.format(messages.getString("task_finished"), taskTitle));
			}
			catch (Exception exception)
			{
				if (exception.getMessage() != null && exception.getMessage().length() != 0)
					output.print(String.format(messages.getString("task_failed_no_message"), taskTitle));
				else
					output.print(String.format(messages.getString("task_failed_with_message"), taskTitle));

				output.println(String.format(messages.getString("exception_stack_trace_begin"), taskTitle));
				exception.printStackTrace(output);
				output.println(String.format(messages.getString("exception_stack_trace_end"), taskTitle));
			}
		}
	}

	/**
	 * Monitors the task for updates. This is executed
	 * in its own thread apart from the <code>Task</code>'s
	 * <code>Thread</code>.
	 */
	class TaskMonitor implements Runnable
	{
		static final int REFRESH_DELAY_IN_MILLISECONDS = 1000;

		final Task task;
		final Thread thread;

		public TaskMonitor(Task task, Thread thread)
		{
			this.task = task;
			this.thread = thread;
		}

		public void run()
		{
			final String taskTitle = task.getTitle();
			String statusMessage = "";
			int progress = 0;

			while (thread.getState() != Thread.State.TERMINATED)
			{
				try
				{
					Thread.sleep(REFRESH_DELAY_IN_MILLISECONDS);
				}
				catch (InterruptedException e) {}

				if (thread.getState() != Thread.State.RUNNABLE) continue;

				if (Interfaces.implementsProgressable(task))
				{
					Progressable progressable = (Progressable) task;
					String currentStatusMessage = progressable.getStatusMessage();
					if (currentStatusMessage == null) currentStatusMessage = "";
					int currentProgress = (int) (progressable.getProgress() * 100);

					if (!statusMessage.equals(currentStatusMessage) || currentProgress > progress)
					{
						statusMessage = currentStatusMessage;
						progress = currentProgress;
						if (statusMessage.length() == 0)
							output.println(String.format(messages.getString("task_progress_no_message"), taskTitle, progress));
						else
							output.println(String.format(messages.getString("task_progress_with_message"), taskTitle, progress, statusMessage));
					}
				}  // end if
			} // end while
		} // end run()
	}
}
