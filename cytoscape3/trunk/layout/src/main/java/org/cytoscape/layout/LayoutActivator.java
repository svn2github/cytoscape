package org.cytoscape.layout;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;

public class LayoutActivator implements BundleActivator {

	public void start(BundleContext bc) {
		System.out.println("org.cytoscape.layout.LayoutActivator start");
		try {
		ServiceReference[] sr = bc.getServiceReferences(CyLayoutAlgorithm.class.getName(), null);
		if ( sr != null )
			for (ServiceReference r : sr ) {
				CyLayoutAlgorithm cla = (CyLayoutAlgorithm)bc.getService(r);
				String prefMenu = (String)r.getProperty("preferredMenu");
				if ( prefMenu == null || prefMenu.equals("") )
					prefMenu = "Cytoscape Layouts";
				CyLayouts.addLayout(cla, prefMenu);
			}
		} catch (Exception e) { e.printStackTrace(); }
	}

	public void stop(BundleContext bc) {
	}
}

