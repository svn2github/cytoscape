package org.cytoscape.work.internal.tunables;

import java.util.Map;

import org.cytoscape.work.TaskFactory;


public interface CLTaskFactory{
	
	void addTaskFactory(TaskFactory factory, Map props);
	void removeTaskFactory(TaskFactory factory, Map props);
}
