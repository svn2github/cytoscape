package org.cytoscape.log.internal;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskMonitor;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

public class TestTaskFactory implements TaskFactory
{
	public Task getTask()
	{
		return new TestTask();
	}
}

class TestTask implements Task
{
	public void run(TaskMonitor taskMonitor)
	{
		Logger logger = Logger.getLogger("org.cytoscape.userlog");
		logger.error("ghalib");
		logger.warn("faiz");
		logger.info("iqbal");
	}

	public void cancel()
	{
	}
}
