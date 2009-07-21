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

	public StatusBarUpdater(CytoStatusBar statusBar, BlockingQueue<LoggingEvent> queue)
	{
		super(queue);
		this.statusBar = statusBar;
	}

	public void processEvent(LoggingEvent event)
	{
		String message = event.getMessage().toString();
		ImageIcon icon = getIcon(event.getLevel().toInt());
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
