
package org.cytoscape.event.internal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleActivator;

import org.cytoscape.event.CyEventHelper;

import java.util.Properties;

class CyEventActivator implements BundleActivator {

	public void start(BundleContext bc) {
		CyEventHelper helper = new CyEventHelperImpl(bc);
		bc.registerService(	CyEventHelper.class.getName(), helper, new Properties() );
	}

	public void stop(BundleContext bc) {
		
	}
}
