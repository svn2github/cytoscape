package org.cytoscape.sample.internal;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.work.TaskFactory;

import org.osgi.framework.BundleContext;

import java.util.Properties;


public class CyActivator extends AbstractCyActivator {

	/*
	 * Class constructor
	 */
	public CyActivator() {
		super();
	}

	/*
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bc) {
		
		CyApplicationManager cyApplicationManagerService = getService(bc,CyApplicationManager.class);
		PrintTableTaskFactory printTableTaskFactory = new PrintTableTaskFactory(cyApplicationManagerService);
		
		Properties printTableTaskFactoryProps = new Properties();
		printTableTaskFactoryProps.setProperty("preferredMenu","Apps");
		printTableTaskFactoryProps.setProperty("menuGravity","11.0");
		printTableTaskFactoryProps.setProperty("title","Print Table");
		
		registerService(bc,printTableTaskFactory,TaskFactory.class, printTableTaskFactoryProps);
	}
}

