package org.cytoscape.log.internal;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.log.statusbar.CytoStatusBar;

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

import cytoscape.view.CySwingApplication;

/**
 * @author Pasteur
 */
public class ConsoleTaskFactory implements TaskFactory
{
	final BlockingQueue<LoggingEvent> internalQueue;
	final ExecutorService service;
	final CytoStatusBar statusBar;
	final CySwingApplication app;

	ConsoleDialog dialog = null;

	public ConsoleTaskFactory(CytoStatusBar statusBar, CySwingApplication app)
	{
		this.statusBar = statusBar;
		this.app = app;
		internalQueue = new LinkedBlockingQueue<LoggingEvent>();
		service = Executors.newFixedThreadPool(2, new LowPriorityDaemonThreadFactory());

		StatusBarUpdater updater = new StatusBarUpdater(statusBar, internalQueue);
		service.submit(updater);
		statusBar.addActionListener(new ConsoleAction());
	}

	public Task getTask()
	{
		return new ConsoleTask();
	}

	ConsoleDialog getDialog()
	{
		if (dialog == null)
		{
			dialog = new ConsoleDialog(statusBar, app);
			ConsoleDialogUpdater updater = new ConsoleDialogUpdater(dialog, internalQueue);
			service.submit(updater);
		}
		return dialog;
	}

	class ConsoleTask implements Task
	{
		public void run(TaskMonitor taskMonitor)
		{
			getDialog().setVisible(true);
		}

		public void cancel()
		{
		}
	}

	class ConsoleAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			getDialog().setVisible(true);
		}
	}
}

class ConsoleDialogUpdater extends QueueProcesser
{
	static DateFormat DATE_FORMATTER = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

	final ConsoleDialog dialog;

	public ConsoleDialogUpdater(ConsoleDialog dialog, BlockingQueue<LoggingEvent> internalQueue)
	{
		super(internalQueue);
		this.dialog = dialog;
	}

	public void processEvent(LoggingEvent event)
	{
		String message = event.getMessage().toString();
		String timeStamp = DATE_FORMATTER.format(new Date(event.getTimeStamp()));
		dialog.append(event.getLevel(), message, timeStamp);
	}
}

class StatusBarUpdater extends QueueProcesser
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
		super(Queues.getUserLogQueue());
		this.statusBar = statusBar;
		this.internalQueue = internalQueue;
	}

	public void processEvent(LoggingEvent event)
	{
		String message = event.getMessage().toString();
		ImageIcon icon = getIcon(event.getLevel().toInt());
		statusBar.setMessage(message, icon);
		internalQueue.offer(event);
	}
}
