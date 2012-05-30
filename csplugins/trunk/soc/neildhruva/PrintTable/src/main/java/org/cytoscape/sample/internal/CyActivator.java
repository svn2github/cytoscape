package org.cytoscape.sample.internal;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.work.TaskFactory;

import org.osgi.framework.BundleContext;

import java.util.Properties;


public class CyActivator extends AbstractCyActivator {

	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {
		
		CyApplicationManager cyApplicationManagerService = getService(bc,CyApplicationManager.class);
		CySwingApplication cySwingApplicationService = getService(bc,CySwingApplication.class);
		MyCytoPanel myCytoPanel = new MyCytoPanel();
		
		PrintTableTaskFactory printTableTaskFactory = new PrintTableTaskFactory(cyApplicationManagerService, cySwingApplicationService, myCytoPanel);
		
		Properties printTableTaskFactoryProps = new Properties();
		printTableTaskFactoryProps.setProperty("preferredMenu","Apps");
		printTableTaskFactoryProps.setProperty("menuGravity","11.0");
		printTableTaskFactoryProps.setProperty("title","Print Table");
		
		registerService(bc,printTableTaskFactory,TaskFactory.class, printTableTaskFactoryProps);
		
		registerService(bc,myCytoPanel,CytoPanelComponent.class, new Properties());
	}
}
