package org.cytoscape.log.internal;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.spi.LoggingEvent;

class Queues
{
	static BlockingQueue<LoggingEvent> userLogQueue = new LinkedBlockingQueue<LoggingEvent>();
	static BlockingQueue<LoggingEvent> developerLogQueue = new LinkedBlockingQueue<LoggingEvent>();

	public static BlockingQueue<LoggingEvent> getUserLogQueue()
	{
		return userLogQueue;
	}

	public static BlockingQueue<LoggingEvent> getDeveloperLogQueue()
	{
		return developerLogQueue;
	}
}
