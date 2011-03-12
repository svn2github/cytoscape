
package org.cytoscape.command.internal;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.Task;
import org.cytoscape.work.TunableInterceptor;

class TFExecutor implements Executor {
	private final TaskFactory tf;
	private final TunableInterceptor interceptor; 
	private final TaskMonitor tm = new OutTaskMonitor(); 

	public TFExecutor(TaskFactory tf, TunableInterceptor interceptor) {
		this.tf = tf;
		this.interceptor = interceptor;
	}

	public void execute(String args) {
		try {
		System.out.println("executing: " + tf + "   with args: '" + args + "'");
		TaskIterator ti = tf.getTaskIterator();
		while (ti.hasNext()) {
			Task t = ti.next();
			interceptor.loadTunables(t);
			interceptor.execUI(t);
			t.run(tm);
		}
		} catch (Exception e) {
			System.out.println("task failed!");
			e.printStackTrace();
		}
	}

	private class OutTaskMonitor implements TaskMonitor {
		public void setTitle(String title) {
			System.out.println("set title: " + title);
		}
		public void setProgress(double progress) {
			System.out.println("set progress: " + progress);
		}
		public void setStatusMessage(String statusMessage) {
			System.out.println("set statusMessage: " + statusMessage);
		}

	}
}
