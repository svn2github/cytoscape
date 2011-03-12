

package org.cytoscape.command.internal;

import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.work.TunableInterceptor;
import org.cytoscape.session.CyApplicationManager;

class NTFExecutor extends TFExecutor {
	private final NetworkTaskFactory ntf;
	private final CyApplicationManager appMgr;

	public NTFExecutor(NetworkTaskFactory ntf, TunableInterceptor interceptor, CyApplicationManager appMgr) {
		super(ntf,interceptor);
		this.ntf = ntf;
		this.appMgr = appMgr;
	}

	public void execute(String args) {
		System.out.println("set current network!");
		ntf.setNetwork( appMgr.getCurrentNetwork() );
		super.execute(args);
	}
}
