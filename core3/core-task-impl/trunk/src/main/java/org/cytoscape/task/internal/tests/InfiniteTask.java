
package org.cytoscape.task.internal.tests;


import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;


public class InfiniteTask extends AbstractTask {

	public InfiniteTask() { }

	public void run(final TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setProgress(0.0);
		while(true){ System.out.println("still working...");}
	}
}
