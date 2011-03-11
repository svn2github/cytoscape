
package org.cytoscape.command.internal;

import org.cytoscape.work.TaskFactory;

class TFExecutor implements Executor {
	private final TaskFactory tf;

	public TFExecutor(TaskFactory tf) {
		this.tf = tf;
	}

	public void execute(String args) {
		System.out.println("executing: " + tf + "   with args: '" + args + "'");
	}
}
