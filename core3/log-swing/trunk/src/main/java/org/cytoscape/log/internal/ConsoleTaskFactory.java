package org.cytoscape.log.internal;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.log.statusbar.CytoStatusBar;

import ch.qos.logback.classic.spi.LoggingEvent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
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
	final BlockingQueue<LoggingEvent> queue;
	final CytoStatusBar statusBar;
	final CySwingApplication app;
	final ExecutorService service;

	ConsoleDialog dialog = null;

	public ConsoleTaskFactory(	BlockingQueue<LoggingEvent> queue,
					CytoStatusBar statusBar,
					CySwingApplication app,
					ExecutorService service)
	{
		this.queue = queue;
		this.statusBar = statusBar;
		this.app = app;
		this.service = service;

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
			ConsoleDialogUpdater updater = new ConsoleDialogUpdater(dialog, queue);
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
