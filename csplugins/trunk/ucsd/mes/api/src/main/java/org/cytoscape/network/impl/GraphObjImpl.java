

package org.cytoscape.network.impl;

import org.cytoscape.network.GraphObject;

import org.cytoscape.attrs.CyAttributes;
import org.cytoscape.attrs.impl.CyAttributesImpl;

class GraphObjImpl implements GraphObject {

	private static int count = 0;

	private static synchronized int getNextSUID() {
		return count++;	
	}

	private final int suid;
	private final CyAttributes attrs;

	GraphObjImpl() {
		suid = getNextSUID();
		attrs = new CyAttributesImpl();
	}

	public int getSUID() {
		return suid;
	}

	public CyAttributes getAttributes() {
		return attrs;
	}
}
