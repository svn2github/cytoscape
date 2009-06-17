package org.cytoscape.log.internal;

public class DeveloperLogAppender extends QueueAppender
{
	public DeveloperLogAppender()
	{
		super(Queues.getDeveloperLogQueue());
	}
}
