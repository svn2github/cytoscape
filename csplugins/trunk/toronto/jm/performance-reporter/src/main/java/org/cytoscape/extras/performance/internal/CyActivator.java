package org.cytoscape.extras.performance.internal;

import static org.cytoscape.work.ServiceProperties.COMMAND;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;

import java.util.Properties;

import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.work.TaskFactory;
import org.osgi.framework.BundleContext;

public class CyActivator extends AbstractCyActivator {
	public void start(BundleContext context) throws Exception {
		Properties properties = new Properties();
		properties.setProperty(PREFERRED_MENU,"Apps");
		properties.setProperty(TITLE,"Generate Performance Report...");
		properties.setProperty(COMMAND,"generate-performance-report");
		properties.setProperty(COMMAND_NAMESPACE,"system");
		registerService(context, new GeneratePerformanceReportTaskFactory(context), TaskFactory.class, properties);
	}
}
