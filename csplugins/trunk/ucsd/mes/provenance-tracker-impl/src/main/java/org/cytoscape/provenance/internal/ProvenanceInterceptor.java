package org.cytoscape.provenance.internal;

import java.util.*;

import javax.swing.JPanel;

import org.cytoscape.work.spring.SpringTunableInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProvenanceInterceptor extends SpringTunableInterceptor<ProvenanceHandler> {

	private static final Logger logger = LoggerFactory.getLogger(ProvenanceInterceptor.class);

	public ProvenanceInterceptor() {
		super(new ProvenanceHandlerFactory());
	}

	public boolean execUI(Object... pobjs) {
		return validateAndWriteBackTunables(pobjs);
	}
	
	public boolean validateAndWriteBackTunables(Object... pobjs) {
		Object[] objs = convertSpringProxyObjs(pobjs);
		for (final Object o : objs) {
			System.out.println("PROVENANCE: Execute task: " + o);
			if ( !handlerMap.containsKey( o ) ) {
				logger.warn("No Tunables exist for Object yet: " + o.toString());
				continue;
			}
			
			final Collection<ProvenanceHandler> handlers = handlerMap.get(o).values();
			
			for (final ProvenanceHandler p : handlers)
				p.record();
		}
		return true;
	}
}
