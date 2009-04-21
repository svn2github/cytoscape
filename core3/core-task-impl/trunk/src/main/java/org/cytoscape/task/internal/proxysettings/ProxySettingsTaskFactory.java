package org.cytoscape.task.internal.proxysettings;


import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.io.util.CyProxyRegistry;

public class ProxySettingsTaskFactory implements TaskFactory
{
	TaskManager taskManager;
	CyProxyRegistry proxyRegistry;
	ProxySettingsTask pst;
	
	public ProxySettingsTaskFactory(TaskManager taskManager, CyProxyRegistry proxyRegistry)
	{
		System.out.println("ProxySettingsTaskFactory has been instantiated");
		this.taskManager = taskManager;
		this.proxyRegistry = proxyRegistry;
		this.pst = new ProxySettingsTask(taskManager, proxyRegistry);
	}

	public Task getTask()
	{
//		return new ProxySettingsTask(taskManager,proxyRegistry);
		return pst;
	}
}
