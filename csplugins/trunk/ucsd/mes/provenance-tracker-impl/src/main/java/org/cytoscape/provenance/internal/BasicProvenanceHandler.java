package org.cytoscape.provenance.internal;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.cytoscape.work.AbstractTunableHandler;
import org.cytoscape.work.Tunable;


public class BasicProvenanceHandler extends AbstractTunableHandler implements ProvenanceHandler {

	public BasicProvenanceHandler(final Field field, final Object instance, final Tunable tunable) {
		super(field, instance, tunable);
	}

	public BasicProvenanceHandler(final Method getter, final Method setter, final Object instance, final Tunable tunable) {
		super(getter, setter, instance, tunable);
	}

	public void record() {
		Object o;
		try {
			o = getValue();
		} catch ( Exception e) {
			o = "<exception extracting value>";
		}
		System.out.println("PROVENANCE: Tunable --> " + getQualifiedName() + " = " + o);
	}
}
