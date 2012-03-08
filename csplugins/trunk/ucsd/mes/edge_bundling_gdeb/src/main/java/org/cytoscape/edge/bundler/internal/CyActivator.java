package org.cytoscape.edge.bundler.internal;

import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.service.util.AbstractCyActivator;
import java.util.Properties;
import org.osgi.framework.BundleContext;


public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {

		EdgeBundlerTaskFactory edgeBundlerTaskFactory = new EdgeBundlerTaskFactory();
		
		Properties edgeBundlerTaskFactoryProps = new Properties();
		edgeBundlerTaskFactoryProps.setProperty("preferredMenu","Layout");
		edgeBundlerTaskFactoryProps.setProperty("menuGravity","11.0");
		edgeBundlerTaskFactoryProps.setProperty("title","Bundle Edges");
		registerService(bc,edgeBundlerTaskFactory,NetworkViewTaskFactory.class, edgeBundlerTaskFactoryProps);
	}
}

