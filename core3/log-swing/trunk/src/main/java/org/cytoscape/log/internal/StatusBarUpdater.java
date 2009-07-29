package org.cytoscape.log.internal;

import org.cytoscape.log.statusbar.CytoStatusBar;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.Level;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import javax.swing.ImageIcon;

class StatusBarUpdater extends QueueProcesser
{
	static final Map<Integer,String> LEVEL_TO_ICON_MAP = new TreeMap<Integer,String>();
	static
	{
		LEVEL_TO_ICON_MAP.put(Level.DEBUG_INTEGER,	"/petit-info.png");
		LEVEL_TO_ICON_MAP.put(Level.ERROR_INTEGER,	"/petit-error.png");
		LEVEL_TO_ICON_MAP.put(Level.INFO_INTEGER,	"/petit-info.png");
		LEVEL_TO_ICON_MAP.put(Level.TRACE_INTEGER,	"/petit-info.png");
		LEVEL_TO_ICON_MAP.put(Level.WARN_INTEGER,	"/petit-warning.png");
	}

	ImageIcon getIcon(Integer level)
        {
		String path = LEVEL_TO_ICON_MAP.get(level);
		if (path == null)
			path = "/petit-info.png";
		return new ImageIcon(getClass().getResource(path));
        }

	final CytoStatusBar statusBar;

	public StatusBarUpdater(CytoStatusBar statusBar, BlockingQueue<LoggingEvent> queue)
	{
		super(queue);
		this.statusBar = statusBar;
	}

	public void processEvent(LoggingEvent event)
	{
		String message = event.getMessage().toString();
		ImageIcon icon = getIcon(event.getLevel().toInteger());
		statusBar.setMessage(message, icon);
	}

	public static StatusBarUpdater executeStatusBarUpdater(	ExecutorService service,
								CytoStatusBar statusBar,
								BlockingQueue<LoggingEvent> queue)
	{
		StatusBarUpdater updater = new StatusBarUpdater(statusBar, queue);
		service.submit(updater);
		return updater;
	}
}
