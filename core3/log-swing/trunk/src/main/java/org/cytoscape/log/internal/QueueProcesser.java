package org.cytoscape.log.internal;

import java.util.concurrent.BlockingQueue;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author Pasteur
 */
abstract class QueueProcesser implements Runnable
{
	public abstract void processEvent(LoggingEvent event);

	final BlockingQueue<LoggingEvent> queue;

	public QueueProcesser(BlockingQueue<LoggingEvent> queue)
	{
		this.queue = queue;
	}

	public void run()
	{
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

			if (event.equals(QueueAppender.NULL_EVENT))
				break;

			processEvent(event);
		}
	}
}
