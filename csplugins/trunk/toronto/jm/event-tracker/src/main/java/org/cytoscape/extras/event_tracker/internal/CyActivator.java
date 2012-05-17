package org.cytoscape.extras.event_tracker.internal;

import java.util.Properties;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskFactory;
import org.osgi.framework.BundleContext;

public class CyActivator extends AbstractCyActivator {	
	public void start(BundleContext context) throws Exception {
		EventTracker tracker = new EventTracker(context);

		CySwingApplication application = getService(context, CySwingApplication.class);

		Properties properties = new Properties();
		properties.setProperty(ServiceProperties.TITLE, "Show Event Tracker");
		properties.setProperty(ServiceProperties.PREFERRED_MENU, "Apps");
		TaskFactory showEventTrackerTaskFactory = new ShowEventTrackerTaskFactory(application, tracker);
		registerService(context, showEventTrackerTaskFactory, TaskFactory.class, properties);
		
		tracker.start();
	}
}
