package org.cytoscape.sample.internal;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class PrintTableTaskFactory extends AbstractTaskFactory {
	
	private CyApplicationManager manager;
	
	/*
	 * Class constructor invoked by the <code>CyActivator</code> class
	 * 
	 *  @param manager an instance of <code>CyApplicationManager</code> that is used to manage the current network
	 */
	public PrintTableTaskFactory(CyApplicationManager manager){
		this.manager=manager;
	}
	
	/*
	 * @see org.cytoscape.work.TaskFactory#createTaskIterator()
	 */
	public TaskIterator createTaskIterator(){
		return new TaskIterator(new PrintTableTask(manager));
	}
}
