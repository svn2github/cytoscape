package ${package}.internal;

import java.util.Properties;

import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.NetworkTaskFactory;
import org.osgi.framework.BundleContext;

public class CyActivator extends AbstractCyActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		SampleTaskFactory taskFactory = new SampleTaskFactory();
		
		Properties properties = new Properties();
		properties.put("title", "Sample Task");
		properties.put("preferredMenu", "Plugins");
		properties.put("enableFor", "network");
		
		registerService(context, taskFactory, NetworkTaskFactory.class, properties);
	}

}
