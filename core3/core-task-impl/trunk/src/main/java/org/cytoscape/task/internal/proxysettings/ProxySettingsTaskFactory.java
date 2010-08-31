package org.cytoscape.task.internal.proxysettings;


import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.io.util.StreamUtil;


public class ProxySettingsTaskFactory implements TaskFactory {
	ProxySettingsTask pst;
	
	public ProxySettingsTaskFactory(TaskManager taskManager, StreamUtil streamUtil) {
		this.pst = new ProxySettingsTask(taskManager, streamUtil);
	}

	public TaskIterator getTaskIterator() {
		return new TaskIterator(pst);
	}
}
