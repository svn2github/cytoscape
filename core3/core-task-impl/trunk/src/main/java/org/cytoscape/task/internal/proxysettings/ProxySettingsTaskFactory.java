package org.cytoscape.task.internal.proxysettings;


import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.io.util.StreamUtil;


public class ProxySettingsTaskFactory implements TaskFactory {
	private TaskManager taskManager;
	private StreamUtil streamUtil;
	
	public ProxySettingsTaskFactory(TaskManager taskManager, StreamUtil streamUtil) {
		this.taskManager = taskManager;
		this.streamUtil = streamUtil;
	}

	public TaskIterator getTaskIterator() {
		return new TaskIterator(new ProxySettingsTask(taskManager, streamUtil));
	}
}
