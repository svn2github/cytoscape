package org.cytoscape.log.internal;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskMonitor;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Appender;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.Date;
import java.text.DateFormat;

public class ConsoleTaskFactory implements TaskFactory
{
	Task task = new ConsoleTask();

	public Task getTask()
	{
		return task;
	}
}

class ConsoleTask implements Task
{
	ConsoleDialog dialog = null;

	public void run(TaskMonitor taskMonitor)
	{
		if (dialog == null)
		{
			dialog = new ConsoleDialog();
			ConsoleDialogUpdater updater = new ConsoleDialogUpdater(dialog);
			ExecutorService service = Executors.newSingleThreadExecutor(new LowPriorityDaemonThreadFactory());
			service.submit(updater);
		}
		dialog.setVisible(true);
	}

	public void cancel()
	{
	}
}

class LowPriorityDaemonThreadFactory implements ThreadFactory
{
	public Thread newThread(Runnable r)
	{
		Thread thread = new Thread(r);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.setDaemon(true);
		return thread;
	}
}

class ConsoleDialogUpdater implements Runnable
{
	static DateFormat DATE_FORMATTER = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
	ConsoleDialog dialog;

	public ConsoleDialogUpdater(ConsoleDialog dialog)
	{
		this.dialog = dialog;
	}

	public void run()
	{
		final BlockingQueue<LoggingEvent> queue = Queues.getUserLogQueue();

		while (true)
		{
			LoggingEvent event = null;
			try
			{
				event = queue.take();
			}
			catch (InterruptedException e)
			{
				break;
			}
			String message = event.getMessage().toString();
			String timeStamp = DATE_FORMATTER.format(new Date(event.getTimeStamp()));
			dialog.append(event.getLevel(), message, timeStamp);
		}
	}
}
