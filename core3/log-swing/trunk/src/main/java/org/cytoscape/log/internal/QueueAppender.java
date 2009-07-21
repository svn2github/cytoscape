package org.cytoscape.log.internal;

import java.util.Queue;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Level;

/**
 * @author Pasteur
 */
public class QueueAppender extends AppenderSkeleton implements Appender
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

	public void append(LoggingEvent event)
	{
		for (Filter	filter = getFirstFilter();
				filter != null;
				filter = filter.getNext())
		{
			final int result = filter.decide(event);
			if (result == Filter.ACCEPT)
				break;
			else if (result == Filter.DENY)
				return;
		}

		if (	event.getLevel().equals(Level.INFO) || 
			event.getLevel().equals(Level.WARN))
		{
			userLogQueue.offer(event);
			statusBarQueue.offer(event);
		}
		else
			developerLogQueue.offer(event);
	}

	public boolean requiresLayout()
	{
		return false;
	}

	public void close()
	{
	}
}
