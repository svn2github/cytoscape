package org.cytoscape.rest.internal;

import org.cytoscape.rest.internal.net.server.CommandPostResponder;
import org.cytoscape.rest.internal.net.server.LocalHttpServer;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.osgi.framework.BundleContext;


import java.util.Properties;
import java.util.concurrent.Executors;

public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {
		
		final CommandExecutorTaskFactory cetf = getService(bc,CommandExecutorTaskFactory.class);
		final SynchronousTaskManager stm = getService(bc,SynchronousTaskManager.class);

		
		Thread serverThread = new Thread() {
			
			private LocalHttpServer server;
			
			@Override
			public void run() {
				server = new LocalHttpServer(2609, Executors.newSingleThreadExecutor());
				server.addPostResponder(new CommandPostResponder(cetf,stm));
				server.run();
			}
		};
		serverThread.setDaemon(true);
		Executors.newSingleThreadExecutor().execute(serverThread);
		
	}
}

