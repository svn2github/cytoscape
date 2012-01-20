



package org.cytoscape.internal.test;

import org.cytoscape.work.TaskManager;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;


import org.osgi.framework.BundleContext;

import org.cytoscape.service.util.AbstractCyActivator;

import java.util.Properties;



public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}


	public void start(BundleContext bc) {

		CyRootNetworkManager rootNetworkManagerServiceRef = getService(bc,CyRootNetworkManager.class);
		
		InfiniteTaskFactory infiniteTaskFactory = new InfiniteTaskFactory(rootNetworkManagerServiceRef);
		
		Properties infiniteTaskFactoryProps = new Properties();
		infiniteTaskFactoryProps.setProperty("preferredMenu","Help");
		infiniteTaskFactoryProps.setProperty("title","Subnetwork Test...");
		registerService(bc,infiniteTaskFactory,NetworkTaskFactory.class, infiniteTaskFactoryProps);
	}
}

