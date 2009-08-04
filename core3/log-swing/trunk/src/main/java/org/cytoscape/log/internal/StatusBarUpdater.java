package org.cytoscape.log.internal;

import org.cytoscape.log.statusbar.CytoStatusBar;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Level;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import javax.swing.ImageIcon;

class StatusBarUpdater extends QueueProcesser
{
	final CytoStatusBar statusBar;
	final Map config;

	public StatusBarUpdater(CytoStatusBar statusBar, BlockingQueue<LoggingEvent> queue, Map config)
	{
		super(queue);
		this.statusBar = statusBar;
		this.config = config;
	}

	public void processEvent(LoggingEvent event)
	{
		String message = event.getMessage().toString();
		String iconPath = config.get(event.getLevel().toString()).toString();
		ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
		statusBar.setMessage(message, icon);
	}

	public static StatusBarUpdater executeStatusBarUpdater(	ExecutorService service,
								CytoStatusBar statusBar,
								BlockingQueue<LoggingEvent> queue,
								Map config)
	{
		StatusBarUpdater updater = new StatusBarUpdater(statusBar, queue, config);
		service.submit(updater);
		return updater;
	}
}
