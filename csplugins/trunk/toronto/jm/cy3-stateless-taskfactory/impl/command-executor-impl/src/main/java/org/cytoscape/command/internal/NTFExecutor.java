

package org.cytoscape.command.internal;

import org.cytoscape.task.NetworkTaskContext;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.command.internal.tunables.CommandTunableInterceptorImpl;

class NTFExecutor extends TFExecutor {
	private final NetworkTaskFactory<NetworkTaskContext> ntf;
	private final CyApplicationManager appMgr;

	public NTFExecutor(NetworkTaskFactory<NetworkTaskContext> ntf, CommandTunableInterceptorImpl interceptor, 
	                   CyApplicationManager appMgr) {
		super(ntf,interceptor);
		this.ntf = ntf;
		this.appMgr = appMgr;
	}

	public void execute(String args) {
		NetworkTaskContext context = ntf.createTaskContext();
		context.setNetwork( appMgr.getCurrentNetwork() );
		super.execute(args, context);
	}
}
