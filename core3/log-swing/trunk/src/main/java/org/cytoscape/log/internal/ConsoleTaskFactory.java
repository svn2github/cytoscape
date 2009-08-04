package org.cytoscape.log.internal;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.log.statusbar.CytoStatusBar;

import org.apache.log4j.spi.LoggingEvent;

import java.util.Map;
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
	final BlockingQueue<LoggingEvent> simpleQueue;
	final BlockingQueue<LoggingEvent> advancedQueue;
	final ExecutorService service;
	final CytoStatusBar statusBar;
	final CySwingApplication app;
	final TaskManager manager;
	final Map simpleLogConfig;
	final Map advancedLogConfig;

	ConsoleDialog dialog = null;
	SimpleLogViewer simpleLogViewer = null;
	AdvancedLogViewer advancedLogViewer = null;

	public ConsoleTaskFactory(	BlockingQueue<LoggingEvent> simpleQueue,
					BlockingQueue<LoggingEvent> advancedQueue,
					ExecutorService service,
					CytoStatusBar statusBar,
					CySwingApplication app,
					TaskManager manager,
					Map simpleLogConfig,
					Map advancedLogConfig)
	{
		this.simpleQueue = simpleQueue;
		this.advancedQueue = advancedQueue;
		this.service = service;
		this.statusBar = statusBar;
		this.app = app;
		this.manager = manager;
		this.simpleLogConfig = simpleLogConfig;
		this.advancedLogConfig = advancedLogConfig;

		statusBar.addActionListener(new ConsoleAction());
	}

	public Task getTask()
	{
		return new ConsoleTask();
	}

	synchronized ConsoleDialog getDialog()
	{
		if (dialog == null)
		{
			simpleLogViewer = new SimpleLogViewer(statusBar, new LogViewer(simpleLogConfig));
			advancedLogViewer = new AdvancedLogViewer(manager, new LogViewer(advancedLogConfig));
			dialog = new ConsoleDialog(app, simpleLogViewer, advancedLogViewer);
			SimpleUpdater simpleUpdater = new SimpleUpdater(simpleLogViewer, simpleQueue);
			service.submit(simpleUpdater);
			AdvancedUpdater advancedUpdater = new AdvancedUpdater(advancedLogViewer, advancedQueue);
			service.submit(advancedUpdater);
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

class SimpleUpdater extends QueueProcesser
{
	static DateFormat DATE_FORMATTER = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG);

	final SimpleLogViewer simpleLogViewer;

	public SimpleUpdater(SimpleLogViewer simpleLogViewer, BlockingQueue<LoggingEvent> internalQueue)
	{
		super(internalQueue);
		this.simpleLogViewer = simpleLogViewer;
	}

	public void processEvent(LoggingEvent event)
	{
		String message = event.getMessage().toString();
		String timeStamp = DATE_FORMATTER.format(new Date(event.getTimeStamp()));
		simpleLogViewer.append(event.getLevel().toString(), message, timeStamp);
	}
}

class AdvancedUpdater extends QueueProcesser
{
	static final DateFormat DATE_FORMATTER = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);

	final AdvancedLogViewer advancedLogViewer;

	public AdvancedUpdater(AdvancedLogViewer advancedLogViewer, BlockingQueue<LoggingEvent> queue)
	{
		super(queue);
		this.advancedLogViewer = advancedLogViewer;
	}

	public void processEvent(LoggingEvent event)
	{
		String[] formattedEvent = new String[5];
		formattedEvent[0] = DATE_FORMATTER.format(new Date(event.getTimeStamp()));
		formattedEvent[1] = event.getLogger().getName();
		formattedEvent[2] = event.getLevel().toString().toLowerCase();
		formattedEvent[3] = event.getThreadName();
		formattedEvent[4] = event.getMessage().toString();

		advancedLogViewer.addLogEvent(formattedEvent);
	}
}
