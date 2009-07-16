package org.cytoscape.log.internal;

/**
 * @author Pasteur
 */
public class UserLogAppender extends QueueAppender
{
	public UserLogAppender()
	{
		super(Queues.getUserLogQueue());
	}
}
