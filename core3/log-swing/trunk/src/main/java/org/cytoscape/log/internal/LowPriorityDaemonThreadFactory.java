package org.cytoscape.log.internal;

import java.util.concurrent.ThreadFactory;

/**
 * @author Pasteur
 */
class LowPriorityDaemonThreadFactory implements ThreadFactory
{
	public Thread newThread(Runnable r)
	{
		Thread thread = new Thread(r);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.setDaemon(true);
		return thread;
	}
}


