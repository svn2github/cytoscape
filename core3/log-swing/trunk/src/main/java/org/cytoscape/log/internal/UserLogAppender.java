package org.cytoscape.log.internal;

public class UserLogAppender extends QueueAppender
{
	public UserLogAppender()
	{
		super(Queues.getUserLogQueue());
	}
}
