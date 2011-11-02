
package org.cytoscape.internal.test;


import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;


public class SingleTask extends AbstractTask {

	public void run(final TaskMonitor taskMonitor) throws Exception {
		double progress = 0.0;
		taskMonitor.setProgress(progress);
		taskMonitor.setStatusMessage("Excuting task...");
		while(progress < 1.0){ 
			taskMonitor.setStatusMessage("executing step: " + progress);
			taskMonitor.setProgress(progress);
			Thread.sleep(200);
			progress += 0.1;
		}
	}
}
