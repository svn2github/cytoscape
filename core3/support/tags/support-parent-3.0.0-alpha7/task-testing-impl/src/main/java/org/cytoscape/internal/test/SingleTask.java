
package org.cytoscape.internal.test;


import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;


public class SingleTask extends AbstractTask {
	private boolean showProgress;

	public SingleTask(boolean s) {
		showProgress = s;
	}

	public void run(final TaskMonitor taskMonitor) throws Exception {
		if ( !showProgress )
			taskMonitor.setProgress(-1.0);

		double progress = 0.0;
		if ( showProgress ) taskMonitor.setProgress(progress);
		taskMonitor.setStatusMessage("Excuting task...");
		while(progress < 1.0){ 
			if ( showProgress ) taskMonitor.setStatusMessage("executing step: " + progress);
			if ( showProgress ) taskMonitor.setProgress(progress);
			Thread.sleep(200);
			progress += 0.1;
		}
	}
}
