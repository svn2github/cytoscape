
package org.cytoscape.internal.test;


import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;


public class InfiniteTask extends AbstractTask {

	public InfiniteTask() { }

	public void run(final TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setProgress(0.0);
		taskMonitor.setStatusMessage("Excuting forever...");
		while(true){ 
			System.out.println("still working..."); 
//			Thread.sleep(1000);
//			if ( cancelled ) {
//				System.out.println("cancelling Infinite Task");
//				return;
//			}
		}
	}
}
