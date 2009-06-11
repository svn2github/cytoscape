package org.cytoscape.work.internal.tunables;

import java.util.Map;

import org.cytoscape.work.TaskFactory;

import cytoscape.util.CyAction;

public interface CLTaskFactory{
	
	void addAction(CyAction action);
	void removeAction(CyAction action);
	void addTaskFactory(TaskFactory factory, Map props);
	void removeTaskFactory(TaskFactory factory, Map props);
}