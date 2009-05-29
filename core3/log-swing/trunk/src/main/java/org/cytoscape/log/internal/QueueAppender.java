package org.cytoscape.log.internal;

import java.util.Queue;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Layout;

class QueueAppender extends AppenderSkeleton implements Appender
{
	final Queue<LoggingEvent> queue;

	public QueueAppender(Queue<LoggingEvent> queue)
	{
		this.queue = queue;
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
		queue.offer(event);
	}

	public boolean requiresLayout()
	{
		return false;
	}

	public void close()
	{
	}
}
