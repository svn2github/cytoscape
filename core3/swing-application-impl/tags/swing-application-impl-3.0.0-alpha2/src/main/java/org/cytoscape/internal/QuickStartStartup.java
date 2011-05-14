package org.cytoscape.internal;

import org.cytoscape.application.swing.events.CytoscapeStartEvent;
import org.cytoscape.application.swing.events.CytoscapeStartListener;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.swing.GUITaskManager;

/*
 * This class listens CytoscapeStartEvent and pop up QuickStart dialog
 */
public class QuickStartStartup implements CytoscapeStartListener {

	private TaskFactory quickStartTaskFactory;
	private GUITaskManager guiTaskManager;
	
	public QuickStartStartup(TaskFactory quickStartTaskFactory, GUITaskManager guiTaskManager){	
		this.quickStartTaskFactory = quickStartTaskFactory;
		this.guiTaskManager = guiTaskManager;
	}
	
	public void handleEvent(CytoscapeStartEvent e){
		this.guiTaskManager.execute(this.quickStartTaskFactory);
	}	
}
