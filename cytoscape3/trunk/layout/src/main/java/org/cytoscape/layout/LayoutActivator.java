package org.cytoscape.layout;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import java.util.Hashtable;

public class LayoutActivator implements BundleActivator {

	public void start(BundleContext bc) {
		System.out.println("org.cytoscape.layout.LayoutActivator start");
		try {

			ServiceTracker trac = new ServiceTracker(bc, CyLayoutAlgorithm.class.getName(), null) {
				 @Override public Object addingService(ServiceReference r) {
					CyLayoutAlgorithm cla = (CyLayoutAlgorithm)super.addingService(r);
					String prefMenu = (String)r.getProperty("preferredMenu");
					if ( prefMenu == null || prefMenu.equals("") )
						prefMenu = "Cytoscape Layouts";
					CyLayouts.addLayout(cla, prefMenu);
					return cla;			 	
				 }
			};
			trac.open();

		} catch (Exception e) { e.printStackTrace(); }
	}

	public void stop(BundleContext bc) {
	}
}

