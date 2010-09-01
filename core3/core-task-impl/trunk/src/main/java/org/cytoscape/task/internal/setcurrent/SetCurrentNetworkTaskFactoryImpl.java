package org.cytoscape.task.internal.setcurrent;


import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

// TODO Verify that we need this class in headless mode!
/**
 * This TaskFactory is for headless mode and not GUI mode. This
 * factory shouldn't be registered by the swing GUI as it doesn't
 * make sense in that context.
 */
public class SetCurrentNetworkTaskFactoryImpl implements TaskFactory {

	private CyNetworkManager netmgr;

	public SetCurrentNetworkTaskFactoryImpl(CyNetworkManager netmgr) {
		this.netmgr = netmgr;
	}

	public TaskIterator getTaskIterator() {
		return new TaskIterator(new SetCurrentNetworkTask(netmgr));
	}
}
