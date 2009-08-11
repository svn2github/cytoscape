package org.cytoscape.log.internal;

import java.util.Queue;

import org.ops4j.pax.logging.spi.PaxAppender;
import org.ops4j.pax.logging.spi.PaxLoggingEvent;

/**
 * @author Pasteur
 */
public class QueueAppender implements PaxAppender
{
	final Queue<PaxLoggingEvent> userLogQueue;
	final Queue<PaxLoggingEvent> statusBarQueue;
	final Queue<PaxLoggingEvent> developerLogQueue;

	public QueueAppender(Queue<PaxLoggingEvent> userLogQueue, Queue<PaxLoggingEvent> statusBarQueue, Queue<PaxLoggingEvent> developerLogQueue)
	{
		this.userLogQueue = userLogQueue;
		this.statusBarQueue = statusBarQueue;
		this.developerLogQueue = developerLogQueue;
	}

	public void doAppend(PaxLoggingEvent event)
	{
		System.out.println(String.format("PaxLoggingEvent (%d - %s): %s", event.getLevel().toInt(), event.getLevel().toString(), event.getMessage()));
		//if (	event.getLevel().equals(Level.INFO) || 
			//event.getLevel().equals(Level.WARN))
		//{
			userLogQueue.offer(event);
			statusBarQueue.offer(event);
		//}

		developerLogQueue.offer(event);
	}
}
