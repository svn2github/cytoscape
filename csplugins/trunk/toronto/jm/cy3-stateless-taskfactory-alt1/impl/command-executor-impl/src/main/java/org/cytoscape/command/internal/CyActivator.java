



package org.cytoscape.command.internal;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.task.TaskFactoryProvisioner;
import org.cytoscape.work.TaskFactory;
import org.osgi.framework.BundleContext;



public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}


	public void start(BundleContext bc) {

		CyApplicationManager cyApplicationManagerServiceRef = getService(bc,CyApplicationManager.class);
		
		TaskFactoryProvisioner factoryProvisioner = getService(bc, TaskFactoryProvisioner.class);
		CommandExecutorImpl commandExecutorImpl = new CommandExecutorImpl(cyApplicationManagerServiceRef, factoryProvisioner);
		CommandExecutorTaskFactory commandExecutorTaskFactory = new CommandExecutorTaskFactory(commandExecutorImpl);
		
		
		Properties commandExecutorTaskFactoryProps = new Properties();
		commandExecutorTaskFactoryProps.setProperty("preferredMenu","Apps");
		commandExecutorTaskFactoryProps.setProperty("title","Load Command File");
		registerService(bc,commandExecutorTaskFactory,TaskFactory.class, commandExecutorTaskFactoryProps);

		registerServiceListener(bc,commandExecutorImpl,"addTaskFactory","removeTaskFactory",TaskFactory.class);
		registerServiceListener(bc,commandExecutorImpl,"addNetworkTaskFactory","removeNetworkTaskFactory",NetworkTaskFactory.class);


	}
}

