package org.cytoscape.log.internal;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskMonitor;

import org.apache.log4j.Logger;

public class TestTaskFactory implements TaskFactory
{
	public TestTaskFactory()
	{
	}

	public Task getTask()
	{
		return new Task()
		{
			public void run(TaskMonitor taskMonitor)
			{
				Logger logger = Logger.getLogger(TestTaskFactory.class);
				logger.info("wow!");
			}

			public void cancel()
			{
			}
		};
	}
}
