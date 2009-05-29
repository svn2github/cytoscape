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
		}
		dialog.setVisible(true);
	}

	public void cancel()
	{
	}
}

class ConsoleDialogUpdater implements Runnable
{
	static final Map<Integer,String> LEVEL_TO_ICON_MAP = new TreeMap<Integer,String>();
	static
	{
		LEVEL_TO_ICON_MAP.put(Level.DEBUG.toInt(),	"info");
		LEVEL_TO_ICON_MAP.put(Level.ERROR.toInt(),	"error");
		LEVEL_TO_ICON_MAP.put(Level.FATAL.toInt(),	"error");
		LEVEL_TO_ICON_MAP.put(Level.INFO.toInt(),	"info");
		LEVEL_TO_ICON_MAP.put(Level.TRACE.toInt(),	"info");
		LEVEL_TO_ICON_MAP.put(Level.WARN.toInt(),	"warning");
	}
	ConsoleDialog dialog;

	public ConsoleDialogUpdater(ConsoleDialog dialog)
	{
		this.dialog = dialog;
	}

	public void run()
	{
		BlockingQueue<LoggingEvent> queue = new LinkedBlockingQueue<LoggingEvent>();
		Appender appender = new QueueAppender(queue);
		Logger logger = Logger.getLogger("org.cytoscape.userlog");
		logger.setAdditivity(false);
		logger.addAppender(appender);

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
			String icon = LEVEL_TO_ICON_MAP.get(event.getLevel().toInt());
			if (icon == null)
				icon = "info";
			String timeStamp = DateFormat.getInstance().format(new Date(event.getTimeStamp()));
			dialog.append(icon, message, timeStamp);
		}
	}
}
