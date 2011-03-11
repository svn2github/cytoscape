

package org.cytoscape.command.internal;

import org.cytoscape.task.NetworkTaskFactory;

class NTFExecutor extends TFExecutor {
	private final NetworkTaskFactory ntf;

	public NTFExecutor(NetworkTaskFactory ntf) {
		super(ntf);
		this.ntf = ntf;
	}

	public void execute(String args) {
		System.out.println("set current network!");
		super.execute(args);
	}
}
