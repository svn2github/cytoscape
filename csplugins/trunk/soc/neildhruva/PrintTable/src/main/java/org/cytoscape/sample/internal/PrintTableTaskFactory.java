package org.cytoscape.sample.internal;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class PrintTableTaskFactory extends AbstractTaskFactory {
	
	private CyApplicationManager manager;
	private CySwingApplication desktopApp;
	private final CytoPanel cytoPanelSouth;
	private MyCytoPanel myCytoPanel;
	
	/**
	 * Class constructor invoked by the <code>CyActivator</code> class
	 * 
	 *  @param manager an instance of <code>CyApplicationManager</code> that is used to manage the current network
	 */
	public PrintTableTaskFactory(CyApplicationManager manager, CySwingApplication desktopApp, MyCytoPanel myCytoPanel){
		this.manager = manager;
		this.desktopApp = desktopApp;
		this.cytoPanelSouth = this.desktopApp.getCytoPanel(CytoPanelName.SOUTH);
		this.myCytoPanel = myCytoPanel;
	}
	
	/**
	 * @see org.cytoscape.work.TaskFactory#createTaskIterator()
	 */
	public TaskIterator createTaskIterator(){
		// If the state of the cytoPanelSouth is HIDE, show it
		if (cytoPanelSouth.getState() == CytoPanelState.HIDE) {
			cytoPanelSouth.setState(CytoPanelState.DOCK);
		}	

		// Select my panel
		int index = cytoPanelSouth.indexOfComponent(myCytoPanel);
		if (index == -1) {
			return null;
		}
		cytoPanelSouth.setSelectedIndex(index);
		
		return new TaskIterator(new PrintTableTask(manager, desktopApp, myCytoPanel));
	}
}
