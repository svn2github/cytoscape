
package org.cytoscape.provenance.internal;


import org.cytoscape.provenance.internal.ProvenanceHandlerFactory;
import org.cytoscape.provenance.internal.ProvenanceInterceptor;

import org.cytoscape.work.TunableHandlerFactory;
import org.cytoscape.work.TunableInterceptor;


import org.osgi.framework.BundleContext;

import org.cytoscape.service.util.AbstractCyActivator;

import java.util.Properties;

public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {
		
		ProvenanceInterceptor provenanceInterceptor = new ProvenanceInterceptor();
		ProvenanceHandlerFactory provenanceHandlerFactory = new ProvenanceHandlerFactory();
		
		registerService(bc,provenanceInterceptor,TunableInterceptor.class, new Properties());
		registerService(bc,provenanceHandlerFactory,TunableHandlerFactory.class, new Properties());

		registerServiceListener(bc,provenanceInterceptor,"addTunableHandlerFactory","removeTunableHandlerFactory",TunableHandlerFactory.class);
	}
}

