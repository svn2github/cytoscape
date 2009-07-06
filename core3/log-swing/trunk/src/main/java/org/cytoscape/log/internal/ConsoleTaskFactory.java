package org.cytoscape.log.internal;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskMonitor;
import cytoscape.view.CytoStatusBar;

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

import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/**
 * @author Pasteur
 */
public class ConsoleTaskFactory implements TaskFactory
{
	final ConsoleTask task;

	public ConsoleTaskFactory(CytoStatusBar statusBar)
	{
		BlockingQueue<LoggingEvent> internalQueue = new LinkedBlockingQueue<LoggingEvent>();
		ExecutorService service = Executors.newFixedThreadPool(2, new LowPriorityDaemonThreadFactory());

		StatusBarUpdater updater = new StatusBarUpdater(statusBar, internalQueue);
		service.submit(updater);

		task = new ConsoleTask(internalQueue, service, statusBar);
		statusBar.addActionListener(task);
	}

	public Task getTask()
	{
		return task;
	}
}

class ConsoleTask implements Task, ActionListener
{
	final BlockingQueue<LoggingEvent> internalQueue;
	final ExecutorService service;
	final CytoStatusBar statusBar;

	ConsoleDialog dialog = null;

	public ConsoleTask(BlockingQueue<LoggingEvent> internalQueue, ExecutorService service, CytoStatusBar statusBar)
	{
		this.internalQueue = internalQueue;
		this.service = service;
		this.statusBar = statusBar;
	}

	public void run(TaskMonitor taskMonitor)
	{
		actionPerformed(null);
	}

	public void cancel()
	{
	}

	public void actionPerformed(ActionEvent e)
	{
		if (dialog == null)
		{
			dialog = new ConsoleDialog(statusBar);
			ConsoleDialogUpdater updater = new ConsoleDialogUpdater(dialog, internalQueue);
			service.submit(updater);
		}
		dialog.setVisible(true);
	}
}

class ConsoleDialogUpdater implements Runnable
{
	static DateFormat DATE_FORMATTER = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

	final ConsoleDialog dialog;
	final BlockingQueue<LoggingEvent> internalQueue;

	public ConsoleDialogUpdater(ConsoleDialog dialog, BlockingQueue<LoggingEvent> internalQueue)
	{
		this.dialog = dialog;
		this.internalQueue = internalQueue;
	}

	public void run()
	{
		while (true)
		{
			LoggingEvent event = null;
			try
			{
				event = internalQueue.take();
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

class StatusBarUpdater implements Runnable
{
	static final Map<Integer,String> LEVEL_TO_ICON_MAP = new TreeMap<Integer,String>();
	static
	{
		LEVEL_TO_ICON_MAP.put(Level.DEBUG.toInt(),	"/petit-info.png");
		LEVEL_TO_ICON_MAP.put(Level.ERROR.toInt(),	"/petit-error.png");
		LEVEL_TO_ICON_MAP.put(Level.FATAL.toInt(),	"/petit-error.png");
		LEVEL_TO_ICON_MAP.put(Level.INFO.toInt(),	"/petit-info.png");
		LEVEL_TO_ICON_MAP.put(Level.TRACE.toInt(),	"/petit-info.png");
		LEVEL_TO_ICON_MAP.put(Level.WARN.toInt(),	"/petit-warning.png");
	}

        ImageIcon getIcon(int level)
        {
		String path = LEVEL_TO_ICON_MAP.get(level);
		if (path == null)
			path = "/petit-info.png";
		return new ImageIcon(getClass().getResource(path));
        }

	final CytoStatusBar statusBar;
	final BlockingQueue<LoggingEvent> internalQueue;

	public StatusBarUpdater(CytoStatusBar statusBar, BlockingQueue<LoggingEvent> internalQueue)
	{
		this.statusBar = statusBar;
		this.internalQueue = internalQueue;
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
			ImageIcon icon = getIcon(event.getLevel().toInt());
			statusBar.setMessage(message, icon);

			internalQueue.offer(event);
		}
	}
}
