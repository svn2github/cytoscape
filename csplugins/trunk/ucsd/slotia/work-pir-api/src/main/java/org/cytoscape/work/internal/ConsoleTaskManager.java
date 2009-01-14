package org.cytoscape.work.internal;

import java.io.PrintStream;
import java.util.Timer;
import java.util.TimerTask;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;

public class ConsoleTaskManager implements TaskManager
{
	final PrintStream output;

	public ConsoleTaskManager(PrintStream output)
	{
		this.output = output;
	}

	public ConsoleTaskManager()
	{
		this(System.out);
	}

	public void execute(final Task task)
	{
		final Timer timer = new Timer();
		final TaskMonitor taskMonitor = new ConsoleTaskMonitor(timer);

		Runnable runnable = new Runnable()
		{
			public void run()
			{
				task.run(taskMonitor);
				timer.cancel();
			}
		};

		Thread thread = new Thread(runnable);
		thread.start();
	}

	class ConsoleTaskMonitor implements TaskMonitor
	{
		static final int UPDATE_DELAY = 2000;

		final Timer timer;
		String title = "Task";
		String statusMessage = "";
		int progress = 0;
		boolean hasChanged = false;

		public ConsoleTaskMonitor(Timer timer)
		{
			this.timer = timer;
			timer.scheduleAtFixedRate(new UpdateTask(), UPDATE_DELAY, UPDATE_DELAY);
		}

		public void setTitle(String title)
		{
			if (title == null || title.length() == 0)
				this.title = "Task";
			else
				this.title = title;
		}

		public boolean needsToCancel()
		{
			return false;
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

		public void setException(Throwable exception)
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
