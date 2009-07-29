package org.cytoscape.log.internal;

import java.util.Queue;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Appender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;

/**
 * @author Pasteur
 */
public class QueueAppender extends AppenderBase<LoggingEvent> implements Appender<LoggingEvent>
{
	final Queue<LoggingEvent> userLogQueue;
	final Queue<LoggingEvent> statusBarQueue;
	final Queue<LoggingEvent> developerLogQueue;

	public QueueAppender(Queue<LoggingEvent> userLogQueue, Queue<LoggingEvent> statusBarQueue, Queue<LoggingEvent> developerLogQueue)
	{
		this.userLogQueue = userLogQueue;
		this.statusBarQueue = statusBarQueue;
		this.developerLogQueue = developerLogQueue;
	}

	protected void append(LoggingEvent event)
	{
		if (	event.getLevel().equals(Level.INFO) || 
			event.getLevel().equals(Level.WARN))
		{
			userLogQueue.offer(event);
			statusBarQueue.offer(event);
		}

		developerLogQueue.offer(event);
	}
}
