package org.cytoscape.log.internal;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import cytoscape.view.CySwingApplication;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Appender;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.Date;
import java.text.DateFormat;

/**
 * @author Pasteur
 */
public class DeveloperLogTaskFactory implements TaskFactory
{
	final ExecutorService service;
	final BlockingQueue<LoggingEvent> queue;
	final CySwingApplication app;
	final TaskManager manager;

	DeveloperLogDialog dialog = null;

	public DeveloperLogTaskFactory(ExecutorService service, BlockingQueue<LoggingEvent> queue, CySwingApplication app, TaskManager manager)
	{
		this.service = service;
		this.queue = queue;
		this.app = app;
		this.manager = manager;
	}

	public Task getTask()
	{
		return new DeveloperLogTask();
	}

	class DeveloperLogTask implements Task
	{
		public void run(TaskMonitor monitor)
		{
			getDialog().setVisible(true);
		}

		public void cancel()
		{
		}
	}

	DeveloperLogDialog getDialog()
	{
		if (dialog == null)
		{
			dialog = new DeveloperLogDialog(app, manager);
			DeveloperLogUpdater updater = new DeveloperLogUpdater(dialog, queue);
			service.submit(updater);
		}
		return dialog;
	}
}

class DeveloperLogUpdater extends QueueProcesser
{
	static final DateFormat DATE_FORMATTER = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

	final DeveloperLogDialog dialog;

	public DeveloperLogUpdater(DeveloperLogDialog dialog, BlockingQueue<LoggingEvent> queue)
	{
		super(queue);
		this.dialog = dialog;
	}

	public void processEvent(LoggingEvent event)
	{
		String[] formattedEvent = new String[5];
		formattedEvent[0] = DATE_FORMATTER.format(new Date(event.getTimeStamp()));
		formattedEvent[1] = event.getLogger().getName();
		formattedEvent[2] = event.getLevel().toString().toLowerCase();
		formattedEvent[3] = event.getThreadName();
		formattedEvent[4] = event.getMessage().toString();

		dialog.addLogEvent(formattedEvent);
	}
}
