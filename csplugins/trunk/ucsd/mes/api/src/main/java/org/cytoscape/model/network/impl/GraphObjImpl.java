

package org.cytoscape.model.network.impl;

import org.cytoscape.model.network.GraphObject;

import org.cytoscape.model.attrs.CyAttributes;

class GraphObjImpl implements GraphObject {

	private final long suid;

	GraphObjImpl() {
		suid = IdFactory.getNextSUID();
	}

	public long getSUID() {
		return suid;
	}

	public CyAttributes getCyAttributes(String namespace) {
		return null;
	}
}
