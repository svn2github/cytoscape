package org.cytoscape.provenance.internal;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.io.File;
import java.net.URL;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableHandlerFactory;
import org.cytoscape.work.util.*;


public class ProvenanceHandlerFactory implements TunableHandlerFactory<ProvenanceHandler> {
	public ProvenanceHandler getHandler(final Field field, final Object instance, final Tunable tunable) {
		return new BasicProvenanceHandler(field, instance, tunable);
	}

	public ProvenanceHandler getHandler(final Method getter, final Method setter, final Object instance, final Tunable tunable) {
		return new BasicProvenanceHandler(getter, setter, instance, tunable);
	}
}
