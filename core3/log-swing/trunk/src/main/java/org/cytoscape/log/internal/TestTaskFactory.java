package org.cytoscape.log.internal;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskMonitor;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

public class TestTaskFactory implements TaskFactory
{
	int i = 0;
	public Task getTask()
	{
		Task task = new TestTask(i);
		i = (i + 1) % 4;
		return task;
	}
}

class TestTask implements Task
{
	int i;
	public TestTask(int i)
	{
		this.i = i;
	}

	public void run(TaskMonitor taskMonitor)
	{
		Logger logger = Logger.getLogger("org.cytoscape.userlog2");
		if (i == 0)
			logger.error("Happiness is a warm gun");
		else if (i == 1)
			logger.warn("When I hold you in my arms");
		else if (i == 2)
			logger.info("And I put my finger on your trigger");
		else if (i == 3)
			logger.info("I know nobody can do me no harm, because...");
	}

	public void cancel()
	{
	}
}
