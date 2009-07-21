package org.cytoscape.log.internal;

import org.apache.log4j.spi.LoggingEvent;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueueFactory
{
	public static BlockingQueue<LoggingEvent> newBlockingQueue()
	{
		return new LinkedBlockingQueue<LoggingEvent>();
	}
}
