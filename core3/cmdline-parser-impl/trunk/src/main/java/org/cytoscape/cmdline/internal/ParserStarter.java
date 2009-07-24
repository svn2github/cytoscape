package org.cytoscape.cmdline.internal;


import org.cytoscape.cmdline.launcher.CommandLineProvider;

import org.springframework.osgi.context.event.*;

public final class ParserStarter implements OsgiBundleApplicationContextListener {

	private TaskFactoryGrabber tfg; 
	private CommandLineProvider clp;

	public ParserStarter(TaskFactoryGrabber tfg, CommandLineProvider clp) {
		this.tfg = tfg;
		this.clp = clp;
	}

	public void onOsgiApplicationEvent(OsgiBundleApplicationContextEvent event) { 
		if ( event.getBundle().getSymbolicName().equals("org.cytoscape.core-task-impl") ) {
			CLTaskFactoryInterceptor cl = new CLTaskFactoryInterceptor(clp,tfg);	
		}
	}
}

