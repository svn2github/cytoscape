package org.cytoscape.work.internal;

import java.io.PrintStream;
import java.util.Timer;
import java.util.TimerTask;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;

/**
 * Executes <code>Task</code>s and displays interfaces by
 * writing to a <code>PrintStream</code>.
 *
 * This is better suited for applications running in headless mode.
 *
 * This will only periodically display information about
 * <code>Task</code>s it is executing to prevent the screen from being flooded
 * with messages.
 *
 * This cannot cancel <code>Task</code>s because it has no means for receiving
 * input from the user.
 */
public class ConsoleTaskManager implements TaskManager
{
	final PrintStream output;

	public ConsoleTaskManager(PrintStream output)
	{
		this.output = output;
	}

	/**
	 * Use <code>System.out</code> as the output stream.
	 */
	public ConsoleTaskManager()
	{
		this(System.out);
	}

	public void execute(final Task task)
	{
		final Timer timer = new Timer();
		final ConsoleTaskMonitor taskMonitor = new ConsoleTaskMonitor(timer);

		Runnable runnable = new Runnable()
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
				timer.cancel();
			}
		};

		Thread thread = new Thread(runnable);
		thread.start();
	}

	class ConsoleTaskMonitor implements TaskMonitor
	{
		static final int UPDATE_DELAY_IN_MILLISECONDS = 2000;

		final Timer timer;
		String title = "Task";
		String statusMessage = "";
		int progress = 0;
		boolean hasChanged = false;

		public ConsoleTaskMonitor(Timer timer)
		{
			this.timer = timer;
			timer.scheduleAtFixedRate(new UpdateTask(), UPDATE_DELAY_IN_MILLISECONDS, UPDATE_DELAY_IN_MILLISECONDS);
		}

		public void setTitle(String title)
		{
			if (title == null || title.length() == 0)
				this.title = "Task";
			else
				this.title = title;
		}

		public void setProgress(double progress)
		{
			this.progress = (int) (progress * 100);
			hasChanged = true;
		}

		public void setStatusMessage(String statusMessage)
		{
			if (statusMessage == null)
				this.statusMessage = "";
			else
				this.statusMessage = statusMessage;
			hasChanged = true;
		}

		public void showException(Exception exception)
		{
			timer.cancel();
			if (exception.getMessage() == null || exception.getMessage().length() == 0)
				output.println(String.format("%s has encountered an error.", title));
			else
				output.println(String.format("%s has encountered an error: %s", title, exception.getMessage()));
			exception.printStackTrace(output);
		}

		class UpdateTask extends TimerTask
		{
			public void run()
			{
				if (!hasChanged) return;

				if (statusMessage.length() == 0)
					output.println(String.format("%s is at %d%%.", title, progress));
				else
					output.println(String.format("%s is at %d%%: %s", title, progress, statusMessage));
				
				hasChanged = false;
			}
		}
	}
}
