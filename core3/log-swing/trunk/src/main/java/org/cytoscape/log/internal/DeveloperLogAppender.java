package org.cytoscape.log.internal;

/**
 * @author Pasteur
 */
public class DeveloperLogAppender extends QueueAppender
{
	public DeveloperLogAppender()
	{
		super(Queues.getDeveloperLogQueue());
	}
}
